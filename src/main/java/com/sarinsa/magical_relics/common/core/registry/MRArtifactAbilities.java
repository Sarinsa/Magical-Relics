package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.artifact.BakerAbility;
import com.sarinsa.magical_relics.common.artifact.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.artifact.CashoutAbility;
import com.sarinsa.magical_relics.common.artifact.EmptyAbility;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MRArtifactAbilities {

    public static final DeferredRegister<BaseArtifactAbility> ARTIFACT_ABILITIES = DeferredRegister.create(new ResourceLocation(MagicalRelics.MODID, "artifact_abilities"), MagicalRelics.MODID);
    public static final Supplier<IForgeRegistry<BaseArtifactAbility>> ARTIFACT_ABILITY_REGISTRY = ARTIFACT_ABILITIES.makeRegistry(() -> {
        return (new RegistryBuilder<BaseArtifactAbility>()).setDefaultKey(MagicalRelics.resLoc("empty"));
    });



    public static final RegistryObject<BaseArtifactAbility> EMPTY = register("empty", EmptyAbility::new);
    public static final RegistryObject<BaseArtifactAbility> BAKER = register("baker", BakerAbility::new);
    public static final RegistryObject<BaseArtifactAbility> CASHOUT = register("cashout", CashoutAbility::new);



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
