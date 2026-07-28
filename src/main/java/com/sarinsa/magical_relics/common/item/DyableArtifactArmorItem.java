package com.sarinsa.magical_relics.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class DyableArtifactArmorItem extends ArmorItem implements IArtifactItem, DyeableLeatherItem {
    
    private final ArtifactCategory type;
    
    public DyableArtifactArmorItem( ArmorMaterial armorMaterial, ArtifactCategory type, ArmorItem.Type armorType, Properties properties ) {
        super( armorMaterial, armorType, properties.rarity( ArtifactUtils.RARITY_GLORIOUS ) );
        this.type = type;
    }
    
    @Override
    public ArtifactCategory getCategory() {
        return type;
    }
    
    @Override
    public Item artifactAsItem() {
        return asItem();
    }
    
    @Override
    public void appendHoverText( ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag ) {
        super.appendHoverText( itemStack, level, components, flag );
        
        ArtifactUtils.addDescriptionsToTooltip( itemStack, level, components, flag );
    }
    
    
    @SuppressWarnings( "removal" )
    @Override
    public void onArmorTick( ItemStack stack, Level level, Player player ) {
        Collection<BaseArtifactAbility<?>> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.ARMOR_TICK, stack );
        
        if( !abilities.isEmpty() ) {
            for( BaseArtifactAbility<?> ability : abilities ) {
                ability.onArmorTick( stack, level, player, getEquipmentSlot() );
            }
        }
    }
    
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers( EquipmentSlot slot, ItemStack stack ) {
        Multimap<Attribute, AttributeModifier> artifactModifiers = ArtifactUtils.getAttributeMods( stack, AttributeBoost.ActiveType.EQUIPPED );
        
        if( artifactModifiers != null && slot == getEquipmentSlot() ) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> attribs = ImmutableMultimap.builder();
            attribs.putAll( artifactModifiers );
            attribs.putAll( getDefaultAttributeModifiers( slot ) );
            return attribs.build();
        }
        return super.getAttributeModifiers( slot, stack );
    }
    
    @Override
    public boolean canApplyAtEnchantingTable( ItemStack stack, Enchantment enchantment ) {
        if( enchantment == Enchantments.MENDING )
            return false;
        
        return super.canApplyAtEnchantingTable( stack, enchantment );
    }
    
    @Override
    public boolean isBookEnchantable( ItemStack stack, ItemStack book ) {
        if( book.getEnchantmentLevel( Enchantments.MENDING ) > 0 )
            return false;
        
        return super.isBookEnchantable( stack, book );
    }
    
    @Override
    public boolean shouldCauseBlockBreakReset( ItemStack oldStack, ItemStack newStack ) {
        return !newStack.is( oldStack.getItem() );
    }
    
    /**
     * Overriding this so the equip/use animation does not constantly play
     * when dealing with artifact ability cooldown ticks changing NBT.
     */
    @Override
    public boolean shouldCauseReequipAnimation( ItemStack oldStack, ItemStack newStack, boolean slotChanged ) {
        return ArtifactUtils.shouldCauseReequipAnimation( oldStack, newStack );
    }
    
    @Override
    public Component getName( ItemStack itemStack ) {
        final Component name = ArtifactUtils.getItemDisplayName( itemStack );
        if( name == null ) return super.getName( itemStack );
        return name;
    }
}
