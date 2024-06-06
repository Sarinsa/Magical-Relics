package com.sarinsa.magical_relics.common.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to identify ability config builders
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AbilityConfig {

    /**
     * @return A String representing the ID of this ability in the abilities-config.<br><br>
     *
     *         ID does not need to exactly match the registry ID of the ability object for this config entry,
     *         but probably should anyways since the ID needs to be unique in order to not accidentally override
     *         any other entries in the config.
     */
    String abilityId();
}
