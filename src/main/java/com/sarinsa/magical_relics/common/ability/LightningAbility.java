package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.annotations.AbilityConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LightningAbility extends BaseArtifactAbility {
    
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
            TriggerType.RIGHT_CLICK_BLOCK,
            TriggerType.USER_ATTACKING
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AXE,
            ArtifactCategory.SWORD,
            ArtifactCategory.DAGGER,
            ArtifactCategory.STAFF
    );
    
    public static ForgeConfigSpec.BooleanValue cancelUserDamage;
    private static ForgeConfigSpec.IntValue cooldown;
    
    
    @AbilityConfig( abilityId = "magical_relics:lightning" )
    public static void buildEntries( ForgeConfigSpec.Builder configBuilder ) {
        cancelUserDamage = configBuilder.comment( "If enabled, the player summoning the lightning bolt will not be struck themselves." )
                .define( "cancelUserDamage", true );
        
        cooldown = configBuilder.comment( "How many ticks of cooldown to put this ability on when it has been used" )
                .defineInRange( "cooldown", 100, 20, 100000 );
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
            ArtifactUtils.setAbilityCooldown( artifact, this, cooldown.get() );
            
            Level level = attackedMob.level();
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( level );
            lightningBolt.moveTo( Vec3.atBottomCenterOf( attackedMob.blockPosition() ) );
            lightningBolt.setCause( player instanceof ServerPlayer serverPlayer ? serverPlayer : null );
            level.addFreshEntity( lightningBolt );
            assignSummoner( lightningBolt, player );
            
            artifact.hurtAndBreak( 3, player, ( p ) -> p.broadcastBreakEvent( player.getUsedItemHand() ) );
        }
    }
    
    @Override
    public boolean onClickBlock( Level level, ItemStack artifact, BlockPos pos, BlockState state, Direction face, Player player ) {
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            ArtifactUtils.setAbilityCooldown( artifact, this, cooldown.get() );
            
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( level );
            lightningBolt.moveTo( Vec3.atBottomCenterOf( pos ) );
            lightningBolt.setCause( player instanceof ServerPlayer serverPlayer ? serverPlayer : null );
            level.addFreshEntity( lightningBolt );
            assignSummoner( lightningBolt, player );
            
            artifact.hurtAndBreak( 3, player, ( p ) -> p.broadcastBreakEvent( player.getUsedItemHand() ) );
            return true;
        }
        return false;
    }
    
    public static void assignSummoner( LightningBolt lightningBolt, Player player ) {
        Objects.requireNonNull( lightningBolt );
        Objects.requireNonNull( player );
        
        CompoundTag modData = new CompoundTag();
        modData.putUUID( "PlayerSummoner", player.getUUID() );
        lightningBolt.getPersistentData().put( "MagicalRelicsData", modData );
    }
    
    @Nullable
    public static UUID getSummonerId( LightningBolt lightningBolt ) {
        if( lightningBolt.getPersistentData().contains( "MagicalRelicsData", Tag.TAG_COMPOUND ) ) {
            CompoundTag modData = lightningBolt.getPersistentData().getCompound( "MagicalRelicsData" );
            
            if( modData.hasUUID( "PlayerSummoner" ) ) {
                return modData.getUUID( "PlayerSummoner" );
            }
        }
        return null;
    }
    
    @Override
    public @Nullable TriggerType getRandomTrigger( ItemStack artifact, RandomSource random, boolean isArmor, boolean isCurio ) {
        if( isArmor ) return null;
        
        return random.nextBoolean() ? TriggerType.USER_ATTACKING : TriggerType.RIGHT_CLICK_BLOCK;
    }
    
    @Override
    public @NotNull List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }
    
    @Override
    public MutableComponent getAbilityDescription( TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        if( type == null ) return null;
        
        return type == TriggerType.RIGHT_CLICK_BLOCK
                ? Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.lightning.description.right_click_block" )
                : Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.lightning.description.user_attacking" );
    }
}
