package com.sarinsa.magical_relics.common.worldgen;

import com.sarinsa.magical_relics.common.worldgen.processor.CustomAgingProcessor;

public class WorldgenHelper {


    public static void bootstrap() {
        CustomAgingProcessor.init();
    }
}
