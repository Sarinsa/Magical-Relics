package com.sarinsa.magical_relics.client;

import com.mojang.datafixers.util.Either;
import com.sarinsa.magical_relics.common.artifact.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ClientEventListener {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderItemTooltip(RenderTooltipEvent.GatherComponents event) {
        ItemStack itemStack = event.getItemStack();
        List<BaseArtifactAbility> abilities = ArtifactUtils.getAllAbilities(itemStack);

        if (!abilities.isEmpty()) {
            for (BaseArtifactAbility ability : abilities) {
                Component description = ability.getAbilityDescription();

                if (description != null)
                    event.getTooltipElements().add(Either.left(description));
            }
        }
    }
}
