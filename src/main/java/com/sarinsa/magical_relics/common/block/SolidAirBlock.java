package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class SolidAirBlock extends AirBlock {

    public SolidAirBlock(Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        List<Player> abovePlayers = level.getEntitiesOfClass(Player.class, new AABB(pos.above()));

        if (!abovePlayers.isEmpty()) {
            boolean presentAirSneaker = false;

            for (Player player : abovePlayers) {
                if (ArtifactUtils.hasAbility(player.getItemInHand(InteractionHand.MAIN_HAND), MRArtifactAbilities.AIR_SNEAK.get())) {
                    presentAirSneaker = true;
                    break;
                }
            }
            if (!presentAirSneaker)
                level.removeBlock(pos, false);
            else
                level.scheduleTick(pos, this, 40);
        }
        else {
            level.removeBlock(pos, false);
        }
    }
}
