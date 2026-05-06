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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class FoodieAbility extends BaseArtifactAbility<CooldownAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "foodie", "well_fed" ),
            createPrefix( "foodie", "porky" ),
            createPrefix( "foodie", "gluttonous" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "foodie", "saturation" ),
            createSuffix( "foodie", "noms" ),
            createSuffix( "foodie", "munching" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USE,
            TriggerType.USER_ATTACKING,
            TriggerType.ARMOR_TICK,
            TriggerType.CURIO_TICK
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.RING,
            ArtifactCategory.AMULET,
            ArtifactCategory.WAND,
            ArtifactCategory.STAFF,
            ArtifactCategory.SWORD,
            ArtifactCategory.AXE,
            ArtifactCategory.CHESTPLATE,
            ArtifactCategory.HELMET
    );
    
    
    public FoodieAbility() { }
    
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new CooldownAbilityConfig( cfgManager, abilityId, 20 );
    }
    
    @Override
    public boolean onUse( Level level, Player player, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( !player.getFoodData().needsFood() ) return false;
        
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
            RandomSource random = player.getRandom();
            
            if( !level.isClientSide ) {
                player.getFoodData().eat( 2, 0.0F );
                playEatSound( (ServerLevel) player.level(), player.blockPosition(), random );
            }
            artifact.hurtAndBreak( 1, player, ( p ) -> p.broadcastBreakEvent( hand ) );
            return true;
        }
        return false;
    }
    
    @Override
    public void onDamageMob( ItemStack artifact, Player player, LivingEntity attackedMob ) {
        if( !player.getFoodData().needsFood() ) return;
        
        // noinspection resource
        if( !player.level().isClientSide ) {
            RandomSource random = player.getRandom();
            
            if( random.nextInt( 4 ) == 0 ) {
                player.getFoodData().eat( 1, 0.0F );
                playEatSound( (ServerLevel) player.level(), player.blockPosition(), random );
                artifact.hurtAndBreak( 1, player, ( p ) -> p.broadcastBreakEvent( EquipmentSlot.MAINHAND ) );
            }
        }
    }
    
    @Override
    public void onArmorTick( ItemStack artifact, Level level, Player player, EquipmentSlot slot ) {
        if( level.isClientSide ) return;
        
        FoodData foodData = player.getFoodData();
        
        if( foodData.getFoodLevel() < 10 ) {
            final int restoredHunger = 10 - foodData.getFoodLevel();
            foodData.eat( restoredHunger, 0.0F );
            playEatSound( (ServerLevel) level, player.blockPosition(), player.getRandom() );
            artifact.hurtAndBreak( restoredHunger, player, ( p ) -> p.broadcastBreakEvent( slot ) );
        }
    }
    
    @Override
    public void onCurioTick( ItemStack artifact, Level level, Player player, SlotContext slotContext ) {
        if( level.isClientSide ) return;
        
        FoodData foodData = player.getFoodData();
        
        if( foodData.getFoodLevel() < 10 ) {
            final int restoredHunger = 10 - foodData.getFoodLevel();
            foodData.eat( restoredHunger, 0.0F );
            playEatSound( (ServerLevel) level, player.blockPosition(), player.getRandom() );
            artifact.hurtAndBreak( restoredHunger, player, ( p ) -> CuriosApi.broadcastCurioBreakEvent( slotContext ) );
        }
    }
    
    private static void playEatSound( ServerLevel level, BlockPos pos, RandomSource random ) {
        level.playSound(
                null,
                pos,
                SoundEvents.GENERIC_EAT,
                SoundSource.PLAYERS,
                1.0F,
                random.nextFloat() - (random.nextFloat() * 0.2F) + 1.0F
        );
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
        if( isArmor ) return TriggerType.ARMOR_TICK;
        if( isCurio ) return random.nextBoolean() ? TriggerType.CURIO_TICK : TriggerType.USE;
        
        return random.nextBoolean() ? TriggerType.USE : TriggerType.USER_ATTACKING;
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
            case USE, USER_ATTACKING, CURIO_TICK, ARMOR_TICK -> descComponent( type );
            default -> null;
        };
    }
}
