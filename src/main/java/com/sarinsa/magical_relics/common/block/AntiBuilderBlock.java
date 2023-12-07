package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class AntiBuilderBlock extends Block implements EntityBlock {

    public AntiBuilderBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
                .strength(1.0F, 3600000.0F)
                .sound(SoundType.STONE)
                .destroyTime(0.5F)
                .lightLevel((state) -> 8)
                .noLootTable());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AntiBuilderBlockEntity(pos, state);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid)) {
            level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, ((float) level.random.nextDouble() / 2) + 0.75F);
            return true;
        }
        return false;
    }
}
