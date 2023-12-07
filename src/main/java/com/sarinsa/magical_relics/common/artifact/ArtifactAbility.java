package com.sarinsa.magical_relics.common.artifact;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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
     * Called when the player sneaks and right-clicks on a block with an artifact
     * <br><br>
     * @return True if durability should be decreased on the used artifact
     */
    boolean onSneakClickBlock(Level level, BlockPos pos, BlockState state, Player player);

    /**
     * Called per tick when the player is sneaking with an artifact in their hand
     * <br><br>
     * @return True if durability should be decreased on the used artifact
     */
    boolean onSneak();

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
     * Called each tick, server-side. Artifacts do not need to be in
     * the player's hand for this to run.
     */
    void tickPassiveEffect();
}
