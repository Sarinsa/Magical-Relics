package com.sarinsa.magical_relics.common.event;

import com.sarinsa.magical_relics.common.artifact.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MREventListener {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack heldItem = event.getItemStack();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState clickedState = level.getBlockState(pos);
        Player player = event.getEntity();
        BaseArtifactAbility ability = ArtifactUtils.getFirstAbility(BaseArtifactAbility.TriggerType.RIGHT_CLICK, heldItem);

        // Help prevent stupid things from happening
        // when holding a potentially dangerous artifact
        // when trying to interact with a block entity.
        if (clickedState.hasBlockEntity()) return;

        if (ability != null) {
            if (ability.onClickBlock(level, pos, level.getBlockState(pos), event.getFace(), player) && heldItem.isDamageableItem()) {
                heldItem.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(event.getHand()));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDropItem(ItemTossEvent event) {
        ItemEntity tossedItem = event.getEntity();
        Level level = event.getEntity().level;
        Player player = event.getPlayer();
        BaseArtifactAbility ability = ArtifactUtils.getFirstAbility(BaseArtifactAbility.TriggerType.DROPPED, tossedItem.getItem());

        if (ability != null) {
            if (ability.onDropped(level, tossedItem, player) && tossedItem.getItem().isDamageableItem()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        Level level = player.getLevel();
        BaseArtifactAbility ability = ArtifactUtils.getFirstAbility(BaseArtifactAbility.TriggerType.HELD, heldItem);

        if (ability != null) {
            if (ability.onHeld(level, player, heldItem)) {
                heldItem.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
        }
    }
}
