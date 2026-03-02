package com.sarinsa.magical_relics.common.worldgen.processor;

import com.mojang.serialization.Codec;
import com.sarinsa.magical_relics.common.blockentity.DisplayPedestalBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.core.registry.MRStructureProcessors;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Looks for empty Display Pedestals and either puts random artifacts in them
 * or a "Wizard's Favorite" item, depending on processor config.
 */
public class DisplayPedestalProcessor extends StructureProcessor {
    
    public static final Codec<DisplayPedestalProcessor> CODEC = Codec.FLOAT.fieldOf( "legendary_chance" )
            .xmap( DisplayPedestalProcessor::new, ( processor ) -> processor.legendaryChance )
            .codec();
    
    private static final List<Item> WIZARD_FAVORITES = new ArrayList<>();
    
    private final float legendaryChance;
    
    
    public DisplayPedestalProcessor( float legendaryChance ) {
        this.legendaryChance = legendaryChance;
    }
    
    
    @Nullable
    public StructureTemplate.StructureBlockInfo process( LevelReader level, BlockPos pos, BlockPos pos2, StructureTemplate.StructureBlockInfo info, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings structureSettings, @Nullable StructureTemplate template ) {
        RandomSource random = structureSettings.getRandom( blockInfo.pos() );
        BlockState state = blockInfo.state();
        BlockPos blockpos = blockInfo.pos();
        
        boolean isDisplayPedestal = state.is( MRBlocks.DISPLAY_PEDESTAL.get() );
        CompoundTag tag = blockInfo.nbt();
        
        if( isDisplayPedestal ) {
            if( tag == null ) tag = new CompoundTag();
            
            if( tag.contains( DisplayPedestalBlockEntity.WIZARDS_FAVORITE_KEY, Tag.TAG_BYTE ) && tag.getBoolean( DisplayPedestalBlockEntity.WIZARDS_FAVORITE_KEY ) ) {
                CompoundTag itemStackTag = new CompoundTag();
                ItemStack itemStack = getRandomItem( random );
                itemStack.save( itemStackTag );
                tag.put( DisplayPedestalBlockEntity.ITEM_KEY, itemStackTag );
            }
            else {
                CompoundTag itemStackTag = new CompoundTag();
                ItemStack itemStack = ArtifactUtils.generateRandomArtifact( level, random, random.nextFloat() < legendaryChance );
                itemStack.save( itemStackTag );
                
                tag.put( DisplayPedestalBlockEntity.ITEM_KEY, itemStackTag );
            }
        }
        return isDisplayPedestal ? new StructureTemplate.StructureBlockInfo( blockpos, state, tag ) : blockInfo;
    }
    
    @Override
    protected StructureProcessorType<?> getType() {
        return MRStructureProcessors.DISPLAY_PEDESTAL.get();
    }
    
    private static ItemStack getRandomItem( RandomSource random ) {
        return new ItemStack( WIZARD_FAVORITES.get( random.nextInt( WIZARD_FAVORITES.size() ) ) );
    }
    
    public static void refreshWizFavorites( Set<Item> blacklistedItems ) {
        WIZARD_FAVORITES.clear();
        WIZARD_FAVORITES.addAll( ForgeRegistries.ITEMS.getValues() );
        WIZARD_FAVORITES.removeAll( blacklistedItems );
    }
}
