package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.entity.VolatileFireball;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FireballAbility extends BaseArtifactAbility<FireballAbility.FireballAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "fireball", "flaming" ),
            createPrefix( "fireball", "burning" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "fireball", "fire" ),
            createSuffix( "fireball", "heat" ),
            createSuffix( "fireball", "incineration" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USE
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.RING,
            ArtifactCategory.WAND,
            ArtifactCategory.STAFF,
            ArtifactCategory.DAGGER,
            ArtifactCategory.AXE
    );
    
    
    public FireballAbility() { }
    
    
    public static class FireballAbilityConfig extends CooldownAbilityConfig {
        
        public Fireball FIREBALL;
        
        public FireballAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                      int cooldown, int explosionPower ) {
            super( cfgManager, abilityId, cooldown );
            
            FIREBALL = new Fireball( this, explosionPower );
        }
        
        public static class Fireball extends AbstractConfigCategory<FireballAbilityConfig> {
            
            public IntField explosionPower;
            
            public Fireball( FireballAbilityConfig parent, int explosionPwer ) {
                super( parent, "fireball", "Options for the fireball summoned by this ability." );
                
                explosionPower = SPEC.define( new IntField( "explosion_power", explosionPwer, IntField.Range.NON_NEGATIVE,
                        "The explosion power of the fireballs summoned by this ability.",
                        "Be a bit careful with larger numbers, since the fireballs explode automatically after having traveled a good distance." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new FireballAbilityConfig( cfgManager, abilityId, 20, 1 );
    }
    
    @Override
    public boolean onUse( Level level, Player player, ItemStack itemStack, @Nullable HitResult hitResult ) {
        if( !ArtifactUtils.isAbilityOnCooldown( itemStack, this ) ) {
            if( !level.isClientSide ) {
                shootFireball( level, player );
                itemStack.hurtAndBreak( 1, player, ( p ) -> p.broadcastBreakEvent( player.getUsedItemHand() ) );
                
                ArtifactUtils.setAbilityOnCooldown( itemStack, this );
            }
            return true;
        }
        return false;
    }
    
    private void shootFireball( Level level, Player player ) {
        Vec3 viewVec = player.getViewVector( 0.5F );
        VolatileFireball fireball = new VolatileFireball( level, player, 0.0D, 0.0D, 0.0D, getConfig().FIREBALL.explosionPower.get() );
        fireball.setPos( player.getX() + viewVec.x * 2.0D, player.getY( 0.5D ) + 0.25D, fireball.getZ() + viewVec.z * 2.0D );
        fireball.shootFromRotation( player, player.getXRot(), player.getYRot(), 1.5F, 1.5F, 1.5F );
        level.addFreshEntity( fireball );
        
        RandomSource random = level.random;
        level.playSound( null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F );
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
        return isArmor ? null : TriggerType.USE;
    }
    
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }
}
