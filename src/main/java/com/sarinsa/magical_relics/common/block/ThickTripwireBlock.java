package com.sarinsa.magical_relics.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Functions identically to normal tripwire except
 * Item entities cannot trigger this tripwire.
 */
public class ThickTripwireBlock extends TripWireBlock {

    public ThickTripwireBlock(TripWireHookBlock hookBlock, Properties properties) {
        super(hookBlock, properties);
    }

    @Override
    public void checkPressed(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        boolean powered = state.getValue(POWERED);
        boolean entityColliding = false;
        List<? extends Entity> list = level.getEntities(null, state.getShape(level, pos).bounds().move(pos));

        if (!list.isEmpty()) {
            for(Entity entity : list) {
                if (!entity.isIgnoringBlockTriggers() && !(entity instanceof ItemEntity)) {
                    entityColliding = true;
                    break;
                }
            }
        }

        if (entityColliding != powered) {
            state = state.setValue(POWERED, entityColliding);
            level.setBlock(pos, state, Block.UPDATE_ALL);
            updateSource(level, pos, state);
        }

        if (entityColliding) {
            level.scheduleTick(new BlockPos(pos), this, 10);
        }
    }
}
