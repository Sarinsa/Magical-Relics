package com.sarinsa.magical_relics.common.item;

import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RandomArtifactItem extends Item {

    public RandomArtifactItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            if (!player.isCreative())
                player.setItemInHand(hand, ItemStack.EMPTY);

            ItemStack randomArtifact = ArtifactUtils.generateRandomArtifact(level.random);
            player.getInventory().add(randomArtifact);
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
}
