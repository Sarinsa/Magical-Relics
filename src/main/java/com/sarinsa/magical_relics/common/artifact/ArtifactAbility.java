package com.sarinsa.magical_relics.common.artifact;

import com.sarinsa.magical_relics.common.artifact.misc.AttributeBoost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * The skeleton of an artifact ability.
 */
public interface ArtifactAbility {


    /**
     * Called when the player uses the artifact in their hand (right click)
     * <br><br>
     * @return True if durability should be decreased on the used artifact
     */
    boolean onUse();

    /**
     * Called when the player right-clicks on a block with an artifact
     * <br><br>
     * @return True if durability should be decreased on the used artifact
     */
    boolean onClickBlock(Level level, BlockPos pos, BlockState state, Direction face, Player player);

    /**
     * Called when the player deals damage to a mob with an artifact
     * <br><br>
     * @return True if durability should be decreased on the used artifact
     */
    boolean onDamageMob();

    /**
     * Called when the player throws an artifact with this ability out
     * of their inventory.
     * <br><br>
     * @return True if the ItemEntity should be removed/despawned.
     */
    boolean onDropped(Level level, ItemEntity itemEntity, Player player);

    /**
     * Called when the player takes damage. Artifacts do not need to be in
     * the player's hand for this to run.
     * <br><br>
     * @return True if durability should be decreased on the used artifact
     */
    boolean onUserDamaged();

    /**
     * Called when the player is holding an artifact with this ability in their hand,
     * <br><br>
     * @return True if durability should be decreased on the used artifact
     */
    boolean onHeld(Level level, Player player, ItemStack heldArtifact);

    /**
     * Called each tick, server-side. Artifacts do not need to be in
     * the player's hand for this to run.
     */
    void tickPassiveEffect();

    /**
     * @return An AttributeBoost instance containing an AttributeModifier and a double.
     *         The double will act as a random range between 0 and X
     *         for the modifier value that is applied to an artifact.
     */
    @Nullable
    AttributeBoost getAttributeWithBoost();

    /**
     * @return An array of translation key Strings representing the possible
     *         item name prefixes this ability grants.
     */
    String[] getPrefixes();

    /**
     * @return An array of translation key Strings representing the possible
     *         item name suffixes this ability grants.
     */
    String[] getSuffixes();
}
