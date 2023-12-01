package com.sarinsa.magical_relics.common.artifact;

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
    boolean onClickBlock();

    /**
     * Called when the player sneaks and right-clicks on a block with an artifact
     * <br><br>
     * @return True if durability should be decreased on the used artifact
     */
    boolean onSneakClickBlock();

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
