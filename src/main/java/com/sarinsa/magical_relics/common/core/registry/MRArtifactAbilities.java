package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.artifact.*;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MRArtifactAbilities {

    public static final DeferredRegister<BaseArtifactAbility> ARTIFACT_ABILITIES = DeferredRegister.create(new ResourceLocation(MagicalRelics.MODID, "artifact_abilities"), MagicalRelics.MODID);
    public static final Supplier<IForgeRegistry<BaseArtifactAbility>> ARTIFACT_ABILITY_REGISTRY = ARTIFACT_ABILITIES.makeRegistry(()
            -> (new RegistryBuilder<BaseArtifactAbility>()).setDefaultKey(MagicalRelics.resLoc("empty")));



    public static final RegistryObject<BaseArtifactAbility> EMPTY = register("empty", EmptyAbility::new);
    public static final RegistryObject<BaseArtifactAbility> BAKER = register("baker", BakerAbility::new);
    public static final RegistryObject<BaseArtifactAbility> CASHOUT = register("cashout", CashoutAbility::new);
    public static final RegistryObject<BaseArtifactAbility> HEALTH_BOOST = register("health_boost", HealthBoostAbility::new);
    public static final RegistryObject<BaseArtifactAbility> SPEED_BOOST = register("speed_boost", SpeedAbility::new);
    public static final RegistryObject<BaseArtifactAbility> AIR_SNEAK = register("air_sneak", AirSneakAbility::new);
    public static final RegistryObject<BaseArtifactAbility> NIGHT_VISION = register("night_vision", NightVisionAbility::new);
    public static final RegistryObject<BaseArtifactAbility> ADRENALINE = register("adrenaline", AdrenalineAbility::new);
    public static final RegistryObject<BaseArtifactAbility> JUKEBOX = register("jukebox", JukeboxAbility::new);
    public static final RegistryObject<BaseArtifactAbility> WATER_BREATHING = register("water_breathing", WaterBreathingAbility::new);
    public static final RegistryObject<BaseArtifactAbility> OBSCURITY = register("obscurity", ObscurityAbility::new);
    public static final RegistryObject<BaseArtifactAbility> SAILOR = register("sailor", SailorAbility::new);
    public static final RegistryObject<BaseArtifactAbility> STUN = register("stun", StunAbility::new);


    private static RegistryObject<BaseArtifactAbility> register(String name, Supplier<BaseArtifactAbility> supplier) {
        return ARTIFACT_ABILITIES.register(name, supplier);
    }

    public static BaseArtifactAbility getOrDefault(ResourceLocation id) {
        for (BaseArtifactAbility type : ARTIFACT_ABILITY_REGISTRY.get().getValues()) {
            if (ARTIFACT_ABILITY_REGISTRY.get().getKey(type).equals(id))
                return type;
        }
        return EMPTY.get();
    }
}
