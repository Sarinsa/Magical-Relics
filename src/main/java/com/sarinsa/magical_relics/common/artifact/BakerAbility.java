package com.sarinsa.magical_relics.common.artifact;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BakerAbility extends BaseArtifactAbility {

    public BakerAbility() {
        super("baker");
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.MAIN_HAND;
    }

    @Override
    public boolean onClickBlock(Level level, BlockPos pos, BlockState state, Direction face, Player player) {
        BlockPos toPlacePos = pos.relative(face);
        BlockState toPlaceState = level.getBlockState(toPlacePos);

        if (toPlaceState.isAir() && Blocks.CAKE.canSurvive(toPlaceState, level, toPlacePos)) {
            level.setBlock(toPlacePos, Blocks.CAKE.defaultBlockState(), Block.UPDATE_ALL);

            if (!level.isClientSide) {
                level.playSound(null, toPlacePos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
            }
            return true;
        }
        return false;
    }
}
