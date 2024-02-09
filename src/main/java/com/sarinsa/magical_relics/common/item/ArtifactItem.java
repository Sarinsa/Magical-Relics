package com.sarinsa.magical_relics.common.item;

import com.google.common.collect.Multimap;
import com.sarinsa.magical_relics.common.artifact.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Attr;

import java.util.List;

public class ArtifactItem extends TieredItem {

    public ArtifactItem(Tier tier) {
        super(tier, new Properties().rarity(ArtifactUtils.MAGICAL).stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        BaseArtifactAbility ability = ArtifactUtils.getFirstAbility(BaseArtifactAbility.TriggerType.USE, heldItem);

        if (ability != null) {
            if (ability.onUse(level, player, heldItem)) {
                heldItem.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(hand));
                return InteractionResultHolder.success(heldItem);
            }
        }
        return InteractionResultHolder.pass(heldItem);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack heldItem = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(pos);
        Player player = context.getPlayer();
        BaseArtifactAbility ability = ArtifactUtils.getFirstAbility(BaseArtifactAbility.TriggerType.RIGHT_CLICK_BLOCK, heldItem);

        // Help prevent stupid things from happening
        // when holding a potentially dangerous artifact
        // when trying to interact with a block entity.
        // It do be sad when your chest full of diamonds
        // go bye bye and turns into cake.
        if (clickedState.hasBlockEntity()) return InteractionResult.PASS;

        System.out.println("Ability: " + (ability == null ? "null" : ability) + " client: " + level.isClientSide);

        if (ability != null) {
            if (ability.onClickBlock(level, pos, level.getBlockState(pos), context.getClickedFace(), player)) {
                heldItem.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(context.getHand()));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, components, flag);

        List<Component> descriptions = ArtifactUtils.getDescriptions(itemStack);
        // Get a little space between name and description to break things up a bit
        components.add(Component.literal(""));
        components.addAll(descriptions);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.MENDING)
            return false;

        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> artifactModifiers = ArtifactUtils.getAttributeMods(stack);

        if (artifactModifiers != null && slot == EquipmentSlot.MAINHAND)
            return artifactModifiers;

        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        if (book.getEnchantmentLevel(Enchantments.MENDING) > 0)
            return false;

        return super.isBookEnchantable(stack, book);
    }

    /**
     * Overriding this so the equip/use animation does not constantly play
     * when dealing with artifact ability cooldown ticks changing NBT.
     */
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public Component getName(ItemStack itemStack) {
        Component alteredName = ArtifactUtils.getItemDisplayName(itemStack);

        if (alteredName == null)
            return super.getName(itemStack);

        return alteredName;
    }
}
