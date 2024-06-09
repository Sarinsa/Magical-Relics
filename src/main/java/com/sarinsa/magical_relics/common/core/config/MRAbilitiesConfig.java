package com.sarinsa.magical_relics.common.core.config;

import com.sarinsa.magical_relics.common.ability.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.annotations.AbilityConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;

public class MRAbilitiesConfig {

    public static final MRAbilitiesConfig.Config CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    static {
        Pair<MRAbilitiesConfig.Config, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(MRAbilitiesConfig.Config::new);
        CONFIG = commonPair.getLeft();
        CONFIG_SPEC = commonPair.getRight();
    }


    public static final class Config {


        private Config(ForgeConfigSpec.Builder configBuilder) {
            configBuilder.push("ability_properties");

            constructAbilityEntries(configBuilder);

            configBuilder.pop();
        }


        /**
         * Looks for classes that contain a valid ability config entries builder method and
         * attempts to invoke them.<br><br>
         * Valid target classes must:<br><br>
         *
         * - Contain a public static void method with the name 'buildEntries'<br>
         * - Said method must have a single parameter of type {@link ForgeConfigSpec.Builder}<br>
         * - Said method must be annotated with the {@link AbilityConfig} annotation
         */
        private void constructAbilityEntries(ForgeConfigSpec.Builder configBuilder) {
            ModList.get().getAllScanData().forEach(scanData -> {
                scanData.getAnnotations().forEach(annotationData -> {

                    // Look for classes annotated with @AbilityConfig
                    if (annotationData.annotationType().getClassName().equals(AbilityConfig.class.getName())) {
                        try {
                            Class<?> clazz = Class.forName(annotationData.clazz().getClassName());
                            String abilityId = (String) annotationData.annotationData().getOrDefault("abilityId", "");

                            if (abilityId == null || abilityId.isEmpty()) {
                                MagicalRelics.LOG.error("Failed to construct annotated ability config entry in {} due to lacking ability ID in the annotation", annotationData.clazz().getClassName());
                            }
                            Method configBuildMethod = clazz.getMethod("buildEntries", ForgeConfigSpec.Builder.class);

                            configBuilder.push(abilityId);
                            configBuildMethod.invoke(null, configBuilder);
                            configBuilder.pop();
                        }
                        catch (Exception e) {
                            MagicalRelics.LOG.error("Failed to construct annotated ability config entry in {}", annotationData.clazz().getClassName());
                            e.printStackTrace();
                        }
                    }
                });
            });
        }
    }
}
