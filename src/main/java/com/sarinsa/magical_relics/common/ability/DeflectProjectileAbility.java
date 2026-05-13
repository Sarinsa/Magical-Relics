package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.collection.RegistrySetField;
import fathertoast.crust.api.config.common.value.collection.RegistrySet;
import fathertoast.crust.api.lib.EntityEventHelper;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

// TODO - WIP! Finish some time
@SuppressWarnings( "UnstableApiUsage" )
public class DeflectProjectileAbility extends BaseArtifactAbility<DeflectProjectileAbility.DeflectProjectileAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "deflect_projectile", "shielding" ),
            createPrefix( "deflect_projectile", "blocking" ),
            createPrefix( "deflect_projectile", "deflective" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "deflect_projectile", "redirecting" ),
            createSuffix( "deflect_projectile", "diversion" ),
            createSuffix( "deflect_projectile", "ricocheting" ),
            createSuffix( "deflect_projectile", "parrying" ),
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK,
            TriggerType.CURIO_TICK,
            TriggerType.HELD
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.HELMET,
            ArtifactCategory.BELT,
            ArtifactCategory.AXE,
            ArtifactCategory.SWORD
    );
    
    
    public DeflectProjectileAbility() { }
    
    
    public static class DeflectProjectileAbilityConfig extends AbilityConfig {
        
        public final DeflectProjectile DEFLECT_PRJ;
        
        public DeflectProjectileAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                               double deflectChance, double deflectRadius ) {
            super( cfgManager, abilityId );
            
            DEFLECT_PRJ = new DeflectProjectile( this, deflectChance, deflectRadius );
        }
        
        public static class DeflectProjectile extends AbstractConfigCategory<DeflectProjectileAbilityConfig> {
            
            public final DoubleField deflectChance;
            
            public final DoubleField deflectRadius;
            
            public final RegistrySetField<EntityType<?>> deflectableProjectiles;
            
            
            public DeflectProjectile( DeflectProjectileAbilityConfig parent, double deflectCh, double deflectRd ) {
                super( parent, "deflect_projectile", "Options for this ability's projectile deflection behavior." );
                
                deflectChance = SPEC.define( new DoubleField( "deflect_chance", deflectCh, DoubleField.Range.PERCENT,
                        "The chance for an incoming projectile to be deflected." ) );
                
                SPEC.newLine();
                
                deflectRadius = SPEC.define( new DoubleField( "deflect_radius", deflectRd, DoubleField.Range.NON_NEGATIVE,
                        "The radius around the player in which the ability can detect projectiles." ) );
                
                SPEC.newLine();
                
                deflectableProjectiles = SPEC.define( new RegistrySetField<>( "deflectable_projectiles", createDefaultDeflectableProjectiles(),
                        "A set of projectile entity types that can be deflected by this ability." ) );
            }
            
            private RegistrySet<EntityType<?>> createDefaultDeflectableProjectiles() {
                return new RegistrySet.Builder<>( ForgeRegistries.ENTITY_TYPES )
                        .addTag( EntityTypeTags.IMPACT_PROJECTILES )
                        .build();
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new DeflectProjectileAbilityConfig( cfgManager, abilityId, 0.3, 2.5 );
    }
    
    @Override
    public void onHeld( Level level, Player player, ItemStack artifact, EquipmentSlot slot ) {
        if( maybeDeflect( level, player ) ) {
            artifact.hurtAndBreak( 1, player, p -> p.broadcastBreakEvent( slot ) );
        }
    }
    
    @Override
    public void onArmorTick( ItemStack artifact, Level level, Player player, EquipmentSlot slot ) {
        if( maybeDeflect( level, player ) ) {
            artifact.hurtAndBreak( 1, player, p -> p.broadcastBreakEvent( slot ) );
        }
    }
    
    @Override
    public void onCurioTick( ItemStack artifact, Level level, Player player, SlotContext slotContext ) {
        if( maybeDeflect( level, player ) ) {
            artifact.hurtAndBreak( 1, player, p -> CuriosApi.broadcastCurioBreakEvent( slotContext ) );
        }
    }
    
    private boolean maybeDeflect( Level level, Player player ) {
        if( level.isClientSide ) return false;
        if( !getConfig().DEFLECT_PRJ.deflectChance.rollChance( level.random ) ) return false;
        
        final double radius = getConfig().DEFLECT_PRJ.deflectRadius.get();
        
        if( radius <= 0 ) return false;
        
        final AABB scanBox = new AABB( -radius, -radius, -radius, radius, radius, radius )
                .move( player.getX(), player.getY() + (player.getBbHeight() / 2), player.getZ() );
        final List<Projectile> nearbyProjectiles = level.getEntitiesOfClass( Projectile.class, scanBox );
        // Remove projectiles not within the configured radius
        nearbyProjectiles.removeIf( entity -> entity.distanceTo( player ) > radius );
        
        if( nearbyProjectiles.isEmpty() ) return false;
        
        final Projectile projectile = nearbyProjectiles.get( level.random.nextInt( nearbyProjectiles.size() ) );
        
        if( !projectile.isAlive() || projectile.getOwner() == player ) return false;
        
        projectile.shootFromRotation( player, -projectile.getXRot(), -projectile.getYRot(), 1.5F, 1.5F, 1.5F );
        projectile.setOwner( player );
        projectile.hasImpulse = true;
        EntityEventHelper.SHIELD_BLOCK_SOUND.broadcast( player );
        
        return true;
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
        if( isArmor ) return TriggerType.ARMOR_TICK;
        if( isCurio ) return TriggerType.CURIO_TICK;
        
        return TriggerType.HELD;
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
            case ARMOR_TICK, CURIO_TICK, HELD -> descComponent( type );
            default -> null;
        };
    }
}
