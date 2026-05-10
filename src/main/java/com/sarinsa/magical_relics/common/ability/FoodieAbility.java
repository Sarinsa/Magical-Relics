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
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
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
import java.util.function.Consumer;

public class FoodieAbility extends BaseArtifactAbility<FoodieAbility.FoodieAbilityConfig> {
    
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
    
    
    public static class FoodieAbilityConfig extends CooldownAbilityConfig {
        
        public final Foodie FOODIE;
        
        public FoodieAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, int cooldown,
                                    int passiveHungerThresh, int hungerOnUse, int hungerOnAttack, double restoreHungerChance ) {
            super( cfgManager, abilityId, cooldown );
            
            FOODIE = new Foodie( this, passiveHungerThresh, hungerOnUse, hungerOnAttack, restoreHungerChance );
        }
        
        public static class Foodie extends AbstractConfigCategory<FoodieAbilityConfig> {
            
            public final IntField passiveHungerThreshold;
            
            public final IntField hungerOnUse;
            
            public final IntField hungerOnAttack;
            public final DoubleField restoreHungerChance;
            
            
            public Foodie( FoodieAbilityConfig parent, int passiveHungrThresh, int hungrOnUse, int hungrOnAttck, double restoreHungrChn ) {
                super( parent, "foodie", "Options for the amount of hunger restored by this ability." );
                
                passiveHungerThreshold = SPEC.define( new IntField( "passive_hunger_threshold", passiveHungrThresh, IntField.Range.POSITIVE,
                        "When this ability has a passive trigger, the wielder's hunger level will not drop below this value." ) );
                
                SPEC.newLine();
                
                hungerOnUse = SPEC.define( new IntField( "hunger_on_use", hungrOnUse, IntField.Range.TOKEN_NEGATIVE,
                        "The amount of hunger this ability restores for its wielder when used.",
                        "This only applies when the ability has a use trigger type." ) );
                
                SPEC.newLine();
                
                hungerOnAttack = SPEC.define( new IntField( "hunger_on_attack", hungrOnAttck, IntField.Range.TOKEN_NEGATIVE,
                        "The amount of hunger this ability restores for its wielder when dealing damage to a creature.",
                        "This only applies when the ability has an attack trigger type." ) );
                
                restoreHungerChance = SPEC.define( new DoubleField( "restore_hunger_chance", restoreHungrChn, DoubleField.Range.PERCENT,
                        "The chance for hunger to be restored when attacking a creature." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new FoodieAbilityConfig( cfgManager, abilityId, 20,
                10, 2, 1, 0.25 );
    }
    
    @Override
    public boolean onUse( Level level, Player player, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( !player.getFoodData().needsFood() ) return false;
        
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
            final RandomSource random = player.getRandom();
            
            if( !level.isClientSide ) {
                player.getFoodData().eat( getConfig().FOODIE.hungerOnUse.get(), 0.0F );
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
            final RandomSource random = player.getRandom();
            
            if( getConfig().FOODIE.restoreHungerChance.rollChance( random ) ) {
                player.getFoodData().eat( getConfig().FOODIE.hungerOnAttack.get(), 0.0F );
                playEatSound( (ServerLevel) player.level(), player.blockPosition(), random );
                artifact.hurtAndBreak( 1, player, ( p ) -> p.broadcastBreakEvent( EquipmentSlot.MAINHAND ) );
            }
        }
    }
    
    @Override
    public void onArmorTick( ItemStack artifact, Level level, Player player, EquipmentSlot slot ) {
        handlePassiveHunger( artifact, level, player, ( p ) -> p.broadcastBreakEvent( slot ) );
    }
    
    @Override
    public void onCurioTick( ItemStack artifact, Level level, Player player, SlotContext slotContext ) {
        handlePassiveHunger( artifact, level, player, ( p ) -> CuriosApi.broadcastCurioBreakEvent( slotContext ) );
    }
    
    private void handlePassiveHunger( ItemStack artifact, Level level, Player player, Consumer<Player> breakAnim ) {
        if( level.isClientSide ) return;
        
        final FoodData foodData = player.getFoodData();
        final int threshold = getConfig().FOODIE.passiveHungerThreshold.get();
        
        if( foodData.getFoodLevel() < threshold ) {
            final int restoredHunger = threshold - foodData.getFoodLevel();
            foodData.eat( restoredHunger, 0.0F );
            playEatSound( (ServerLevel) level, player.blockPosition(), player.getRandom() );
            artifact.hurtAndBreak( restoredHunger, player, breakAnim );
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
    public List<ArtifactCategory> getCompatibleCategories() {
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
