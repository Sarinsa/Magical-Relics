package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.blockentity.CamoDispenserBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import com.sarinsa.magical_relics.common.entity.SwungSword;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class CamoDispenserBlock extends DispenserBlock implements EntityBlock, CamoBlock {

    /** The vanilla dispenser behavior registry is copied over to this one during {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent} */
    private static Object2ObjectOpenHashMap<Item, DispenseItemBehavior> DISPENSER_BEHAVIORS;


    public CamoDispenserBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 1.0F));

        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, false));
    }

    public static void setupBehaviors() {
        // Copy vanilla behavior first
        DISPENSER_BEHAVIORS = new Object2ObjectOpenHashMap<>(DispenserBlock.DISPENSER_REGISTRY);
        DISPENSER_BEHAVIORS.defaultReturnValue(new DefaultDispenseItemBehavior());

        // Swung Sword behavior
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (item instanceof SwordItem swordItem) {
                DISPENSER_BEHAVIORS.put(item, new OptionalDispenseItemBehavior() {
                    protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                        Level level = blockSource.getLevel();
                        setSuccess(true);
                        Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
                        BlockPos blockPos = blockSource.getPos().relative(direction);
                        BlockState state = level.getBlockState(blockPos);

                        if (state.isFaceSturdy(level, blockPos, direction.getOpposite()) || !level.getEntitiesOfClass(SwungSword.class, new AABB(blockPos)).isEmpty()) {
                            setSuccess(false);
                            return itemStack;
                        }
                        SwungSword sword = new SwungSword(level, blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D);
                        sword.setSwordItem(swordItem);
                        sword.setAttackDirection(direction);
                        level.addFreshEntity(sword);

                        if (itemStack.hurt(1, level.random, null)) {
                            itemStack.setCount(0);
                        }
                        return itemStack;
                    }
                });
            }
        }
    }

    @Override
    protected DispenseItemBehavior getDispenseMethod(ItemStack itemStack) {
        return DISPENSER_BEHAVIORS.get(itemStack.getItem());
    }

    @Override
    protected void dispenseFrom(ServerLevel serverLevel, BlockPos pos) {
        BlockSourceImpl blockSource = new BlockSourceImpl(serverLevel, pos);
        DispenserBlockEntity dispenser = blockSource.getEntity();
        int slot = dispenser.getRandomSlot(serverLevel.random);

        if (slot < 0) {
            serverLevel.levelEvent(1001, pos, 0);
            serverLevel.gameEvent(null, GameEvent.DISPENSE_FAIL, pos);
        }
        else {
            ItemStack itemStack = dispenser.getItem(slot);
            DispenseItemBehavior behavior = DISPENSER_BEHAVIORS.get(itemStack.getItem());

            try {
                if (behavior != DispenseItemBehavior.NOOP) {
                    dispenser.setItem(slot, behavior.dispense(blockSource, itemStack));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return getLightEmission(level, pos, super.getLightEmission(state, level, pos));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return use(level, pos, player, hand, null, (player1, menuProvider) -> {
            player.openMenu(menuProvider);
            player.awardStat(Stats.INSPECT_DISPENSER);
        });
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        CamoDispenserBlockEntity arrowTrap = new CamoDispenserBlockEntity(pos, state);
        arrowTrap.setCamoState(Blocks.STONE_BRICKS.defaultBlockState());
        return arrowTrap;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == MRBlockEntities.CAMO_DISPENSER.get()
                ? (_level, _pos, _state, _blockEntity) -> CamoDispenserBlockEntity.tick(_level, _pos, _state, (CamoDispenserBlockEntity) _blockEntity)
                : null;
    }
}
