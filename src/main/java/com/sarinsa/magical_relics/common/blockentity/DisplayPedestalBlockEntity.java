package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.block.DisplayPedestalBlock;
import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DisplayPedestalBlockEntity extends BlockEntity {
    
    /** Various NBT keys. */
    public static final String KEY_GENERATE_ARTIFACT = "GenerateArtifact";
    public static final String KEY_ITEM = "ArtifactItem";
    public static final String KEY_LOCKED = "Locked";
    public static final String KEY_WIZARDS_FAVORITE = "WizardsFavorite";
    
    /** The display pedestal's stored item stack. */
    private ItemStack artifact;
    /**
     * True if this display pedestal should
     * pick a random display item (Wizard's Favorite)
     * from the item registry.
     *
     * @see com.sarinsa.magical_relics.common.core.config.MainConfig.Misc#wizardFavoriteBlacklist
     */
    private boolean generateWizFavorite = false;
    
    
    public DisplayPedestalBlockEntity( BlockPos pos, BlockState state ) {
        super( MRBlockEntities.DISPLAY_PEDESTAL.get(), pos, state );
    }
    
    /** @return This display pedestal's item stack. */
    public ItemStack getItemStack() {
        return artifact == null ? ItemStack.EMPTY : artifact;
    }
    
    /** Sets this display pedestal's item stack. */
    public void setItemStack( ItemStack itemStack ) {
        artifact = itemStack;
        setChanged();
    }
    
    @Override
    protected void saveAdditional( CompoundTag saveTag ) {
        super.saveAdditional( saveTag );
        writeUpdateData( saveTag );
        
        saveTag.remove( KEY_GENERATE_ARTIFACT );
        saveTag.putBoolean( KEY_WIZARDS_FAVORITE, generateWizFavorite );
        
        if( getBlockState().is( MRBlocks.DISPLAY_PEDESTAL.get() ) ) {
            saveTag.putBoolean( KEY_LOCKED, getBlockState().getValue( DisplayPedestalBlock.LOCKED ) );
        }
    }
    
    @SuppressWarnings( "ConstantConditions" )
    @Override
    public void load( CompoundTag saveTag ) {
        super.load( saveTag );
        readDisplayItem( saveTag );
        
        if( NBTHelper.containsNumber( saveTag, KEY_GENERATE_ARTIFACT ) ) {
            if( saveTag.getBoolean( KEY_GENERATE_ARTIFACT ) && level != null && !level.isClientSide ) {
                setItemStack( ArtifactUtils.generateRandomArtifact( level, level.random, false ) );
            }
        }
        if( NBTHelper.containsNumber( saveTag, KEY_WIZARDS_FAVORITE ) ) {
            generateWizFavorite = saveTag.getBoolean( KEY_WIZARDS_FAVORITE );
        }
        if( hasLevel() && NBTHelper.containsNumber( saveTag, KEY_LOCKED ) ) {
            level.setBlock( getBlockPos(), getBlockState().setValue( DisplayPedestalBlock.LOCKED, saveTag.getBoolean( KEY_LOCKED ) ), Block.UPDATE_CLIENTS );
        }
    }
    
    /** Convenience method for reading display item from NBT. */
    private void readDisplayItem( CompoundTag compoundTag ) {
        if( NBTHelper.containsCompound( compoundTag, KEY_ITEM ) ) {
            artifact = ItemStack.of( compoundTag.getCompound( KEY_ITEM ) );
        }
    }
    
    /** Convenience method for writing update packet data. */
    private void writeUpdateData( CompoundTag compoundTag ) {
        compoundTag.put( KEY_ITEM, getItemStack().save( new CompoundTag() ) );
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        final CompoundTag compoundTag = new CompoundTag();
        writeUpdateData( compoundTag );
        return compoundTag;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create( this );
    }
    
    @Override
    public void handleUpdateTag( CompoundTag tag ) {
        super.handleUpdateTag( tag );
    }
    
    @SuppressWarnings( "ConstantConditions" )
    @Override
    public void onDataPacket( Connection net, ClientboundBlockEntityDataPacket pkt ) {
        if( level.isClientSide ) {
            super.onDataPacket( net, pkt );
            
            final CompoundTag compoundTag = pkt.getTag();
            
            if( compoundTag == null )
                return;
            
            readDisplayItem( compoundTag );
        }
    }
}
