package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.entity.SwungSword;
import com.sarinsa.magical_relics.common.entity.VolatileFireball;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MREntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MagicalRelics.MODID);


    public static final RegistryObject<EntityType<VolatileFireball>> VOLATILE_FIREBALL = register("volatile_fireball",
            EntityType.Builder.<VolatileFireball>of(VolatileFireball::new, MobCategory.MISC).fireImmune().sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10));

    public static final RegistryObject<EntityType<SwungSword>> SWUNG_SWORD = register("swung_sword",
            EntityType.Builder.<SwungSword>of(SwungSword::new, MobCategory.MISC).fireImmune().noSave().sized(1.0F, 1.0F).noSummon());


    public static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(name));
    }
}
