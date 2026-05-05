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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;

import javax.annotation.Nullable;
import java.util.List;

public class MassExcavateAbility extends BaseArtifactAbility<CooldownAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "mass_excavate", "miners" ),
            createPrefix( "mass_excavate", "digging" ),
            createPrefix( "mass_excavate", "excavating" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "mass_excavate", "drilling" ),
            createSuffix( "mass_excavate", "tunneling" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USE
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.TRINKET,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.STAFF,
            ArtifactCategory.RING,
            ArtifactCategory.WAND
    );
    
    public MassExcavateAbility() { }
    
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new CooldownAbilityConfig( cfgManager, abilityId, Rarity.UNCOMMON, 20 );
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
    public boolean onUse( Level level, Player player, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( level.isClientSide || !(hitResult instanceof BlockHitResult blockHitResult) ) return false;
        
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            final BlockState state = level.getBlockState( blockHitResult.getBlockPos() );
            final BlockPos pos = blockHitResult.getBlockPos();
            final Direction face = blockHitResult.getDirection();
            
            if( state.is( BlockTags.MINEABLE_WITH_PICKAXE ) || state.is( BlockTags.MINEABLE_WITH_SHOVEL ) ) {
                BlockPos pos1;
                BlockPos pos2;
                
                switch( face ) {
                    case DOWN -> {
                        pos1 = pos.offset( 1, 0, 1 );
                        pos2 = pos.offset( -1, 2, -1 );
                    }
                    case NORTH -> {
                        pos1 = pos.offset( 1, -1, 0 );
                        pos2 = pos.offset( -1, 1, 2 );
                    }
                    case SOUTH -> {
                        pos1 = pos.offset( -1, -1, 1 );
                        pos2 = pos.offset( 1, 1, -2 );
                    }
                    case EAST -> {
                        pos1 = pos.offset( 0, -1, 1 );
                        pos2 = pos.offset( -2, 1, -1 );
                    }
                    case WEST -> {
                        pos1 = pos.offset( 0, -1, 1 );
                        pos2 = pos.offset( 2, 1, -1 );
                    }
                    // Up and default!
                    default -> {
                        pos1 = pos.offset( 1, 0, 1 );
                        pos2 = pos.offset( -1, -2, -1 );
                    }
                }
                boolean destroyedAnyBlocks = false;
                
                for( BlockPos nextPos : BlockPos.betweenClosed( pos1, pos2 ) ) {
                    if( checkAndMineBlock( (ServerLevel) level, nextPos, player ) ) {
                        destroyedAnyBlocks = true;
                    }
                }
                if( destroyedAnyBlocks ) {
                    artifact.hurtAndBreak( 1, player, ( entity ) -> entity.broadcastBreakEvent( hand ) );
                    ArtifactUtils.setAbilityOnCooldown( artifact, this );
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    private boolean checkAndMineBlock( ServerLevel level, BlockPos pos, Player player ) {
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent( level, pos, level.getBlockState( pos ), player );
        MinecraftForge.EVENT_BUS.post( event );
        
        if( !event.isCanceled() ) {
            BlockState state = level.getBlockState( pos );
            
            if( state.is( BlockTags.MINEABLE_WITH_SHOVEL ) || state.is( BlockTags.MINEABLE_WITH_PICKAXE ) ) {
                if( !player.isCreative() ) {
                    Block.dropResources( state, level, pos );
                }
                level.playSound( null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.5F, 1.0F );
                level.removeBlock( pos, false );
                return true;
            }
        }
        return false;
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
    public boolean showCooldownSymbol() {
        return false;
    }
}
