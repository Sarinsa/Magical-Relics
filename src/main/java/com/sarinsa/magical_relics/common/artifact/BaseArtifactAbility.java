package com.sarinsa.magical_relics.common.artifact;

import com.sarinsa.magical_relics.common.artifact.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.artifact.misc.AttributeBoost;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import javax.annotation.Nullable;
import java.util.List;

public abstract class BaseArtifactAbility implements ArtifactAbility {

    public BaseArtifactAbility() {

    }

    //Component.translatable(MagicalRelics.MODID + ".artifact_ability." + modid + "." + abilityName + ".description");


    /** Helper method for creating artifact prefixes. */
    protected static String createPrefix(String abilityName, String prefix) {
        return MagicalRelics.MODID + ".artifact_ability." + MagicalRelics.MODID + "." + abilityName + ".prefix." + prefix;
    }

    /** Helper method for creating artifact suffixes. */
    protected static String createSuffix(String abilityName, String suffix) {
        return MagicalRelics.MODID + ".artifact_ability." + MagicalRelics.MODID + "." + abilityName + ".suffix." + suffix;
    }


    /**
     * @return A random TriggerType that should be used for this ability when attached to an
     *         artifact item stack.
     */
    @Nullable
    public abstract TriggerType getRandomTrigger(RandomSource random, boolean isArmor);

    public abstract List<ArtifactCategory> getCompatibleTypes();

    /**
     * @return A description of this ability that will be added to the artifact item stack's tooltip.
     */
    public abstract MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag);

    /**
     * Called from {@link ArtifactUtils#generateRandomArtifact(RandomSource)} when the ability
     * is applied to the generated artifact item.
     * <br><br>
     * Can be used to write additional data to the ItemStack's NBT.
     */
    public void onAbilityAttached(ItemStack artifact, RandomSource randomSource) {

    }

    @Override
    public boolean onUse(Level level, Player player, ItemStack artifact) {
        return false;
    }

    @Override
    public boolean onClickBlock(Level level, ItemStack itemStack, BlockPos pos, BlockState state, Direction face, Player player) {
        return false;
    }

    @Override
    public boolean onHeld(Level level, Player player, ItemStack heldArtifact) {
        return false;
    }

    @Override
    public boolean onDropped(Level level, ItemEntity itemEntity, Player player) {
        return false;
    }

    @Override
    public boolean onDamageMob() {
        return false;
    }

    @Override
    public boolean onUserDamaged(Level level, Player player, @Nullable LivingEntity attacker, DamageSource damageSource, ItemStack artifact) {
        return false;
    }

    @Override
    public void onInventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean isSelectedItem) {

    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {

    }

    /**
     * Primarily used for the ability's description text color when
     * rendering it in item tooltip.
     */
    public Rarity getRarity() {
        return ArtifactUtils.COMMON_ABILITY;
    }

    @Override
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


    /**
     * Represents the type of trigger that activates an artifact ability.
     */
    public enum TriggerType {
        RIGHT_CLICK_BLOCK("right_click_block", false), // Activates when the player right-clicks a block with the artifact
        USE("use", false), // Activates when the player right-clicks with the artifact
        HELD("held", false), // Activates every tick while the artifact is held in main hand
        USER_DAMAGED("user_damaged", true), // Activates when the player takes damage
        USER_ATTACKING("user_attacking", true), // Activates when the player deals damage to a mob
        DROPPED("dropped", false), // Activates when the artifact is thrown out of the player's inventory
        ARMOR_TICK("armor_tick", true), // Activates when the artifact is equipped as armor, every tick
        INVENTORY_TICK("inventory_tick", true); // Activates every tick, regardless of where in the inventory the artifact is

        TriggerType(String name, boolean canStack) {
            this.name = name;
            this.canStack = canStack;
        }

        final String name;
        /**
         * Whether more than one ability with this
         * trigger type can exist on the same artifact.
         */
        final boolean canStack;


        public String getName() {
            return name;
        }

        public boolean canStack() {
            return canStack;
        }

        @Nullable
        public static TriggerType getFromName(String name) {
            for (TriggerType triggerType : values()) {
                if (triggerType.getName().equals(name))
                    return triggerType;
            }
            return null;
        }
    }
}
