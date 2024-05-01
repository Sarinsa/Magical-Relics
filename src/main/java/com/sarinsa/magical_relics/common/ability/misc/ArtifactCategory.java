package com.sarinsa.magical_relics.common.ability.misc;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.List;

public enum ArtifactCategory {
    TRINKET("trinket", 10),
    SWORD("sword", 7),
    DAGGER("dagger", 6),
    STAFF("staff", 6),
    WAND("wand", 7),
    AMULET("amulet", 7),
    BELT("belt", 6),
    RING("ring", 8),
    FIGURINE("figurine", 5),
    HELMET("helmet", 1),
    CHESTPLATE("chestplate", 1),
    LEGGINGS("leggings", 1),
    BOOTS("boots", 1);

    public static final List<ArtifactCategory> ALL = ImmutableList.copyOf(values());


    ArtifactCategory(String name, int variations) {
        this.name = name;
        this.variations = variations;
    }
    final String name;
    final int variations;

    public String getName() {
        return name;
    }

    public int getVariations() {
        return variations;
    }

    @Nullable
    public static ArtifactCategory getFromName(String name) {
        for (ArtifactCategory artifactCategory : values()) {
            if (artifactCategory.getName().equals(name))
                return artifactCategory;
        }
        return null;
    }
}
