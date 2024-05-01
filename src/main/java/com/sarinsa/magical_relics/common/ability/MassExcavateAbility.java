package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MassExcavateAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("mass_excavate", "miners"),
            createPrefix("mass_excavate", "digging"),
            createPrefix("mass_excavate", "excavating")
    };

    private static final String[] SUFFIXES = {
            createSuffix("mass_excavate", "drilling"),
            createSuffix("mass_excavate", "tunneling")
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.RIGHT_CLICK_BLOCK
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.TRINKET,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.STAFF,
            ArtifactCategory.RING,
            ArtifactCategory.WAND
    );


    public MassExcavateAbility() {

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
    public boolean onClickBlock(Level level, ItemStack artifact, BlockPos pos, BlockState state, Direction face, Player player) {
        if (level.isClientSide) return false;

        if (!ArtifactUtils.isAbilityOnCooldown(artifact, this)) {
            if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                BlockPos pos1;
                BlockPos pos2;

                switch (face) {
                    // Up and default
                    default -> {
                        pos1 = pos.offset(1, 0, 1);
                        pos2 = pos.offset(-1, -2, -1);
                    }
                    case DOWN -> {
                        pos1 = pos.offset(1, 0, 1);
                        pos2 = pos.offset(-1,  2, -1);
                    }
                    case NORTH -> {
                        pos1 = pos.offset(1, -1, 0);
                        pos2 = pos.offset(-1,  1, 2);
                    }
                    case SOUTH -> {
                        pos1 = pos.offset(-1, -1, 1);
                        pos2 = pos.offset(1,  1, -2);
                    }
                    case EAST -> {
                        pos1 = pos.offset(0, -1, 1);
                        pos2 = pos.offset(-2,  1, -1);
                    }
                    case WEST -> {
                        pos1 = pos.offset(0, -1, 1);
                        pos2 = pos.offset(2,  1, -1);
                    }
                }

                boolean destroyedAnyBlocks = false;

                for (BlockPos nextPos : BlockPos.betweenClosed(pos1, pos2)) {
                    if (checkAndMineBlock((ServerLevel) level, nextPos, player)) {
                        destroyedAnyBlocks = true;
                    }
                }
                if (destroyedAnyBlocks) {
                    artifact.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(player.getUsedItemHand()));
                    ArtifactUtils.setAbilityCooldown(artifact, this, 20);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean checkAndMineBlock(ServerLevel level, BlockPos pos, Player player) {
        if (ForgeEventFactory.getMobGriefingEvent(level, player)) {
            BlockState state = level.getBlockState(pos);

            if (state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                if (!player.isCreative()) {
                    Block.dropResources(state, level, pos);
                }
                level.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.5F, 1.0F);
                level.removeBlock(pos, false);
                return true;
            }
        }
        return false;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Nullable
    @Override
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor) {
        return isArmor ? null : TriggerType.RIGHT_CLICK_BLOCK;
    }

    @NotNull
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }

    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }

    @Override
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.mass_excavate.description");
    }
}
