package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LightningAbility extends BaseArtifactAbility<LightningAbility.LightningAbilityConfig> {
    
    public static final String TAG_SUMMONER_UUID = "SummonerUUID";
    
    private static final String[] PREFIXES = {
            createPrefix( "lightning", "shocking" ),
            createPrefix( "lightning", "electric" ),
            createPrefix( "lightning", "thors" ),
            createPrefix( "lightning", "conductive" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "lightning", "thunder" ),
            createSuffix( "lightning", "lightning" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USE,
            TriggerType.USER_ATTACKING
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AXE,
            ArtifactCategory.SWORD,
            ArtifactCategory.DAGGER,
            ArtifactCategory.STAFF
    );
    
    public LightningAbility() { }
    
    
    public static class LightningAbilityConfig extends CooldownAbilityConfig {
        
        public Lightning LIGHTNING;
        
        public LightningAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                       int cooldown, boolean immuneSummoner ) {
            super( cfgManager, abilityId, cooldown );
            
            LIGHTNING = new Lightning( this, immuneSummoner );
        }
        
        public static class Lightning extends AbstractConfigCategory<LightningAbilityConfig> {
            
            public BooleanField immuneSummoner;
            
            public Lightning( LightningAbilityConfig parent, boolean immuneSummnr ) {
                super( parent, "lightning", "Options for the lightning summoned by this ability." );
                
                immuneSummoner = SPEC.define( new BooleanField( "immune_summoner", immuneSummnr,
                        "If enabled, the player summoning the lightning will not be damaged by it." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new LightningAbilityConfig( cfgManager, abilityId, 100, true );
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
    public void onDamageMob( ItemStack artifact, Player player, LivingEntity attackedMob ) {
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
            
            Level level = attackedMob.level();
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( level );
            
            if( lightningBolt != null ) {
                lightningBolt.moveTo( Vec3.atBottomCenterOf( attackedMob.blockPosition() ) );
                
                if( !level.isClientSide )
                    lightningBolt.setCause( (ServerPlayer) player );
                
                level.addFreshEntity( lightningBolt );
                assignSummoner( lightningBolt, player );
            }
            artifact.hurtAndBreak( 3, player, ( p ) -> p.broadcastBreakEvent( player.getUsedItemHand() ) );
        }
    }
    
    @Override
    public boolean onUse( Level level, Player player, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( hitResult instanceof BlockHitResult blockHitResult ) {
            if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
                ArtifactUtils.setAbilityOnCooldown( artifact, this );
                
                final BlockPos pos = blockHitResult.getBlockPos();
                final LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( level );
                
                if( lightningBolt != null ) {
                    lightningBolt.moveTo( Vec3.atBottomCenterOf( pos ) );
                    lightningBolt.setCause( player instanceof ServerPlayer serverPlayer ? serverPlayer : null );
                    level.addFreshEntity( lightningBolt );
                    assignSummoner( lightningBolt, player );
                }
                artifact.hurtAndBreak( 3, player, ( p ) -> p.broadcastBreakEvent( hand ) );
                return true;
            }
        }
        return false;
    }
    
    public static void assignSummoner( LightningBolt lightningBolt, Player player ) {
        Objects.requireNonNull( lightningBolt );
        Objects.requireNonNull( player );
        
        CompoundTag modData = NBTHelper.getOrCreateCompound( lightningBolt.getPersistentData(), ArtifactUtils.TAG_MOD_DATA );
        modData.putUUID( TAG_SUMMONER_UUID, player.getUUID() );
    }
    
    @Nullable
    public static UUID getSummonerId( LightningBolt lightningBolt ) {
        CompoundTag persistentData = lightningBolt.getPersistentData();
        
        if( NBTHelper.containsCompound( persistentData, ArtifactUtils.TAG_MOD_DATA ) ) {
            CompoundTag modData = NBTHelper.getOrCreateCompound( persistentData, ArtifactUtils.TAG_MOD_DATA );
            
            if( modData.hasUUID( TAG_SUMMONER_UUID ) ) {
                return modData.getUUID( TAG_SUMMONER_UUID );
            }
        }
        return null;
    }
    
    @Override
    @Nullable
    public TriggerType getRandomTrigger( ItemStack artifact, RandomSource random, boolean isArmor, boolean isCurio ) {
        if( isArmor ) return null;
        return random.nextBoolean() ? TriggerType.USER_ATTACKING : TriggerType.USE;
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
            case USE, USER_ATTACKING -> descComponent( type );
            default -> null;
        };
    }
}
