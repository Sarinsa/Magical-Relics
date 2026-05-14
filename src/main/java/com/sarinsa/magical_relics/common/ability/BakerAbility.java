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
import fathertoast.crust.api.config.common.field.collection.RegistryWeightedListField;
import fathertoast.crust.api.config.common.value.collection.RegistryWeightedList;
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
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;


@SuppressWarnings( "UnstableApiUsage" )
public class BakerAbility extends BaseArtifactAbility<BakerAbility.BakerAbilityConfig> {
    
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
    
    
    public static class BakerAbilityConfig extends CooldownAbilityConfig {
        
        public final Baker BAKER;
        
        public BakerAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, int cooldown ) {
            super( cfgManager, abilityId, cooldown );
            
            BAKER = new Baker( this );
        }
        
        public static class Baker extends AbstractConfigCategory<BakerAbilityConfig> {
            
            public final RegistryWeightedListField<Block> summonableCakes;
            
            
            public Baker( BakerAbilityConfig parent ) {
                super( parent, "baker", "Options for the cakes this ability can summon." );
                
                summonableCakes = SPEC.define( new RegistryWeightedListField<>( "summonable_cakes", createDefaultSummonableCakes(),
                        "A weighted list of types of cake blocks that can be summoned by this ability." ) );
            }
            
            private RegistryWeightedList<Block> createDefaultSummonableCakes() {
                final var builder = new RegistryWeightedList.Builder<>( ForgeRegistries.BLOCKS )
                        .add( 20, Blocks.CAKE );
                
                // Add all registered cakes by default >:)
                ForgeRegistries.BLOCKS.getValues().stream()
                        .filter( block -> block instanceof CandleCakeBlock )
                        .forEach( block -> builder.add( 1, block ) );
                
                return builder.build();
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new BakerAbilityConfig( cfgManager, abilityId, 20 );
    }
    
    @Override
    public InteractionResult onUse( Level level, @Nullable LivingEntity abilityUser, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( abilityUser == null || ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) return InteractionResult.PASS;
        
        if( !level.isClientSide && hitResult instanceof BlockHitResult blockHitResult ) {
            final Direction face = blockHitResult.getDirection();
            
            if( face != Direction.UP )
                return InteractionResult.PASS;
            
            final BlockPos clickedPos = blockHitResult.getBlockPos();
            final BlockPos toPlacePos = clickedPos.relative( face );
            
            final BlockState currentStateAt = level.getBlockState( toPlacePos );
            final Block cakeBlock = getConfig().BAKER.summonableCakes.next( level.random );
            
            if( cakeBlock != null ) {
                final BlockState stateToPlace = cakeBlock.defaultBlockState();
                
                if( currentStateAt.canBeReplaced() && stateToPlace.canSurvive( level, toPlacePos )
                        && Block.isFaceFull( level.getBlockState( clickedPos ).getCollisionShape( level, clickedPos ), Direction.UP ) ) {
                    level.setBlock( toPlacePos, stateToPlace, Block.UPDATE_CLIENTS );
                    ArtifactUtils.setAbilityOnCooldown( artifact, this );
                    
                    artifact.hurtAndBreak( 1, abilityUser, ( p ) -> p.broadcastBreakEvent( hand ) );
                    
                    level.playSound( null, toPlacePos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F );
                    double x = toPlacePos.getX() + 0.5D;
                    double y = toPlacePos.getY() + 0.4D;
                    double z = toPlacePos.getZ() + 0.5D;
                    double xSpeed = level.random.nextGaussian() * 0.02D;
                    double ySpeed = level.random.nextGaussian() * 0.02D;
                    double zSpeed = level.random.nextGaussian() * 0.02D;
                    ((ServerLevel) level).sendParticles( ParticleTypes.CLOUD, x, y, z, 5, xSpeed, ySpeed, zSpeed, 0.05D );
                    
                    return InteractionResult.SUCCESS;
                }
            }
            level.playSound( null, toPlacePos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 0.9F, 1.0F );
            double x = toPlacePos.getX() + 0.5D;
            double y = toPlacePos.getY() + 0.4D;
            double z = toPlacePos.getZ() + 0.5D;
            double xSpeed = level.random.nextGaussian() * 0.02D;
            double ySpeed = level.random.nextGaussian() * 0.02D;
            double zSpeed = level.random.nextGaussian() * 0.02D;
            ((ServerLevel) level).sendParticles( ParticleTypes.SMOKE, x, y, z, 5, xSpeed, ySpeed, zSpeed, 0.05D );
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
