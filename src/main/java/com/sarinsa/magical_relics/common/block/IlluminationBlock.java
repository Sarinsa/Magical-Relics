package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class IlluminationBlock extends Block {

    public IlluminationBlock() {
        super(BlockBehaviour.Properties.of()
                .lightLevel((state) -> 15)
                .replaceable()
                .noCollission()
                .noOcclusion()
                .noParticlesOnBreak()
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    /**
     * Remove this block if no players with the Illumination ability is nearby.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(6.0F));

        if (nearbyPlayers.isEmpty()) {
            showExtinguishEffect(level, pos, random);
            level.removeBlock(pos, false);
            return;
        }

        for (Player player : nearbyPlayers) {
            if (ArtifactUtils.hasAbility(player.getItemInHand(InteractionHand.MAIN_HAND), MRArtifactAbilities.ILLUMINATION.get())
                    || ArtifactUtils.hasAbility(player.getItemBySlot(EquipmentSlot.HEAD), MRArtifactAbilities.ILLUMINATION.get())) {

                return;
            }
        }
        showExtinguishEffect(level, pos, random);
        level.removeBlock(pos, false);
    }

    private void showExtinguishEffect(ServerLevel level, BlockPos pos, RandomSource random) {
        level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 10, 0.0D, random.nextDouble() * 0.2, 0.0D, 0.01D);
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
    }

    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double y = pos.getY() + 0.5D;

        for (int i = 0; i < 3; i++) {
            double x = (pos.getX() + 0.5D) + random.nextGaussian() / 2;
            double z = (pos.getZ() + 0.5D) + random.nextGaussian() / 2;

            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0D, random.nextDouble() * 0.1, 0.0D);
        }
    }
}
