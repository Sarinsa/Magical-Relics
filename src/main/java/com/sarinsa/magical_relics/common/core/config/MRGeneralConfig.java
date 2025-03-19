package com.sarinsa.magical_relics.common.core.config;

import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class MRGeneralConfig {

    public static final MRGeneralConfig.Config CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    static {
        Pair<MRGeneralConfig.Config, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(MRGeneralConfig.Config::new);
        CONFIG = commonPair.getLeft();
        CONFIG_SPEC = commonPair.getRight();
    }


    public static final class Config {

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> unobtainableAbilities;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> wizardFavoriteBlacklist;
        public final ForgeConfigSpec.ConfigValue<Boolean> enableAntiBuilderBlock;


        private Config(ForgeConfigSpec.Builder configBuilder) {
            unobtainableAbilities = configBuilder.comment("A list of artifact abilities that should be unobtainable (essentially disabled). Abilities will still function, " +
                    "but will not be applied to artifacts when artifacts are generated. Changes do not apply until game has been restarted.")
                    .defineListAllowEmpty("unobtainableAbilities", List.of(), ConfigUtil.IS_RESOURCE_LOCATION);

            wizardFavoriteBlacklist = configBuilder.comment("This is a list of IDs for items that should not be chosen as a random 'Wizard's Favorite' item. When certain wizard towers generate in the world, " +
                            "a display pedestal can be found in the structure containing a completely random item (the Wiz' favorite item). Changes do not apply until game has been restarted.")
                    .defineListAllowEmpty("wizardFavoriteBlacklist", Config::wizardFavoriteBlacklistDefaults, ConfigUtil.IS_RESOURCE_LOCATION);

            enableAntiBuilderBlock = configBuilder.comment("Enable or disable the AntiBuilderBlock feature. (Default true)")
                    .define("enableAntiBuilderBlock", true);
        }

        @SuppressWarnings("all")
        private static List<? extends String> wizardFavoriteBlacklistDefaults() {
            List<? extends String> values = new ArrayList<>();

            ConfigUtil.addToList((List<String>) values, ForgeRegistries.ITEMS, Items.BEDROCK);
            ConfigUtil.addToList((List<String>) values, ForgeRegistries.ITEMS, Items.STRUCTURE_BLOCK);
            ConfigUtil.addToList((List<String>) values, ForgeRegistries.ITEMS, Items.JIGSAW);
            ConfigUtil.addToList((List<String>) values, ForgeRegistries.ITEMS, Items.STRUCTURE_VOID);
            ConfigUtil.addToList((List<String>) values, ForgeRegistries.ITEMS, Items.BARRIER);
            ConfigUtil.addToList((List<String>) values, ForgeRegistries.ITEMS, Items.DEBUG_STICK);
            ConfigUtil.addToList((List<String>) values, ForgeRegistries.ITEMS, Items.AIR);
            ConfigUtil.addToList((List<String>) values, ForgeRegistries.ITEMS, Items.SPAWNER);

            return values;
        }
    }
}
