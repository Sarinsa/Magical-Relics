package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.sync.SyncedProperties;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.network.NetworkHelper;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.InjectionWrapperField;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;


public class AirSneakAbility extends BaseArtifactAbility<AirSneakAbility.AirSneakAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "air_sneak", "floaty" ),
            createPrefix( "air_sneak", "light" ),
            createPrefix( "air_sneak", "airy" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "air_sneak", "hermes" ),
            createSuffix( "air_sneak", "flight" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK,
            TriggerType.HELD,
            TriggerType.CURIO_TICK
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.TRINKET,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.STAFF,
            ArtifactCategory.RING,
            ArtifactCategory.WAND,
            ArtifactCategory.BOOTS
    );
    
    
    public AirSneakAbility() { }
    
    
    public static class AirSneakAbilityConfig extends AbilityConfig {
        
        public final AirSneakAbilityConfig.AirSneak AIR_SNEAK;
        
        public AirSneakAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                      boolean allowReplacing ) {
            super( cfgManager, abilityId );
            
            AIR_SNEAK = new AirSneak( this, allowReplacing );
        }
        
        public static class AirSneak extends AbstractConfigCategory<AirSneakAbilityConfig> {
            
            public final InjectionWrapperField<BooleanField> allowReplacing;
            
            public AirSneak( AirSneakAbilityConfig parent, boolean allowReplcng ) {
                super( parent, "air_sneak", "Options for the general behavior of this ability." );
                
                allowReplacing = SPEC.define( new InjectionWrapperField<>( new BooleanField( "allow_replacing", allowReplcng,
                        "If enabled, solid air can replace blocks that are considered replaceable, " +
                                "such as tall grass, snow, dead bushes etc." ), ( field ) -> {
                    SyncedProperties.broadcastSyncProperty( SyncedProperties.AIR_SNEAK_ALLOW_REPLACE );
                } ) );
            }
        }
    }
    
    @Override
    public AirSneakAbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new AirSneakAbilityConfig( cfgManager, abilityId, false );
    }
    
    @Override
    public void onHeld( Level level, Player player, ItemStack heldArtifact, EquipmentSlot slot ) {
        placeSolidAir( level, player, slot, null );
    }
    
    @Override
    public void onCurioTick( ItemStack artifact, Level level, Player player, SlotContext slotContext ) {
        placeSolidAir( level, player, null, slotContext );
    }
    
    @Override
    public void onArmorTick( ItemStack stack, Level level, Player player, EquipmentSlot slot ) {
        placeSolidAir( level, player, slot, null );
    }
    
    /** Checks if the specified player is sneaking, and attempts to place a solid air block below their feet if possible. */
    private void placeSolidAir( Level level, Player player, @Nullable EquipmentSlot slot, @Nullable SlotContext slotContext ) {
        // Set block on client quickly and ask the server later if the placement was ok
        if( level.isClientSide ) {
            final BlockPos belowPos = player.blockPosition().below();
            final BlockState belowState = level.getBlockState( belowPos );
            
            if( player.isShiftKeyDown() ) {
                final boolean notAscending = player.onGround() || player.getDeltaMovement().y <= -0.001;
                // Sync config value from server to client!
                final boolean canReplaceBelow = SyncedProperties.AIR_SNEAK_ALLOW_REPLACE.getValue()
                        ? (belowState.canBeReplaced() && !belowState.isFaceSturdy( level, belowPos, Direction.UP ))
                        : belowState.isAir();
                
                if( notAscending && canReplaceBelow && !belowState.is( MRBlocks.SOLID_AIR.get() ) ) {
                    level.setBlock( belowPos, MRBlocks.SOLID_AIR.get().defaultBlockState(), Block.UPDATE_INVISIBLE );
                    NetworkHelper.requestSolidAirPlacement( player, belowPos, slot, slotContext );
                }
            }
            else {
                if( belowState.is( MRBlocks.SOLID_AIR.get() ) ) {
                    level.removeBlock( belowPos, false );
                    NetworkHelper.requestSolidAirRemoval( player, belowPos );
                }
            }
        }
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
        if( isCurio ) {
            return random.nextBoolean() ? TriggerType.HELD : TriggerType.CURIO_TICK;
        }
        return isArmor ? TriggerType.ARMOR_TICK : TriggerType.HELD;
    }
    
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleCategories() {
        return TYPES;
    }
    
    @Override
    @Nullable
    public MutableComponent getAbilityDescription( @Nullable TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        if( type == null ) return null;
        
        return switch( type ) {
            case HELD, CURIO_TICK, ARMOR_TICK -> descComponent( type );
            default -> null;
        };
    }
}
