package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.event.ServerEventListener;
import com.sarinsa.magical_relics.common.item.IArtifactItem;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
    
    
    public RepairOthersAbility() { }
    
    
    public static class RepairOthersAbilityConfig extends CooldownAbilityConfig {
        
        public RepairOthers REPAIR_OTHERS;
        
        public RepairOthersAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                          int cooldown ) {
            super( cfgManager, abilityId, cooldown );
            
            REPAIR_OTHERS = new RepairOthers( this );
        }
        
        public static class RepairOthers extends AbstractConfigCategory<RepairOthersAbilityConfig> {
            
            public IntField durRestoredOnUse;
            public IntField durRestoredPassively;
            
            public RepairOthers( RepairOthersAbilityConfig parent ) {
                super( parent, "repair_others", "Options for this ability repairing other items in the inventory." );
                
                durRestoredOnUse = SPEC.define( new IntField( "durability_restoration.active", 1, IntField.Range.POSITIVE,
                        "The amount of durability that is restored for the target item in the inventory when this ability has a use trigger." ) );
                
                durRestoredPassively = SPEC.define( new IntField( "durability_restoration.passive", 1, IntField.Range.POSITIVE,
                        "The amount of durability that is restored for the target item in the inventory when this ability has a passive trigger." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new RepairOthersAbilityConfig( cfgManager, abilityId, 20 );
    }
    
    @Override
    public boolean onUse( Level level, Player player, ItemStack artifact, @Nullable HitResult hitResult ) {
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            for( int i = 0; i < player.getInventory().getContainerSize(); i++ ) {
                ItemStack checkedStack = player.getInventory().getItem( i );
                
                if( !(checkedStack.getItem() instanceof IArtifactItem) && checkedStack.getDamageValue() > 0 ) {
                    if( !checkedStack.isEmpty() ) {
                        checkedStack.hurt( -getConfig().REPAIR_OTHERS.durRestoredOnUse.get(), level.random, player instanceof ServerPlayer serverPlayer ? serverPlayer : null );
                        artifact.hurtAndBreak( 1, player, ( p ) -> p.broadcastBreakEvent( EquipmentSlot.MAINHAND ) );
                        ArtifactUtils.setAbilityOnCooldown( artifact, this );
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void onHeld( Level level, Player player, ItemStack artifact, EquipmentSlot slot ) {
        if( level.isClientSide ) return;
        
        if( ServerEventListener.getRepairTick() % 200 == 0 ) {
            final List<ItemStack> fixCandidates = new ArrayList<>();
            
            // Collect all item stacks in the inventory that can have durability restored
            for( int i = 0; i < player.getInventory().getContainerSize(); i++ ) {
                ItemStack checkedStack = player.getInventory().getItem( i );
                
                if( !(checkedStack.getItem() instanceof IArtifactItem) && checkedStack.getDamageValue() > 0 ) {
                    if( !checkedStack.isEmpty() ) {
                        fixCandidates.add( checkedStack );
                    }
                }
            }
            // Pick a random "fixable" item to restore durability for
            if( !fixCandidates.isEmpty() ) {
                ItemStack stackToFix = fixCandidates.get( level.random.nextInt( fixCandidates.size() ) );
                
                // 1/3 chance to hurt the artifact. A 1-to-1 conversion ratio would not be much to brag about.
                if( level.random.nextInt( 3 ) == 0 )
                    stackToFix.hurt( -getConfig().REPAIR_OTHERS.durRestoredPassively.get(), level.random, player instanceof ServerPlayer serverPlayer ? serverPlayer : null );
                artifact.hurtAndBreak( 1, player, ( p ) -> p.broadcastBreakEvent( slot ) );
            }
        }
    }
    
    @Override
    public void onCurioTick( ItemStack artifact, Level level, Player player, SlotContext slotContext ) {
        if( level.isClientSide ) return;
        
        if( ServerEventListener.getRepairTick() % 200 == 0 ) {
            for( int i = 0; i < player.getInventory().getContainerSize(); i++ ) {
                ItemStack checkedStack = player.getInventory().getItem( i );
                
                if( !(checkedStack.getItem() instanceof IArtifactItem) && checkedStack.getDamageValue() > 0 ) {
                    if( !checkedStack.isEmpty() ) {
                        checkedStack.hurt( -getConfig().REPAIR_OTHERS.durRestoredPassively.get(), level.random, player instanceof ServerPlayer serverPlayer ? serverPlayer : null );
                        artifact.hurtAndBreak( 1, player, ( p ) -> CuriosApi.broadcastCurioBreakEvent( slotContext ) );
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public void onArmorTick( ItemStack artifact, Level level, Player player, EquipmentSlot slot ) {
        onHeld( level, player, artifact, slot );
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
    public List<ArtifactCategory> getCompatibleTypes() {
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
            case USE ->
                    Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.repair_others.description.use" );
            case HELD ->
                    Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.repair_others.description.held" );
            case CURIO_TICK ->
                    Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.repair_others.description.curio" );
            default ->
                    Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.repair_others.description.armor_tick" );
        };
    }
}
