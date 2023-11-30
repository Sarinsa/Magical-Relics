package com.sarinsa.magical_relics.common.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class CrumblingBlock extends Block {

    public static final BooleanProperty CRUMBLING = BooleanProperty.create("crumbling");


    public CrumblingBlock(Properties properties) {
        super(properties.noLootTable());
        registerDefaultState(stateDefinition.any().setValue(CRUMBLING, false));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!state.getValue(CRUMBLING) && !entity.isSteppingCarefully() && entity instanceof LivingEntity) {
            crumble(level, pos, state);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hitResult, Projectile projectile) {
        BlockPos pos = hitResult.getBlockPos();

        if (!state.getValue(CRUMBLING)) {
            crumble(level, pos, state);
        }
    }

    private void crumble(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(CRUMBLING, true), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.3F, 0.8F);
        level.scheduleTick(pos, this, level.random.nextInt(15) + 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(CRUMBLING)) {
            crumbleBreak(level, state, pos, random);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(CRUMBLING));
    }

    private static void crumbleBreak(ServerLevel level, BlockState state, BlockPos pos, RandomSource random) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.9F, 1.0F - (float) (random.nextInt(10) / 10));
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(pos), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 20, 0.0D, 0.0D, 0.0D, 0.15F);
    }
}
