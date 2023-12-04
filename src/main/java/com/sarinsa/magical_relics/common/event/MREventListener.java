package com.sarinsa.magical_relics.common.event;

import com.sarinsa.magical_relics.common.artifact.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
        BaseArtifactAbility ability = ArtifactUtils.getFirstAbility(BaseArtifactAbility.TriggerType.MAIN_HAND, heldItem);

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
}
