package com.sarinsa.magical_relics.common.ability.base;

import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record AttributeBoost(Supplier<Attribute> attribute, String name, AttributeModifier.Operation operation,
                             RangedValueProvider valueProvider, ActiveType activeType) {
    
    public static final String TAG_ATTRIBUTE_MODS = ArtifactUtils.TAG_ATTRIBUTE_MODS;
    
    
    /**
     * Writes this {@link AttributeBoost} to the given compound tag,
     * with a random sample from its value provider.
     */
    public void writeToNbt( CompoundTag tag, RandomSource random ) {
        // noinspection ConstantConditions
        final String attributeId = ForgeRegistries.ATTRIBUTES.getKey( attribute().get() ).toString();
        final CompoundTag attributeMod = new CompoundTag();
        
        attributeMod.putString( "AttributeId", attributeId );
        attributeMod.put( "AttributeMod", new AttributeModifier(
                name(),
                valueProvider().next( random ),
                operation()
        ).save() );
        attributeMod.putString( "ActiveType", activeType().getName() );
        ListTag attributeModsTag = tag.getList( TAG_ATTRIBUTE_MODS, Tag.TAG_COMPOUND );
        attributeModsTag.add( attributeMod );
        tag.put( TAG_ATTRIBUTE_MODS, attributeModsTag );
    }
    
    public interface RangedValueProvider {
        double next( RandomSource random );
    }
    
    /**
     * Represents which item slots an {@link AttributeBoost} can
     * be active in.
     */
    public enum ActiveType {
        HELD( "held" ),                         // Only active while held in main or off-hand.
        EQUIPPED( "equipped" ),                 // Only active when equipped as armor or a curio.
        HELD_OR_EQUIPPED( "held_or_equipped" ); // Active both when held or equipped.
        
        ActiveType( String name ) {
            this.name = name;
        }
        
        final String name;
        
        public String getName() {
            return name;
        }
        
        /**
         * @return The {@link ActiveType} whose name matches
         * the specified name string. Returns null if no match is found.
         */
        @Nullable
        public static ActiveType getFromName( String name ) {
            for( ActiveType type : values() ) {
                if( type.getName().equals( name ) )
                    return type;
            }
            return null;
        }
    }
}
