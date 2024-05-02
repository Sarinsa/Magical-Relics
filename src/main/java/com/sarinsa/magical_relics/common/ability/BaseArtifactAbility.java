package com.sarinsa.magical_relics.common.ability;

import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.commands.CommandSourceStack;
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
    public abstract TriggerType getRandomTrigger(RandomSource random, boolean isArmor);

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

    public boolean onUse(Level level, Player player, ItemStack artifact) {
        return false;
    }

    public boolean onClickBlock(Level level, ItemStack artifact, BlockPos pos, BlockState state, Direction face, Player player) {
        return false;
    }

    public void onHeld(Level level, Player player, ItemStack artifact) {
    }

    public boolean onDropped(Level level, ItemEntity itemEntity, Player player) {
        return false;
    }

    public void onDamageMob(ItemStack artifact, Player player, LivingEntity attackedMob) {

    }

    public void onUserDamaged(Level level, Player player, DamageSource damageSource, ItemStack artifact) {

    }

    public void onDeath(Level level, Player player, EquipmentSlot slot, ItemStack artifact, LivingDeathEvent event) {

    }

    public void onInventoryTick(ItemStack artifact, Level level, Entity entity, int slot, boolean isSelectedItem) {

    }

    public void onArmorTick(ItemStack artifact, Level level, Player player) {

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
