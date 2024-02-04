package com.sarinsa.magical_relics.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class BlockBucketItem extends Item {

    private final Supplier<? extends Block> placeBlock;

    public BlockBucketItem(Supplier<? extends Block> placeState, Properties properties) {
        super(properties);
        this.placeBlock = placeState;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        BlockPos placePos = hitResult.getBlockPos();

        if (!level.getBlockState(hitResult.getBlockPos()).getMaterial().isReplaceable()) {
            placePos = null;

            if (level.getBlockState(hitResult.getBlockPos().relative(hitResult.getDirection())).getMaterial().isReplaceable())
                placePos = hitResult.getBlockPos().relative(hitResult.getDirection());
        }

        if (placePos != null) {
            level.setBlock(placePos, placeBlock.get().defaultBlockState(), Block.UPDATE_CLIENTS);

            if (!player.isCreative())
                player.setItemInHand(hand, new ItemStack(Items.BUCKET));

            player.playSound(SoundEvents.PACKED_MUD_PLACE);
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        return InteractionResultHolder.pass(heldStack);
    }

    public Supplier<? extends Block> getPlaceBlock() {
        return placeBlock;
    }
}
