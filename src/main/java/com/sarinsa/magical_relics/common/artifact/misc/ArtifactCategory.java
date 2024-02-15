package com.sarinsa.magical_relics.common.artifact.misc;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum ArtifactCategory {
    TRINKET("trinket"),
    SWORD("sword"),
    DAGGER("dagger"),
    STAFF("staff"),
    WAND("wand"),
    AMULET("amulet"),
    BELT("belt"),
    RING("ring"),
    FIGURINE("figurine"),
    HELMET("helmet"),
    CHESTPLATE("chestplate"),
    LEGGINGS("leggings"),
    BOOTS("boots");

    public static final List<ArtifactCategory> ALL = ImmutableList.copyOf(values());


    ArtifactCategory(String name) {
        this.name = name;
    }
    final String name;

    public String getName() {
        return name;
    }
}
