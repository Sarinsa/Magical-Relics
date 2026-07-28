package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.event.ServerEventListener;
import com.sarinsa.magical_relics.common.item.IArtifactItem;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RepairOthersAbility extends BaseArtifactAbility<RepairOthersAbility.RepairOthersAbilityConfig> {
    
    
    private static final String[] PREFIXES = {
            createPrefix( "repair_others", "repairing" ),
            createPrefix( "repair_others", "recharging" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "repair_others", "renewal" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK,
            TriggerType.HELD,
            TriggerType.USE,
            TriggerType.CURIO_TICK
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.WAND,
            ArtifactCategory.BELT,
            ArtifactCategory.STAFF,
            ArtifactCategory.TRINKET,
            ArtifactCategory.CHESTPLATE,
            ArtifactCategory.HELMET
    );
    
    private static final Predicate<ItemStack> REPAIR_CONDITIONS =
            ( stack ) -> !stack.isEmpty()
                    && !(stack.getItem() instanceof IArtifactItem)
                    && stack.getDamageValue() > 0;
    
    
    public RepairOthersAbility() { }
    
    
    public static class RepairOthersAbilityConfig extends CooldownAbilityConfig {
        
        public final RepairOthers REPAIR_OTHERS;
        
        public RepairOthersAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                          int cooldown ) {
            super( cfgManager, abilityId, cooldown );
            
            REPAIR_OTHERS = new RepairOthers( this );
        }
        
        public static class RepairOthers extends AbstractConfigCategory<RepairOthersAbilityConfig> {
            
            public final IntField.RandomRange durRestoredOnUse;
            public final IntField.RandomRange durRestoredPassively;
            
            public RepairOthers( RepairOthersAbilityConfig parent ) {
                super( parent, "repair_others", "Options for this ability repairing other items in the inventory." );
                
                durRestoredOnUse = new IntField.RandomRange( SPEC, "durability_restoration.use", 1, 5, IntField.Range.POSITIVE,
                        "The minimum and maximum (inclusive) amount of durability that can be restored for the target item in the user's inventory when this ability has a use trigger." );
                
                SPEC.newLine();
                
                durRestoredPassively = new IntField.RandomRange( SPEC, "durability_restoration.passive", 1, 2, IntField.Range.POSITIVE,
                        "The minimum and maximum (inclusive) amount of durability that can be restored for the target item in the user's inventory when this ability has a passive trigger." );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new RepairOthersAbilityConfig( cfgManager, abilityId, 20 );
    }
    
    @Override
    public InteractionResult onUse( Level level, @Nullable LivingEntity abilityUser, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( abilityUser != null && !level.isClientSide && !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            final List<ItemStack> fixCandidates = getAbilityUserInventory( abilityUser );
            
            // Pick a random "fixable" item to restore durability for
            if( !fixCandidates.isEmpty() ) {
                final RandomSource random = level.random;
                final ItemStack stackToFix = fixCandidates.get( random.nextInt( fixCandidates.size() ) );
                
                // 1/3 chance to hurt the artifact
                if( random.nextInt( 3 ) == 0 ) {
                    artifact.hurtAndBreak( 1, abilityUser, ( p ) -> p.broadcastBreakEvent( hand ) );
                }
                stackToFix.hurt( -getConfig().REPAIR_OTHERS.durRestoredOnUse.next( random ), random, abilityUser instanceof ServerPlayer serverPlayer ? serverPlayer : null );
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
    
    @Override
    public void onHeld( Level level, Player player, ItemStack artifact, EquipmentSlot slot ) {
        repairRandomItem( level, player, artifact, ( p ) -> p.broadcastBreakEvent( slot ) );
    }
    
    @Override
    public void onCurioTick( ItemStack artifact, Level level, Player player, SlotContext slotContext ) {
        repairRandomItem( level, player, artifact, ( p ) -> CuriosApi.broadcastCurioBreakEvent( slotContext ) );
    }
    
    @Override
    public void onArmorTick( ItemStack artifact, Level level, Player player, EquipmentSlot slot ) {
        onHeld( level, player, artifact, slot );
    }
    
    private List<ItemStack> getAbilityUserInventory( LivingEntity abilityUser ) {
        final List<ItemStack> fixCandidates = new ArrayList<>();
        
        if( abilityUser instanceof Player player ) {
            for( int i = 0; i < player.getInventory().getContainerSize(); i++ ) {
                final ItemStack stack = player.getInventory().getItem( i );
                if( REPAIR_CONDITIONS.test( stack ) ) fixCandidates.add( stack );
            }
        }
        else {
            for( EquipmentSlot slot : EquipmentSlot.values() ) {
                final ItemStack stack = abilityUser.getItemBySlot( slot );
                if( REPAIR_CONDITIONS.test( stack ) ) fixCandidates.add( stack );
            }
        }
        // Collect all curio stacks
        CuriosApi.getCuriosInventory( abilityUser ).ifPresent( ( itemHandler ) -> {
            final List<SlotResult> slotResults = itemHandler.findCurios();
            
            for( SlotResult slotResult : slotResults ) {
                ItemStack stack = slotResult.stack();
                if( REPAIR_CONDITIONS.test( stack ) ) fixCandidates.add( stack );
            }
        } );
        return fixCandidates;
    }
    
    private void repairRandomItem( Level level, Player player, ItemStack artifact, Consumer<Player> onBreakCallback ) {
        if( level.isClientSide ) return;
        
        if( ServerEventListener.getRepairTick() % 200 == 0 ) {
            final List<ItemStack> fixCandidates = getAbilityUserInventory( player );
            
            // Pick a random "fixable" item to restore durability for
            if( !fixCandidates.isEmpty() ) {
                final RandomSource random = level.random;
                final ItemStack stackToFix = fixCandidates.get( random.nextInt( fixCandidates.size() ) );
                
                // 1/3 chance to hurt the artifact. A 1-to-1 conversion ratio would not be much to brag about.
                if( random.nextInt( 3 ) == 0 )
                    artifact.hurtAndBreak( 1, player, onBreakCallback );
                stackToFix.hurt( -getConfig().REPAIR_OTHERS.durRestoredPassively.next( random ), random, player instanceof ServerPlayer serverPlayer ? serverPlayer : null );
            }
        }
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
        if( isCurio ) return TriggerType.CURIO_TICK;
        
        return random.nextInt( 2 ) == 0 ? TriggerType.USE : TriggerType.HELD;
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
    
    @Override
    @Nullable
    public MutableComponent getAbilityDescription( @Nullable TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        if( type == null ) return null;
        
        return switch( type ) {
            case USE, HELD, CURIO_TICK, ARMOR_TICK -> descComponent( type );
            default -> null;
        };
    }
}
