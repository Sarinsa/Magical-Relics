package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.util.References;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;

// TODO - Make creative mode GUI to specify effective area size
public class AntiBuilderBlockEntity extends BlockEntity {

    private AABB effectiveArea = null;
    private boolean registeredListener = false;


    public AntiBuilderBlockEntity(BlockPos pos, BlockState state) {
        super(MRBlockEntities.ANTI_BUILDER.get(), pos, state);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void setLevel(Level level) {
        this.level = level;

        if (level != null && !registeredListener) {
            MinecraftForge.EVENT_BUS.register(this);
            registeredListener = true;

            // Default box
            setEffectiveArea(new AABB(
                    getBlockPos().offset(-20, -20, -20),
                    getBlockPos().offset(20, 20, 20)
            ));
        }
    }

    @Nullable
    public AABB getEffectiveArea() {
        return effectiveArea;
    }


    public void setEffectiveArea(AABB area) {
        this.effectiveArea = area;
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();

        if (registeredListener)
            MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (registeredListener)
            MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        saveBoundData(compoundTag);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        readBoundData(compoundTag);
    }

    private void saveBoundData(CompoundTag tag) {
        if (getEffectiveArea() != null) {
            tag.putIntArray("effectiveAreaBounds", new int[] {
                    (int) effectiveArea.minX, (int) effectiveArea.minY, (int) effectiveArea.minZ,
                    (int) effectiveArea.maxX, (int) effectiveArea.maxY, (int) effectiveArea.maxZ
            });
        }
    }

    private void readBoundData(CompoundTag tag) {
        if (tag.contains("effectiveAreaBounds", Tag.TAG_INT_ARRAY)) {
            try {
                int[] bounds = tag.getIntArray("effectiveAreaBounds");

                if (bounds.length == 6) {
                    setEffectiveArea(new AABB(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]));
                }
            }
            catch (Exception ignored) {

            }
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = new CompoundTag();
        saveBoundData(updateTag);
        return updateTag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        readBoundData(tag);
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().isCreative() || effectiveArea == null || event.getLevel() != level)
            return;

        Item item = event.getItemStack().getItem();

        if (item == MRBlocks.ANTI_BUILDER.get().asItem() || item == Blocks.AIR.asItem())
            return;

        BlockPos pos = event.getHitVec().getBlockPos();

        if (effectiveArea.contains(pos.getX(), pos.getY(), pos.getZ())) {
            event.setUseItem(Event.Result.DENY);
            event.getEntity().displayClientMessage(Component.translatable(References.ANTI_BUILDER_MESSAGE), true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity().isCreative() || effectiveArea == null || event.getLevel() != level)
            return;

        BlockState blockState = event.getLevel().getBlockState(event.getPos());

        if (blockState.getBlock() == MRBlocks.ANTI_BUILDER.get() || blockState.isAir())
            return;

        BlockPos pos = event.getPos();

        if (effectiveArea.contains(pos.getX(), pos.getY(), pos.getZ())) {
            event.setUseItem(Event.Result.DENY);
            event.getEntity().displayClientMessage(Component.translatable(References.ANTI_BUILDER_MESSAGE), true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (effectiveArea == null || event.getLevel() != level)
            return;

        if (event.getState().getBlock() == MRBlocks.ANTI_BUILDER.get())
            return;

        checkAndCancelPlayer(event, event.getPos(), event.getPlayer());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onExplodeEvent(ExplosionEvent.Detonate event) {
        if (effectiveArea == null || event.getLevel() != level)
            return;

        Vec3 pos = event.getExplosion().getPosition();

        if (effectiveArea.contains(pos.x(), pos.y(), pos.z())) {
            event.getAffectedBlocks().clear();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMobGrief(EntityMobGriefingEvent event) {
        if (effectiveArea == null || event.getEntity().getLevel() != level)
            return;

        checkAndCancel(event, event.getEntity().blockPosition());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (effectiveArea == null || event.getLevel() != level)
            return;

        if (event.getState().getBlock() == MRBlocks.ANTI_BUILDER.get())
            return;

        checkAndCancelPlayer(event, event.getPos(), event.getEntity() instanceof Player player ? player : null);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        if (effectiveArea == null || event.getLevel() != level)
            return;

        checkAndCancelPlayer(event, event.getPos(), event.getEntity() instanceof Player player ? player : null);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event) {
        if (effectiveArea == null || event.getLevel() != level)
            return;

        checkAndCancel(event, event.getPos());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPortalSpawn(BlockEvent.PortalSpawnEvent event) {
        if (effectiveArea == null || event.getLevel() != level)
            return;

        checkAndCancel(event, event.getPos());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (effectiveArea == null || event.getLevel() != level)
            return;

        checkAndCancelPlayer(event, event.getPos(), event.getPlayer());
    }


    private void checkAndCancel(Event event, BlockPos pos) {
        if (effectiveArea.contains(pos.getX(), pos.getY(), pos.getZ())) {
            event.setCanceled(true);
        }
    }

    private void checkAndCancelPlayer(Event event, BlockPos pos, @Nullable Player player) {
        if (player == null || player.isCreative())
            return;

        if (effectiveArea.contains(pos.getX(), pos.getY(), pos.getZ())) {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable(References.ANTI_BUILDER_MESSAGE), true);
        }
    }
}
