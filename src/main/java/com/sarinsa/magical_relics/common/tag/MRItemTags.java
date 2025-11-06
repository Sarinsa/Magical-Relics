package com.sarinsa.magical_relics.common.tag;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class MRItemTags {
    
    
    public static final TagKey<Item> ARTIFACTS = modTag( "artifacts" );
    public static final TagKey<Item> ARTIFACT_CURIOS = modTag( "artifact_curios" );
    
    
    private static TagKey<Item> modTag( String name ) {
        return ItemTags.create( MagicalRelics.rl( name ) );
    }
    
    private static TagKey<Item> forgeTag( String name ) {
        return ItemTags.create( ResourceLocation.fromNamespaceAndPath( "forge", name ) );
    }
    
    public static void init() { }
    
    private MRItemTags() { }
}
