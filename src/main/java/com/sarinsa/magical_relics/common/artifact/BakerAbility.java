package com.sarinsa.magical_relics.common.artifact;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BakerAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("baker", "bakers"),
            createPrefix("baker", "confectioners")
    };

    private static final String[] SUFFIXES = {
            createSuffix("baker", "baking"),
            createSuffix("baker", "frosting"),
            createSuffix("baker", "tastiness"),
            createSuffix("baker", "delight"),
    };


    public BakerAbility() {
        super("baker");
    }

    @Override
    public boolean onClickBlock(Level level, BlockPos pos, BlockState state, Direction face, Player player) {
        if (face != Direction.UP)
            return false;

        BlockPos toPlacePos = pos.relative(face);
        BlockState toPlaceState = level.getBlockState(toPlacePos);

        if (toPlaceState.isAir() && Blocks.CAKE.canSurvive(toPlaceState, level, toPlacePos)) {
            level.setBlock(toPlacePos, Blocks.CAKE.defaultBlockState(), Block.UPDATE_ALL);

            if (!level.isClientSide) {
                level.playSound(null, toPlacePos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
                double x = toPlacePos.getX() + 0.5D;
                double y = toPlacePos.getY() + 0.4D;
                double z = toPlacePos.getZ() + 0.5D;
                double xSpeed = level.random.nextGaussian() * 0.02D;
                double ySpeed = level.random.nextGaussian() * 0.02D;
                double zSpeed = level.random.nextGaussian() * 0.02D;
                ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, x, y, z, 5, xSpeed, ySpeed, zSpeed, 0.05D);;
            }
            return true;
        }
        return false;
    }

    @Override
    public String[] getPrefixes() {
        return PREFIXES;
    }

    @Override
    public String[] getSuffixes() {
        return SUFFIXES;
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.MAIN_HAND;
    }
}
