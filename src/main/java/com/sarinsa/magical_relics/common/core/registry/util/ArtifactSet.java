package com.sarinsa.magical_relics.common.core.registry.util;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ArtifactSet<T> {

    private final String type;
    private final int variations;
    private final T dataStructure;

    public ArtifactSet(String type, int variations, @Nonnull T dataStructure) {
        Objects.requireNonNull(dataStructure);
        Objects.requireNonNull(type);
        this.type = type;
        this.variations = variations;
        this.dataStructure = dataStructure;
    }


    public String getType() {
        return type;
    }

    public int getVariations() {
        return variations;
    }

    public T getDataStructure() {
        return dataStructure;
    }
}
