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
import fathertoast.crust.api.config.common.field.collection.RegistrySetField;
import fathertoast.crust.api.config.common.value.collection.RegistrySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings( "UnstableApiUsage" )
public class MassExcavateAbility extends BaseArtifactAbility<MassExcavateAbility.MassExcavateAbilityConfig> {
    
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
    
    
    public static class MassExcavateAbilityConfig extends CooldownAbilityConfig {
        
        public final MassExcavate MASS_EXCAVATE;
        
        public MassExcavateAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                          Rarity rarity, int cooldown ) {
            super( cfgManager, abilityId, rarity, cooldown );
            
            MASS_EXCAVATE = new MassExcavate( this );
        }
        
        public static class MassExcavate extends AbstractConfigCategory<MassExcavateAbilityConfig> {
            
            public final RegistrySetField<Block> effectiveOn;
            
            public MassExcavate( MassExcavateAbilityConfig parent ) {
                super( parent, "mass_excavate", "Options for which blocks can be efficiently mined by this ability." );
                
                effectiveOn = SPEC.define( new RegistrySetField<>( "effective_on", createDefaultEffectiveOn(),
                        "A set of blocks that this ability can mine." ) );
            }
            
            private RegistrySet<Block> createDefaultEffectiveOn() {
                return new RegistrySet.Builder<>( ForgeRegistries.BLOCKS )
                        .addTag( BlockTags.MINEABLE_WITH_PICKAXE )
                        .addTag( BlockTags.MINEABLE_WITH_SHOVEL )
                        .build();
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new MassExcavateAbilityConfig( cfgManager, abilityId, Rarity.UNCOMMON, 20 );
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
    public InteractionResult onUse( Level level, @Nullable LivingEntity abilityUser, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( abilityUser == null || level.isClientSide || !(hitResult instanceof BlockHitResult blockHitResult) )
            return InteractionResult.PASS;
        
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            final BlockState state = level.getBlockState( blockHitResult.getBlockPos() );
            final BlockPos pos = blockHitResult.getBlockPos();
            final Direction face = blockHitResult.getDirection();
            
            if( getConfig().MASS_EXCAVATE.effectiveOn.contains( state.getBlock() ) ) {
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
                    if( checkAndMineBlock( (ServerLevel) level, nextPos, abilityUser ) ) {
                        destroyedAnyBlocks = true;
                    }
                }
                if( destroyedAnyBlocks ) {
                    artifact.hurtAndBreak( 1, abilityUser, ( entity ) -> entity.broadcastBreakEvent( hand ) );
                    ArtifactUtils.setAbilityOnCooldown( artifact, this );
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
    
    private boolean checkAndMineBlock( ServerLevel level, BlockPos pos, LivingEntity abilityUser ) {
        final boolean canDestroyBlock;
        
        if( abilityUser instanceof Player player ) {
            final BlockEvent.BreakEvent event = new BlockEvent.BreakEvent( level, pos, level.getBlockState( pos ), player );
            MinecraftForge.EVENT_BUS.post( event );
            canDestroyBlock = !event.isCanceled();
        }
        else {
            canDestroyBlock = ForgeHooks.canEntityDestroy( level, pos, abilityUser );
        }
        
        if( canDestroyBlock ) {
            final BlockState state = level.getBlockState( pos );
            
            if( getConfig().MASS_EXCAVATE.effectiveOn.contains( state.getBlock() ) ) {
                if( !(abilityUser instanceof Player player) || !player.isCreative() ) {
                    Block.dropResources( state, level, pos );
                }
                level.destroyBlock( pos, false );
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
    public List<ArtifactCategory> getCompatibleCategories() {
        return TYPES;
    }
    
    @Override
    public boolean showCooldownSymbol() {
        return false;
    }
}
