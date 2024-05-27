package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.loot.functions.PercentageSetDamageFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MRLootItemFunctions {

    public static final DeferredRegister<LootItemFunctionType> LOOT_ITEM_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MagicalRelics.MODID);


    public static final RegistryObject<LootItemFunctionType> PERCENTAGE_SET_DAMAGE = LOOT_ITEM_FUNCTIONS.register("percentage_set_damage", () -> new LootItemFunctionType(new PercentageSetDamageFunction.Serializer()));
}
