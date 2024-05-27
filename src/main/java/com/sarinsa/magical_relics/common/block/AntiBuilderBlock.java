package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import com.sarinsa.magical_relics.common.network.NetworkHelper;
import com.sarinsa.magical_relics.common.network.message.S2COpenBEScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AntiBuilderBlock extends Block implements EntityBlock {

    public AntiBuilderBlock() {
        super(BlockBehaviour.Properties.of()
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

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getExistingBlockEntity(pos);

        if (blockEntity instanceof AntiBuilderBlockEntity) {
            if (!level.isClientSide && player.isCreative()) {
                NetworkHelper.sendOpenBEScreen((ServerPlayer) player, pos, S2COpenBEScreen.ALTERATION_NEGATOR_ID);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
