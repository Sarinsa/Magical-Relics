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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ArtifactItem extends TieredItem implements ItemArtifact, ICurioItem {

    private final ArtifactCategory artifactCategory;

    public ArtifactItem(Tier tier, ArtifactCategory artifactCategory) {
        super(tier, new Properties().rarity(ArtifactUtils.MAGICAL).stacksTo(1));
        this.artifactCategory = artifactCategory;
    }

    @Override
    public ArtifactCategory getCategory() {
        return artifactCategory;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        BaseArtifactAbility ability = ArtifactUtils.getAbilityWithTrigger(TriggerType.USE, heldItem);

        if (ability != null) {
            if (ability.onUse(level, player, heldItem)) {
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
        BaseArtifactAbility ability = ArtifactUtils.getAbilityWithTrigger(TriggerType.RIGHT_CLICK_BLOCK, heldItem);

        // Help prevent stupid things from happening
        // when holding a potentially dangerous artifact
        // when trying to interact with a block entity.
        // It do be sad when your chest full of diamonds
        // go bye bye and turns into cake.
        if (clickedState.hasBlockEntity()) return InteractionResult.PASS;

        if (ability != null) {
            if (ability.onClickBlock(level, heldItem, pos, level.getBlockState(pos), context.getClickedFace(), player))
                return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        Map<BaseArtifactAbility, TriggerType> allAbilities = ArtifactUtils.getAllAbilities(stack);

        for (BaseArtifactAbility ability : allAbilities.keySet()) {
            ability.onUnequipped(slotContext, stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, components, flag);

        ArtifactUtils.addDescriptionsToTooltip(itemStack, level, components, flag);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean isSelectedItem) {
        BaseArtifactAbility ability = ArtifactUtils.getAbilityWithTrigger(TriggerType.INVENTORY_TICK, itemStack);

        if (ability != null) {
            ability.onInventoryTick(itemStack, level, entity, slot, isSelectedItem);
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.MENDING)
            return false;

        if (getCategory() == ArtifactCategory.SWORD || getCategory() == ArtifactCategory.DAGGER) {
            return enchantment.category == EnchantmentCategory.WEAPON;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> artifactModifiers = ArtifactUtils.getAttributeMods(stack, AttributeBoost.ActiveType.HELD);

        if (artifactModifiers != null && (slot == EquipmentSlot.MAINHAND)) {
            return artifactModifiers;
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> artifactModifiers = ArtifactUtils.getAttributeMods(stack, AttributeBoost.ActiveType.EQUIPPED);

        if (artifactModifiers != null) {
            return artifactModifiers;
        }
        return ImmutableMultimap.of();
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity attacked, LivingEntity attacker) {
        if (getCategory() == ArtifactCategory.SWORD || getCategory() == ArtifactCategory.DAGGER)
        itemStack.hurtAndBreak(1, attacker, (entity) -> {
            entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return artifactCategory == ArtifactCategory.DAGGER || artifactCategory == ArtifactCategory.SWORD
                ? !player.isCreative()
                : super.canAttackBlock(state, level, pos, player);
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState state) {
        if (artifactCategory == ArtifactCategory.SWORD) {
            if (state.is(Blocks.COBWEB)) {
                return 15.0F;
            }
            return state.is(BlockTags.SWORD_EFFICIENT) ? 1.0F : 1.5F;
        }
        return super.getDestroySpeed(itemStack, state);
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return !newStack.is(oldStack.getItem());
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
