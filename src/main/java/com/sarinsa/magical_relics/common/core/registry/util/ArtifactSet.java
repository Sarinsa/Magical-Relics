package com.sarinsa.magical_relics.common.core.registry.util;

import com.sarinsa.magical_relics.common.artifact.misc.ArtifactCategory;

import javax.annotation.Nonnull;
import java.util.Objects;

public record ArtifactSet<T>(ArtifactCategory category, int variants, T dataStructure) {

    public ArtifactSet(ArtifactCategory category, int variants, @Nonnull T dataStructure) {
        Objects.requireNonNull(dataStructure);
        Objects.requireNonNull(category);
        this.category = category;
        this.variants = variants;
        this.dataStructure = dataStructure;
    }
}
