package com.sarinsa.magical_relics.common.artifact;

import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class AirSneakAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("air_sneak", "floaty"),
            createPrefix("air_sneak", "light"),
            createPrefix("air_sneak", "airy")
    };

    private static final String[] SUFFIXES = {
            createSuffix("air_sneak", "hermes"),
            createSuffix("air_sneak", "flight")
    };



    public AirSneakAbility() {
        super("air_sneak");
    }

    @Override
    public boolean onHeld(Level level, Player player, ItemStack heldArtifact) {
        BlockPos belowPos = player.blockPosition().below().immutable();

        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                if (level.getBlockState(belowPos).isAir() && !level.getBlockState(belowPos).is(MRBlocks.SOLID_AIR.get())) {
                    level.setBlock(belowPos, MRBlocks.SOLID_AIR.get().defaultBlockState(), Block.UPDATE_ALL);
                    level.scheduleTick(belowPos, MRBlocks.SOLID_AIR.get(), 20);
                }
            }
            else {
                if (level.getBlockState(belowPos).is(MRBlocks.SOLID_AIR.get()))
                    level.removeBlock(belowPos, false);
            }
        }
        return false;
    }

    @Override
    public String[] getPrefixes() {
        return PREFIXES;
    }

    @Override
    public String[] getSuffixes() {
        return SUFFIXES;
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.HELD;
    }
}
