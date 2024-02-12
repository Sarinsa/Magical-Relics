package com.sarinsa.magical_relics.common.event;

import com.sarinsa.magical_relics.common.artifact.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MREventListener {

    private static int timeNextServerTick = 0;
    private static final int serverTickDelay = 10;


    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ++timeNextServerTick;

            if (timeNextServerTick >= serverTickDelay) {
                timeNextServerTick = 0;

                for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
                    try {
                        ArtifactUtils.tickAbilityCooldowns(player, serverTickDelay);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDropItem(ItemTossEvent event) {
        ItemEntity tossedItem = event.getEntity();
        Level level = event.getEntity().level;
        Player player = event.getPlayer();
        BaseArtifactAbility ability = ArtifactUtils.getAbilityWithTrigger(BaseArtifactAbility.TriggerType.DROPPED, tossedItem.getItem());

        if (ability != null) {
            if (ability.onDropped(level, tossedItem, player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        Level level = player.getLevel();
        BaseArtifactAbility ability = ArtifactUtils.getAbilityWithTrigger(BaseArtifactAbility.TriggerType.HELD, heldItem);

        if (ability != null) {
            if (ability.onHeld(level, player, heldItem)) {
                heldItem.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
        }
    }
}
