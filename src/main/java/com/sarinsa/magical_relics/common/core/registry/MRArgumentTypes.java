package com.sarinsa.magical_relics.common.core.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import com.sarinsa.magical_relics.common.command.argument.AbilityArgument;
import com.sarinsa.magical_relics.common.command.argument.ArtifactCategoryArgument;
import com.sarinsa.magical_relics.common.command.argument.TriggerTypeArgument;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MRArgumentTypes {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, MagicalRelics.MODID);


    public static final RegistryObject<ArgumentTypeInfo<ArtifactCategoryArgument, ?>> ARTIFACT_CATEGORY = register("artifact_category", () -> ArgumentTypeInfos.registerByClass(ArtifactCategoryArgument.class, SingletonArgumentInfo.contextFree(ArtifactCategoryArgument::artifactCategory)));
    public static final RegistryObject<ArgumentTypeInfo<AbilityArgument, ?>> ABILITY = register("ability", () -> ArgumentTypeInfos.registerByClass(AbilityArgument.class, SingletonArgumentInfo.contextFree(AbilityArgument::ability)));
    public static final RegistryObject<ArgumentTypeInfo<TriggerTypeArgument, ?>> TRIGGER_TYPE = register("trigger_type", () -> ArgumentTypeInfos.registerByClass(TriggerTypeArgument.class, SingletonArgumentInfo.contextFree(TriggerTypeArgument::triggerType)));



    private static <T extends ArgumentType<?>> RegistryObject<ArgumentTypeInfo<T, ?>> register(String name, Supplier<ArgumentTypeInfo<T, ?>> supplier) {
        return ARGUMENT_TYPES.register(name, supplier);
    }
}
