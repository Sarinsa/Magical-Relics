package com.sarinsa.magical_relics.datagen.loot;

import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public class MRBlockLoot extends BlockLoot {

    private final Set<Block> knownBlocks = new HashSet<>();

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return this.knownBlocks;
    }

    @Override
    protected void add(Block block, LootTable.Builder table) {
        super.add(block, table);
        this.knownBlocks.add(block);
    }

    @Override
    protected void addTables() {
        MRBlocks.WALL_PRESSURE_PLATES.keySet().forEach(block -> dropSelf(block.get()));

        dropSelf(MRBlocks.SPIKE_TRAP.get());
        dropSelf(MRBlocks.CAMO_DISPENSER.get());
        dropSelf(MRBlocks.CAMO_TRIPWIRE_HOOK.get());
        dropSelf(MRBlocks.THICK_TRIPWIRE.get());
        dropSelf(MRBlocks.DISPLAY_PEDESTAL.get());
    }
}