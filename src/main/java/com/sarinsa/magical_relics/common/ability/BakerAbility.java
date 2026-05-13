package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;


public class BakerAbility extends BaseArtifactAbility<CooldownAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "baker", "bakers" ),
            createPrefix( "baker", "confectioners" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "baker", "baking" ),
            createSuffix( "baker", "frosting" ),
            createSuffix( "baker", "tastiness" ),
            createSuffix( "baker", "delight" ),
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USE
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.STAFF,
            ArtifactCategory.TRINKET,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.WAND
    );
    
    
    public BakerAbility() { }
    
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new CooldownAbilityConfig( cfgManager, abilityId, 20 );
    }
    
    @Override
    public InteractionResult onUse( Level level, @Nullable LivingEntity abilityUser, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( abilityUser == null || ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) return InteractionResult.PASS;
        
        if( hitResult instanceof BlockHitResult blockHitResult ) {
            BlockPos clickedPos = blockHitResult.getBlockPos();
            Direction face = blockHitResult.getDirection();
            
            if( face != Direction.UP )
                return InteractionResult.PASS;
            
            BlockPos toPlacePos = clickedPos.relative( face );
            BlockState currentStateAt = level.getBlockState( toPlacePos );
            
            if( currentStateAt.canBeReplaced() && Blocks.CAKE.defaultBlockState().canSurvive( level, toPlacePos ) ) {
                level.setBlock( toPlacePos, Blocks.CAKE.defaultBlockState(), Block.UPDATE_ALL );
                ArtifactUtils.setAbilityOnCooldown( artifact, this );
                
                artifact.hurtAndBreak( 1, abilityUser, ( p ) -> p.broadcastBreakEvent( hand ) );
                
                if( !level.isClientSide ) {
                    level.playSound( null, toPlacePos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F );
                    double x = toPlacePos.getX() + 0.5D;
                    double y = toPlacePos.getY() + 0.4D;
                    double z = toPlacePos.getZ() + 0.5D;
                    double xSpeed = level.random.nextGaussian() * 0.02D;
                    double ySpeed = level.random.nextGaussian() * 0.02D;
                    double zSpeed = level.random.nextGaussian() * 0.02D;
                    ((ServerLevel) level).sendParticles( ParticleTypes.CLOUD, x, y, z, 5, xSpeed, ySpeed, zSpeed, 0.05D );
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
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
    public List<ArtifactCategory> getCompatibleCategories() {
        return TYPES;
    }
}
