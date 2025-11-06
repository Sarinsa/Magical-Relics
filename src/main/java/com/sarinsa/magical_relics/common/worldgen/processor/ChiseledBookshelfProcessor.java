package com.sarinsa.magical_relics.common.worldgen.processor;

import com.mojang.serialization.Codec;
import com.sarinsa.magical_relics.common.core.registry.MRStructureProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

/**
 * Looks for Chiseled Bookshelves and fills them with normal and enchanted books.
 */
public class ChiseledBookshelfProcessor extends StructureProcessor {
    
    public static final Codec<ChiseledBookshelfProcessor> CODEC = Codec.FLOAT.fieldOf( "enchanted_chance" )
            .xmap( ChiseledBookshelfProcessor::new, ( processor ) -> processor.enchantedChance )
            .codec();
    
    
    private final float enchantedChance;
    
    public ChiseledBookshelfProcessor( float enchantedChance ) {
        this.enchantedChance = enchantedChance;
    }
    
    
    @Nullable
    public StructureTemplate.StructureBlockInfo process( LevelReader level, BlockPos pos, BlockPos p_74142_, StructureTemplate.StructureBlockInfo info, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings structureSettings, @Nullable StructureTemplate template ) {
        RandomSource random = structureSettings.getRandom( blockInfo.pos() );
        BlockState state = blockInfo.state();
        BlockPos blockpos = blockInfo.pos();
        
        boolean isChiseledBookshelf = state.is( Blocks.CHISELED_BOOKSHELF );
        CompoundTag tag = blockInfo.nbt();
        
        if( isChiseledBookshelf ) {
            if( tag == null ) tag = new CompoundTag();
            
            NonNullList<ItemStack> books = NonNullList.withSize( 6, ItemStack.EMPTY );
            
            for( int i = 0; i < books.size(); i++ ) {
                ItemStack book = new ItemStack( Items.BOOK );
                
                if( random.nextFloat() < enchantedChance ) {
                    book = EnchantmentHelper.enchantItem( random, book, random.nextInt( 26 ) + 5, false );
                }
                books.set( i, book );
            }
            ContainerHelper.saveAllItems( tag, books );
        }
        return isChiseledBookshelf ? new StructureTemplate.StructureBlockInfo( blockpos, state, tag ) : blockInfo;
    }
    
    @Override
    protected StructureProcessorType<?> getType() {
        return MRStructureProcessors.CHISELED_BOOKSHELF.get();
    }
}
