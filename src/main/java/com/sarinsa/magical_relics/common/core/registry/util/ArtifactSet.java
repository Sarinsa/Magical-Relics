package com.sarinsa.magical_relics.common.core.registry.util;

import javax.annotation.Nonnull;
import java.util.Objects;

public record ArtifactSet<T>(String type, int variants, T dataStructure) {

    public ArtifactSet(String type, int variants, @Nonnull T dataStructure) {
        Objects.requireNonNull(dataStructure);
        Objects.requireNonNull(type);
        this.type = type;
        this.variants = variants;
        this.dataStructure = dataStructure;
    }
}
