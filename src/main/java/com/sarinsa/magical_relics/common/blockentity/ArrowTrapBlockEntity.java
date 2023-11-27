package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ArrowTrapBlockEntity extends BaseCamoBlockEntity {

    public ArrowTrapBlockEntity(BlockPos pos, BlockState state) {
        super(MRBlockEntities.ARROW_TRAP.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ArrowTrapBlockEntity arrowTrap) {

    }

}
