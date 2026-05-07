package com.sarinsa.magical_relics.common.util;

import net.minecraft.world.effect.MobEffectInstance;

/**
 * This implementation only exists to "wrap" mob effects on the client upon being applied,
 * such that they can be identified later on with an instanceof check.
 */
public class MarkedMobEffectInstance extends MobEffectInstance {
    
    /** @see com.sarinsa.magical_relics.client.ClientConfig.Misc#noEffectIconFlicker */
    public static final int TICK_THRESHOLD = 10;
    
    /**
     * Creates a new marked mob effect instance with all
     * properties copied from the given effect instance.
     */
    public MarkedMobEffectInstance( MobEffectInstance instance ) {
        super( instance );
        hiddenEffect = instance.hiddenEffect;
    }
}
