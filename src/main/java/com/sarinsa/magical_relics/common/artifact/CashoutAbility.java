package com.sarinsa.magical_relics.common.artifact;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class CashoutAbility extends BaseArtifactAbility {

    private static final ResourceLocation LOOT_TABLE = MagicalRelics.resLoc("misc/cashout_ability");

    private static final String[] PREFIXES = {
            createPrefix("cashout", "valuable"),
            createPrefix("cashout", "precious")
    };

    private static final String[] SUFFIXES = {
            createSuffix("cashout", "wealth"),
            createSuffix("cashout", "riches"),
            createSuffix("cashout", "money"),
            createSuffix("cashout", "treasure"),
    };


    public CashoutAbility() {
        super("cashout");
    }


    @Override
    public boolean onDropped(Level level, ItemEntity itemEntity, Player player) {
        if (level instanceof ServerLevel serverLevel) {
            LootTable lootTable = serverLevel.getServer().getLootTables().get(LOOT_TABLE);

            if (lootTable == LootTable.EMPTY)
                return false;

            LootContext.Builder builder = new LootContext.Builder(serverLevel)
                    .withRandom(serverLevel.random)
                    .withParameter(LootContextParams.ORIGIN, itemEntity.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player);

            ObjectArrayList<ItemStack> loot = lootTable.getRandomItems(builder.create(LootContextParamSets.GIFT));

            for (ItemStack itemStack : loot) {
                Block.popResource(serverLevel, itemEntity.blockPosition(), itemStack);
            }
        }
        return true;
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
        return TriggerType.DROPPED;
    }
}
