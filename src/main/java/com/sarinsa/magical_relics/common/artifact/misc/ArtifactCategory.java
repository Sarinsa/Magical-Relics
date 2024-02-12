package com.sarinsa.magical_relics.common.artifact.misc;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum ArtifactCategory {
    TRINKET,
    SWORD,
    DAGGER,
    STAFF,
    WAND,
    AMULET,
    BELT,
    RING,
    FIGURINE,
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS;

    public static final List<ArtifactCategory> ALL = ImmutableList.copyOf(values());
}
