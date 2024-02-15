package com.sarinsa.magical_relics.common.worldgen;

import com.sarinsa.magical_relics.common.worldgen.processor.CustomMossifierProcessor;

public class WorldgenHelper {


    public static void bootstrap() {
        CustomMossifierProcessor.init();
    }
}
