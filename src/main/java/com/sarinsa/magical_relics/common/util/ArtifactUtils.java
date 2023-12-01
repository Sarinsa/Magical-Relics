package com.sarinsa.magical_relics.common.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ArtifactUtils {

    public static ItemStack generateRandomArtifact(RandomSource random) {
        return new ItemStack(Items.WOODEN_PICKAXE);
    }
}
