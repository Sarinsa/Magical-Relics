package com.sarinsa.magical_relics.common.ability;

import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class BaseArtifactAbility {

    public BaseArtifactAbility() {

    }

    /** Helper method for creating artifact prefixes. */
    protected static String createPrefix(String abilityName, String prefix) {
        return MagicalRelics.MODID + ".artifact_ability." + MagicalRelics.MODID + "." + abilityName + ".prefix." + prefix;
    }

    /** Helper method for creating artifact suffixes. */
    protected static String createSuffix(String abilityName, String suffix) {
        return MagicalRelics.MODID + ".artifact_ability." + MagicalRelics.MODID + "." + abilityName + ".suffix." + suffix;
    }


    /**
     * @return An array of possible translatable prefixes for this ability.
     */
    public abstract String[] getPrefixes();

    /**
     * @return An array of possible translatable suffixes for this ability.
     */
    public abstract String[] getSuffixes();
    /**
     * @return A random TriggerType that should be used for this ability when attached to an
     *         artifact item stack.
     */
    @Nullable
    public abstract TriggerType getRandomTrigger(RandomSource random, boolean isArmor, boolean isCurio);

    /**
     * @return A List of trigger types supported by this ability. This is not super
     *         important; primarily utilized in the "apply ability" command.
     */
    @Nonnull
    public abstract List<TriggerType> supportedTriggers();

    /**
     * @return A List of artifact categories this ability is compatible with.
     */
    public abstract List<ArtifactCategory> getCompatibleTypes();

    /**
     * @return True if a "snowflake" symbol should be prepended to this ability's description
     *         when it is on cooldown.
     */
    public boolean showCooldownSymbol() {
        return true;
    }

    /**
     * @return A description of this ability that will be added to the artifact item stack's tooltip.
     */
    public abstract MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag);

    /**
     * Called from {@link ArtifactUtils#generateRandomArtifact(RandomSource, boolean)} when the ability
     * is applied to an artifact item.
     * <br><br>
     * Can be used to write additional data to the ItemStack's NBT and whatnot.
     */
    public void onAbilityAttached(ItemStack artifact, RandomSource randomSource) {

    }

    /**
     * Only relevant for when an ability is attached to an artifact that is an instance of {@link com.sarinsa.magical_relics.common.item.ArtifactItem}.<br><br>
     * This is called whenever an artifact item that can be equipped in a Curio slot is unequipped.
     */
    public void onUnequipped(SlotContext slotContext, ItemStack artifact) {

    }

    public boolean onUse(Level level, Player player, ItemStack artifact) {
        return false;
    }

    public boolean onClickBlock(Level level, ItemStack artifact, BlockPos pos, BlockState state, Direction face, Player player) {
        return false;
    }

    public void onHeld(Level level, Player player, ItemStack artifact, EquipmentSlot slot) {
    }

    public boolean onDropped(Level level, ItemEntity itemEntity, Player player) {
        return false;
    }

    public void onDamageMob(ItemStack artifact, Player player, LivingEntity attackedMob) {

    }

    public void onUserDamaged(Level level, Player player, DamageSource damageSource, ItemStack artifact) {

    }

    /**
     * Called when the player dies (only for held artifact items, armor and curio artifact items)<br><br>
     *
     * @param slot The equipment slot of the artifact item. This will be null if the artifact item is equipped in a curio slot.
     * @param slotContext The Curios slot context of the artifact item. This will be null if the artifact item is equipped in any vanilla slots.
     */
    public void onDeath(Level level, Player player, @Nullable EquipmentSlot slot, @Nullable SlotContext slotContext, ItemStack artifact, LivingDeathEvent event) {

    }

    public void onInventoryTick(ItemStack artifact, Level level, Entity entity, int slot, boolean isSelectedItem) {

    }

    public void onArmorTick(ItemStack artifact, Level level, Player player, EquipmentSlot slot) {

    }

    public void onCurioTick(ItemStack artifact, Level level, Player player, SlotContext slotContext) {

    }

    /**
     * Primarily used for the ability's description text color when
     * rendering it in item tooltip.
     */
    public Rarity getRarity() {
        return ArtifactUtils.COMMON_ABILITY;
    }

    public AttributeBoost getAttributeWithBoost() {
        return null;
    }

    @Override
    public String toString() {
        String regName = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().containsValue(this)
                ? MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey(this).toString()
                : "null";

        return "Registry name: " + regName + ", Instance: " + super.toString();
    }
}
