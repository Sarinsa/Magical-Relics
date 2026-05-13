package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class YeetAbility extends BaseArtifactAbility<YeetAbility.YeetAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "yeet", "launching" ),
            createPrefix( "yeet", "flinging" ),
            createPrefix( "yeet", "hurling" ),
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "yeet", "yeets" ),
            createSuffix( "yeet", "pitching" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USE,
            TriggerType.DROPPED,
            TriggerType.USER_DAMAGED
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.WAND,
            ArtifactCategory.BELT,
            ArtifactCategory.AMULET,
            ArtifactCategory.STAFF,
            ArtifactCategory.DAGGER
    );
    
    
    public YeetAbility() { }
    
    
    public static class YeetAbilityConfig extends CooldownAbilityConfig {
        
        public final Yeet YEET;
        
        public YeetAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, int cooldown, int yeetPower ) {
            super( cfgManager, abilityId, cooldown );
            
            YEET = new Yeet( this, yeetPower );
        }
        
        public static class Yeet extends AbstractConfigCategory<YeetAbilityConfig> {
            
            public final DoubleField power;
            
            public final DoubleField droppedRadius;
            
            
            public Yeet( YeetAbilityConfig parent, int yeetPower ) {
                super( parent, "yeet", "Options for the throwing power of this ability." );
                
                power = SPEC.define( new DoubleField( "power", yeetPower, DoubleField.Range.NON_NEGATIVE,
                        "The strength of the knockback applied by this ability." ) );
                
                SPEC.newLine();
                
                droppedRadius = SPEC.define( new DoubleField( "dropped_radius", 5.0, DoubleField.Range.NON_NEGATIVE,
                        "The radius of the spherical area around the player in which mobs should be yeeted.",
                        "This is only relevant when this ability has the 'dropped' trigger." ) );
            }
        }
    }
    
    @Override
    public YeetAbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new YeetAbilityConfig( cfgManager, abilityId, 300, 5 );
    }
    
    @Override
    public InteractionResult onUse( Level level, @Nullable LivingEntity abilityUser, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( abilityUser != null && !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            if( hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity target ) {
                if( !level.isClientSide ) {
                    yeet( level, abilityUser, Collections.singletonList( target ) );
                }
                ArtifactUtils.setAbilityOnCooldown( artifact, this );
                artifact.hurtAndBreak( 1, abilityUser, ( p ) -> p.broadcastBreakEvent( hand ) );
                
                return InteractionResult.sidedSuccess( level.isClientSide );
            }
        }
        return InteractionResult.PASS;
    }
    
    @Override
    public boolean onDropped( Level level, ItemEntity itemEntity, Player player ) {
        final ItemStack artifact = itemEntity.getItem();
        
        if( !level.isClientSide && !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            final double radius = getConfig().YEET.droppedRadius.get();
            final List<LivingEntity> nearbyLiving = level.getEntitiesOfClass( LivingEntity.class, player.getBoundingBox().inflate( radius, 0, radius ) );
            // Don't yeet the player who dropped the artifact
            nearbyLiving.remove( player );
            
            nearbyLiving.removeIf( entity -> {
                float xDiff = (float) (entity.getX() - player.getX());
                float zDiff = (float) (entity.getZ() - player.getZ());
                return Mth.sqrt( xDiff * xDiff + zDiff * zDiff ) > radius;
            } );
            
            if( yeet( level, player, nearbyLiving ) ) {
                ArtifactUtils.setAbilityOnCooldown( artifact, this );
            }
        }
        return false;
    }
    
    @Override
    public void onUserDamaged( Level level, Player player, DamageSource damageSource, ItemStack artifact, @Nullable EquipmentSlot slot, @Nullable SlotContext slotContext ) {
        if( !level.isClientSide && !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            if( damageSource.getDirectEntity() instanceof LivingEntity target ) {
                yeet( level, player, Collections.singletonList( target ) );
                
                final Consumer<Player> breakAnim = slot == null
                        ? ( p ) -> CuriosApi.broadcastCurioBreakEvent( slotContext )
                        : ( p ) -> p.broadcastBreakEvent( slot );
                artifact.hurtAndBreak( 1, player, breakAnim );
                ArtifactUtils.setAbilityOnCooldown( artifact, this );
            }
        }
    }
    
    /**
     * Applies knockback to the entities in the given list,
     * relative to the given player.
     *
     * @return True if any entities were yeeted.
     */
    private boolean yeet( Level level, LivingEntity abilityUser, List<LivingEntity> targets ) {
        final double power = getConfig().YEET.power.get();
        
        for( LivingEntity target : targets ) {
            target.knockback( power, abilityUser.getX() - target.getX(), abilityUser.getZ() - target.getZ() );
            if( target instanceof ServerPlayer serverPlayer ) {
                serverPlayer.connection.connection.send( new ClientboundSetEntityMotionPacket( target ) );
            }
        }
        if( !targets.isEmpty() ) {
            level.playSound( null, abilityUser.blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 1.0F, 1.0F );
            return true;
        }
        return false;
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
        if( isCurio ) return random.nextInt( 3 ) == 0 ? TriggerType.USER_DAMAGED : TriggerType.DROPPED;
        return random.nextBoolean() ? TriggerType.USE : TriggerType.DROPPED;
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
            case USE, DROPPED, USER_DAMAGED -> descComponent( type );
            default -> null;
        };
    }
}
