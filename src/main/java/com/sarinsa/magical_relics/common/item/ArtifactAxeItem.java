package com.sarinsa.magical_relics.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.sarinsa.magical_relics.common.ability.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class ArtifactAxeItem extends AxeItem implements ItemArtifact {
    
    public ArtifactAxeItem( Tier tier, float attackDamage, float attackSpeed ) {
        super( tier, attackDamage, attackSpeed, new Properties().rarity( ArtifactUtils.MAGICAL ).stacksTo( 1 ) );
    }
    
    @Override
    public ArtifactCategory getCategory() {
        return ArtifactCategory.AXE;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use( Level level, Player player, InteractionHand hand ) {
        ItemStack heldItem = player.getItemInHand( hand );
        Collection<BaseArtifactAbility> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.USE, heldItem );
        
        if( !abilities.isEmpty() ) {
            for( BaseArtifactAbility ability : abilities ) {
                if( ability.onUse( level, player, heldItem ) ) {
                    return InteractionResultHolder.success( heldItem );
                }
            }
        }
        return InteractionResultHolder.pass( heldItem );
    }
    
    @Override
    public InteractionResult useOn( UseOnContext context ) {
        ItemStack heldItem = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState clickedState = level.getBlockState( pos );
        Player player = context.getPlayer();
        Collection<BaseArtifactAbility> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.RIGHT_CLICK_BLOCK, heldItem );
        
        // Help prevent stupid things from happening
        // when holding a potentially dangerous artifact
        // when trying to interact with a block entity.
        // It do be sad when your chest full of diamonds
        // go bye bye and turns into cake.
        if( clickedState.hasBlockEntity() ) return InteractionResult.PASS;
        
        if( !abilities.isEmpty() ) {
            for( BaseArtifactAbility ability : abilities ) {
                if( ability.onClickBlock( level, heldItem, pos, level.getBlockState( pos ), context.getClickedFace(), player ) )
                    return InteractionResult.SUCCESS;
            }
        }
        return super.useOn( context );
    }
    
    @Override
    public void appendHoverText( ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag ) {
        super.appendHoverText( itemStack, level, components, flag );
        
        ArtifactUtils.addDescriptionsToTooltip( itemStack, level, components, flag );
    }
    
    @Override
    public void inventoryTick( ItemStack itemStack, Level level, Entity entity, int slot, boolean isSelectedItem ) {
        Collection<BaseArtifactAbility> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.INVENTORY_TICK, itemStack );
        
        if( !abilities.isEmpty() ) {
            for( BaseArtifactAbility ability : abilities ) {
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
