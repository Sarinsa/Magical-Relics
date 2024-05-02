package com.sarinsa.magical_relics.common.core.registry.util;

import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;

import javax.annotation.Nonnull;
import java.util.Objects;

public record ArtifactSet<T>(ArtifactCategory category, T dataStructure) {

    public ArtifactSet(ArtifactCategory category, @Nonnull T dataStructure) {
        Objects.requireNonNull(dataStructure);
        Objects.requireNonNull(category);
        this.category = category;
        this.dataStructure = dataStructure;
    }
}
