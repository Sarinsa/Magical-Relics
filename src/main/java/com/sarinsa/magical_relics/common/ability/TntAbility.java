package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class TntAbility extends BaseArtifactAbility<TntAbility.TntAbilityConfig> {
    
    
    private static final String[] PREFIXES = {
            createPrefix( "tnt", "demolishing" ),
            createPrefix( "tnt", "explosive" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "tnt", "booms" ),
            createSuffix( "tnt", "combusting" ),
            // Yes this is spanish, but it is funny
            createSuffix( "tnt", "explotando" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USE
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.TRINKET,
            ArtifactCategory.WAND,
            ArtifactCategory.STAFF,
            ArtifactCategory.SWORD
    );
    
    
    public TntAbility() { }
    
    
    public static class TntAbilityConfig extends CooldownAbilityConfig {
        
        public Tnt TNT;
        
        public TntAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, int cooldown, int fuse ) {
            super( cfgManager, abilityId, cooldown );
            
            TNT = new Tnt( this, fuse );
        }
        
        public static class Tnt extends AbstractConfigCategory<TntAbilityConfig> {
            
            public IntField fuse;
            
            public Tnt( TntAbilityConfig parent, int fuze ) {
                super( parent, "tnt", "Options for the step-boost this ability provides" );
                
                fuse = SPEC.define( new IntField( "fuse", fuze, IntField.Range.NON_NEGATIVE,
                        "The fuse length (in ticks) of the TNT summoned by this ability." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new TntAbilityConfig( cfgManager, abilityId, 120, 80 );
    }
    
    @Override
    public boolean onUse( Level level, Player player, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( !(hitResult instanceof BlockHitResult blockHitResult) ) return false;
        
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            Direction face = blockHitResult.getDirection();
            BlockPos relativePos = blockHitResult.getBlockPos().relative( face );
            BlockState relativeState = level.getBlockState( relativePos );
            
            if( relativeState.getCollisionShape( level, relativePos ).isEmpty() ) {
                PrimedTnt tnt = new PrimedTnt( level, relativePos.getX() + 0.5D, relativePos.getY(), relativePos.getZ() + 0.5D, player );
                tnt.setFuse( getConfig().TNT.fuse.get() );
                level.addFreshEntity( tnt );
                
                if( !level.isClientSide ) {
                    level.playSound( null, relativePos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F );
                }
                artifact.hurtAndBreak( 2, player, ( p ) -> p.broadcastBreakEvent( hand ) );
                ArtifactUtils.setAbilityOnCooldown( artifact, this );
                return true;
            }
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
    
    @Override
    public MutableComponent getAbilityDescription( @Nullable TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        return Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.tnt.description" );
    }
}
