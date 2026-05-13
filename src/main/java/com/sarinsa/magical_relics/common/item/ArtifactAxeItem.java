package com.sarinsa.magical_relics.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class ArtifactAxeItem extends AxeItem implements IArtifactItem {
    
    public ArtifactAxeItem( Tier tier, float attackDamage, float attackSpeed ) {
        super( tier, attackDamage, attackSpeed, new Properties().rarity( ArtifactUtils.RARITY_GLORIOUS ).stacksTo( 1 ) );
    }
    
    @Override
    public ArtifactCategory getCategory() {
        return ArtifactCategory.AXE;
    }
    
    @Override
    public Item artifactAsItem() {
        return asItem();
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use( Level level, Player player, InteractionHand hand ) {
        final ItemStack heldItem = player.getItemInHand( hand );
        final List<BaseArtifactAbility<?>> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.USE, heldItem );
        
        if( !abilities.isEmpty() ) {
            InteractionResult result = abilities.get( 0 ).onUse( level, player, heldItem, hand, null );
            return new InteractionResultHolder<>( result, heldItem );
        }
        return InteractionResultHolder.pass( heldItem );
    }
    
    @Override
    public InteractionResult useOn( UseOnContext context ) {
        final ItemStack heldItem = context.getItemInHand();
        final Level level = context.getLevel();
        final Player player = context.getPlayer();
        final List<BaseArtifactAbility<?>> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.USE, heldItem );
        
        if( !abilities.isEmpty() ) {
            return abilities.get( 0 ).onUse( level, player, heldItem, context.getHand(), context.getHitResult() );
        }
        return super.useOn( context );
    }
    
    @Override
    public InteractionResult interactLivingEntity( ItemStack artifact, Player player, LivingEntity livingEntity, InteractionHand hand ) {
        final List<BaseArtifactAbility<?>> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.USE, artifact );
        final Level level = player.level();
        
        if( !abilities.isEmpty() ) {
            InteractionResult result = abilities.get( 0 ).onUse( level, player, artifact, hand, new EntityHitResult( livingEntity ) );
            
            // Force-update the item for creative players
            if( !level.isClientSide && player.isCreative() ) {
                player.setItemInHand( hand, artifact.copy() );
            }
            return result;
        }
        return InteractionResult.FAIL;
    }
    
    @Override
    public void appendHoverText( ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag ) {
        super.appendHoverText( itemStack, level, components, flag );
        
        ArtifactUtils.addDescriptionsToTooltip( itemStack, level, components, flag );
    }
    
    @Override
    public void inventoryTick( ItemStack itemStack, Level level, Entity entity, int slot, boolean isSelectedItem ) {
        Collection<BaseArtifactAbility<?>> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.INVENTORY_TICK, itemStack );
        
        if( !abilities.isEmpty() ) {
            for( BaseArtifactAbility<?> ability : abilities ) {
                ability.onInventoryTick( itemStack, level, entity, slot, isSelectedItem );
            }
        }
    }
    
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers( EquipmentSlot slot, ItemStack stack ) {
        Multimap<Attribute, AttributeModifier> artifactModifiers = ArtifactUtils.getAttributeMods( stack, AttributeBoost.ActiveType.HELD );
        
        if( artifactModifiers != null && slot == EquipmentSlot.MAINHAND ) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> attribs = ImmutableMultimap.builder();
            attribs.putAll( artifactModifiers );
            attribs.putAll( getDefaultAttributeModifiers( slot ) );
            return attribs.build();
        }
        return super.getAttributeModifiers( slot, stack );
    }
    
    @Override
    public boolean shouldCauseBlockBreakReset( ItemStack oldStack, ItemStack newStack ) {
        return !newStack.is( oldStack.getItem() );
    }
    
    @Override
    public boolean canApplyAtEnchantingTable( ItemStack stack, Enchantment enchantment ) {
        if( enchantment == Enchantments.MENDING )
            return false;
        
        return super.canApplyAtEnchantingTable( stack, enchantment );
    }
    
    /**
     * Overriding this so the equip/use animation does not constantly play
     * when dealing with artifact ability cooldown ticks changing NBT.
     */
    @Override
    public boolean shouldCauseReequipAnimation( ItemStack oldStack, ItemStack newStack, boolean slotChanged ) {
        return oldStack.getItem() != newStack.getItem();
    }
    
    @Override
    public Component getName( ItemStack itemStack ) {
        Component alteredName = ArtifactUtils.getItemDisplayName( itemStack );
        
        if( alteredName == null )
            return super.getName( itemStack );
        
        return alteredName;
    }
}
