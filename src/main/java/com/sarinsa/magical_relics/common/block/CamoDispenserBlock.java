package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.blockentity.CamoDispenserBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CamoDispenserBlock extends DispenserBlock implements EntityBlock, CamoBlock {

    /** The vanilla dispenser behavior registry is copied over to this one during {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent} */
    private static Map<Item, DispenseItemBehavior> DISPENSER_BEHAVIORS = new HashMap<>();


    public CamoDispenserBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 1.0F));

        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, false));
    }

    public static void setupBehaviors() {
        DISPENSER_BEHAVIORS = Map.copyOf(DispenserBlock.DISPENSER_REGISTRY);


    }

    @Override
    protected DispenseItemBehavior getDispenseMethod(ItemStack itemStack) {
        return DISPENSER_BEHAVIORS.get(itemStack.getItem());
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
