package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.util.References;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// TODO - Save bounding box to NBT
//      - Make sure we cancel item use event in case of block items
//      - Make creative mode GUI to specify effective area size
public class AntiBuilderBlockEntity extends BlockEntity {

    private AABB effectiveArea = null;
    private boolean registeredListener = false;


    public AntiBuilderBlockEntity(BlockPos pos, BlockState state) {
        super(MRBlockEntities.ANTI_BUILDER.get(), pos, state);
    }

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() == MRBlocks.ANTI_BUILDER.get())
            return;

        checkAndCancelPlayer(event, event.getPos(), event.getPlayer());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onExplodeEvent(ExplosionEvent.Detonate event) {
        Vec3 pos = event.getExplosion().getPosition();

        if (effectiveArea.contains(pos.x(), pos.y(), pos.z())) {
            event.getAffectedBlocks().clear();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMobGrief(EntityMobGriefingEvent event) {
        checkAndCancel(event, event.getEntity().blockPosition());
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerUse(LivingEntityUseItemEvent event) {
        Item item = event.getItem().getItem();

        if (item == MRBlocks.ANTI_BUILDER.get().asItem())
            return;

        if (item instanceof BlockItem) {
            checkAndCancelPlayer(event, event.getEntity().getOnPos(), event.getEntity() instanceof Player player ? player : null);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getState().getBlock() == MRBlocks.ANTI_BUILDER.get())
            return;

        checkAndCancelPlayer(event, event.getPos(), event.getEntity() instanceof Player player ? player : null);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        checkAndCancelPlayer(event, event.getPos(), event.getEntity() instanceof Player player ? player : null);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event) {
        checkAndCancel(event, event.getPos());
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPortalSpawn(BlockEvent.PortalSpawnEvent event) {
        checkAndCancel(event, event.getPos());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        checkAndCancelPlayer(event, event.getPos(), event.getPlayer());
    }


    private boolean checkAndCancel(Event event, BlockPos pos) {
        if (effectiveArea.contains(pos.getX(), pos.getY(), pos.getZ())) {
            event.setCanceled(true);
            return true;
        }
        return false;
    }

    private void checkAndCancelPlayer(Event event, BlockPos pos, Player player) {
        if (player == null || player.isCreative())
            return;

        if (effectiveArea.contains(pos.getX(), pos.getY(), pos.getZ())) {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable(References.ANTI_BUILDER_MESSAGE), true);
        }
    }
}
