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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

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
            TriggerType.RIGHT_CLICK_BLOCK
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
    public boolean onClickBlock( Level level, ItemStack itemStack, BlockPos pos, BlockState state, Direction face, Player player ) {
        if( ArtifactUtils.isAbilityOnCooldown( itemStack, this ) ) return false;
        
        if( face != Direction.UP )
            return false;
        
        BlockPos toPlacePos = pos.relative( face );
        BlockState currentStateAt = level.getBlockState( toPlacePos );
        
        if( currentStateAt.isAir() && Blocks.CAKE.defaultBlockState().canSurvive( level, toPlacePos ) ) {
            level.setBlock( toPlacePos, Blocks.CAKE.defaultBlockState(), Block.UPDATE_ALL );
            ArtifactUtils.setAbilityOnCooldown( itemStack, this );
            
            itemStack.hurtAndBreak( 1, player, ( p ) -> p.broadcastBreakEvent( player.getUsedItemHand() ) );
            
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
        return isArmor ? null : TriggerType.RIGHT_CLICK_BLOCK;
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
