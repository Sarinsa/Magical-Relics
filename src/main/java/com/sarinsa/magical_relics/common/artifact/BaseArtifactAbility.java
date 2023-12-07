package com.sarinsa.magical_relics.common.artifact;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class BaseArtifactAbility implements ArtifactAbility {

    private final Component description;


    public BaseArtifactAbility(String abilityName) {
        this(MagicalRelics.MODID, abilityName);
    }

    public BaseArtifactAbility(String modid, String abilityName) {
        this.description = Component.translatable(MagicalRelics.MODID + ".artifact_ability." + modid + "." + abilityName + ".description");
    }

    protected static String createPrefix(String abilityName, String prefix) {
        return MagicalRelics.MODID + ".artifact_ability." + MagicalRelics.MODID + "." + abilityName + ".prefix." + prefix;
    }

    protected static String createSuffix(String abilityName, String suffix) {
        return MagicalRelics.MODID + ".artifact_ability." + MagicalRelics.MODID + "." + abilityName + ".suffix." + suffix;
    }


    /**
     * @return The ActiveType of this artifact ability.
     */
    public abstract TriggerType getTriggerType();

    /**
     * @return A description of what this artifact ability does. Appended to
     * {@link net.minecraft.world.item.Item#appendHoverText(ItemStack, Level, List, TooltipFlag)}
     */
    public Component getAbilityDescription() {
        return description;
    }

    @Override
    public boolean onUse() {
        return false;
    }

    @Override
    public boolean onClickBlock(Level level, BlockPos pos, BlockState state, Direction face, Player player) {
        return false;
    }

    @Override
    public boolean onSneakClickBlock(Level level, BlockPos pos, BlockState state, Player player) {
        return false;
    }

    @Override
    public boolean onSneak() {
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
    public boolean onUserDamaged() {
        return false;
    }

    @Override
    public void tickPassiveEffect() {

    }

    @Override
    public String toString() {
        return "Description=[" + description.getString() + "], " +
                "TriggerType=[" + getTriggerType().name() + "]";
    }


    /**
     * Represents the type of trigger that activates an artifact ability, actively or passively.
     */
    public enum TriggerType {
        /** Main hand only. */
        MAIN_HAND(false),
        /** Activates when dropped on the ground. */
        DROPPED(false),
        /** Only active when equipped as armor. */
        ARMOR(true),
        /** Active if held, or in hotbar. */
        HOTBAR(true),
        /** Active always, regardless of inventory slot. */
        INVENTORY(true);

        TriggerType(boolean canStack) {
            this.canStack = canStack;
        }

        /**
         * Whether more than one ability with this
         * trigger type can exist on the same artifact.
         */
        final boolean canStack;

        public boolean canStack() {
            return canStack;
        }
    }
}
