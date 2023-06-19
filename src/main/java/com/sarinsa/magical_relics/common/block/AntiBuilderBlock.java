package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

public class AntiBuilderBlock extends Block implements EntityBlock {

    public AntiBuilderBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
                .strength(1.0F, 1000.0F)
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
}
