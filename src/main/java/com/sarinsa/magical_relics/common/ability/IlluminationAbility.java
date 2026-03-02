package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.EnumField;
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class IlluminationAbility extends BaseArtifactAbility<IlluminationAbility.IlluminationAbilityConfig> {
    
    
    private static final String[] PREFIXES = {
            createPrefix( "illumination", "illuminating" ),
            createPrefix( "illumination", "bright" ),
            createPrefix( "illumination", "shining" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "illumination", "light" ),
            createSuffix( "illumination", "sun" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK,
            TriggerType.HELD,
            TriggerType.CURIO_TICK
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.TRINKET,
            ArtifactCategory.STAFF,
            ArtifactCategory.WAND,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.RING,
            ArtifactCategory.WAND,
            ArtifactCategory.HELMET
    );
    
    public enum LightMode {
        BLOCK,
        SKY,
        BOTH
    }
    
    
    public IlluminationAbility() { }
    
    
    public static class IlluminationAbilityConfig extends AbilityConfig {
        
        public Illumination ILLUMINATION;
        
        public IlluminationAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                          int lightThreshold, LightMode lightMode ) {
            super( cfgManager, abilityId );
            
            ILLUMINATION = new Illumination( this, lightThreshold, lightMode );
        }
        
        public static class Illumination extends AbstractConfigCategory<IlluminationAbilityConfig> {
            
            public IntField lightThreshold;
            public EnumField<LightMode> lightCheckMode;
            
            public Illumination( IlluminationAbilityConfig parent, int lightThreshld, LightMode lightMode ) {
                super( parent, "illumination", "Options for the light sources this ability places in the world." );
                
                lightThreshold = SPEC.define( new IntField( "light_threshold", lightThreshld, 0, 15,
                        "When the light level at the player's location is lower or equal to this number, light sources will be placed." ) );
                
                lightCheckMode = SPEC.define( new EnumField<>( "light_check_mode", lightMode,
                        "What type of light layer to check when determining if light sources should be placed.",
                        "By default only block light is checked, so skylight does not matter.",
                        "If both are enabled, the greater light value of the two is picked." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new IlluminationAbilityConfig( cfgManager, abilityId, 3, LightMode.BLOCK );
    }
    
    @Override
    public void onArmorTick( ItemStack artifact, Level level, Player player, EquipmentSlot slot ) {
        if( !level.isClientSide ) {
            BlockPos pos = player.blockPosition();
            final int lightLevel = switch( getConfig().ILLUMINATION.lightCheckMode.get() ) {
                case BLOCK -> level.getBrightness( LightLayer.BLOCK, pos );
                case SKY -> level.getBrightness( LightLayer.SKY, pos );
                // If both, pick the greater of the two.
                case BOTH ->
                        Math.max( level.getBrightness( LightLayer.SKY, pos ), level.getBrightness( LightLayer.BLOCK, pos ) );
            };
            
            if( lightLevel <= getConfig().ILLUMINATION.lightThreshold.get() ) {
                if( isPosForIllumination( level, pos ) ) {
                    level.setBlock( pos, MRBlocks.ILLUMINATION_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS );
                }
                else {
                    if( isPosForIllumination( level, pos.above() ) && !level.getBlockState( pos ).is( MRBlocks.ILLUMINATION_BLOCK.get() ) ) {
                        level.setBlock( pos.above(), MRBlocks.ILLUMINATION_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS );
                    }
                }
            }
        }
    }
    
    private boolean isPosForIllumination( Level level, BlockPos pos ) {
        BlockState state = level.getBlockState( pos );
        return state.getFluidState().isEmpty() && state.is( BlockTags.REPLACEABLE ) && !state.is( MRBlocks.ILLUMINATION_BLOCK.get() );
    }
    
    @Override
    public void onCurioTick( ItemStack artifact, Level level, Player player, SlotContext slotContext ) {
        // noinspection ConstantConditions
        onArmorTick( artifact, level, player, null );
    }
    
    @Override
    public void onHeld( Level level, Player player, ItemStack artifact, EquipmentSlot slot ) {
        onArmorTick( artifact, level, player, slot );
    }
    
    @Override
    public String[] getPrefixes() {
        return PREFIXES;
    }
    
    @Override
    public String[] getSuffixes() {
        return SUFFIXES;
    }
    
    @Override
    @Nullable
    public TriggerType getRandomTrigger( ItemStack artifact, RandomSource random, boolean isArmor, boolean isCurio ) {
        if( isCurio ) return TriggerType.CURIO_TICK;
        return isArmor ? TriggerType.ARMOR_TICK : TriggerType.HELD;
    }
    
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }
    
    @Override
    @Nullable
    public MutableComponent getAbilityDescription( @Nullable TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        if( type == null ) return null;
        
        return switch( type ) {
            case ARMOR_TICK ->
                    Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.illumination.description.armor_tick" );
            case HELD ->
                    Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.illumination.description.held" );
            case CURIO_TICK ->
                    Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.illumination.description.curio" );
            default -> null;
        };
    }
}
