package com.sarinsa.magical_relics.common.blockentity;

import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Interface for block entities that should be able to
 * disguise themselves as a solid, full-cube model block.
 */
public interface CamoBlockEntity {
    
    String TAG_CAMO_STATE = "CamoState";
    
    Supplier<BlockState> defaultCamoState = Blocks.STONE_BRICKS::defaultBlockState;
    
    /** @return The camo state of this camo-block entity. */
    @Nullable
    BlockState getCamoState();
    
    /** Sets the camo state for this camo-block entity. */
    void setCamoState( @Nullable BlockState state );
    
    /** Attempts to read camo block state from the given compound tag. */
    @Nullable
    default BlockState readCamoState( CompoundTag compoundTag ) {
        if( NBTHelper.containsCompound( compoundTag, TAG_CAMO_STATE ) ) {
            BlockState camo = NBTHelper.readBlockState( compoundTag.getCompound( TAG_CAMO_STATE ) );
            return camo == Blocks.AIR.defaultBlockState() ? defaultCamoState.get() : camo;
        }
        return null;
    }
    
    default void writeUpdateData( CompoundTag compoundTag, @Nullable BlockState camoState ) {
        if( camoState != null ) {
            compoundTag.put( TAG_CAMO_STATE, NBTHelper.writeBlockState( camoState ) );
        }
    }
}
