package com.sarinsa.magical_relics.common.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRLootItemFunctions;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.Set;

public class PercentageSetDamageFunction extends LootItemConditionalFunction {

    final NumberProvider percentage;

    public PercentageSetDamageFunction(LootItemCondition[] conditions, NumberProvider percentage) {
        super(conditions);
        this.percentage = percentage;
    }

    @Override
    public LootItemFunctionType getType() {
        return MRLootItemFunctions.PERCENTAGE_SET_DAMAGE.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return percentage.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext context) {
        float percent = percentage.getFloat(context);

        if (itemStack.isDamageableItem() && (percent >= 0.01F && percent <= 1.0F)) {
            int maxDamage = itemStack.getMaxDamage();
            int desiredDamage = maxDamage - (int) (maxDamage * percent);
            itemStack.setDamageValue(desiredDamage);
        }
        else {
            MagicalRelics.LOG.warn("Couldn't set percentage damage of loot item {}", itemStack);
        }
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> setDamage(NumberProvider numberProvider) {
        return simpleBuilder((conditions) -> new PercentageSetDamageFunction(conditions, numberProvider));
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<PercentageSetDamageFunction> {

        @Override
        public void serialize(JsonObject jsonObject, PercentageSetDamageFunction function, JsonSerializationContext context) {
            super.serialize(jsonObject, function, context);
            jsonObject.add("percentage", context.serialize(function.percentage));
        }

        @Override
        public PercentageSetDamageFunction deserialize(JsonObject jsonObject, JsonDeserializationContext context, LootItemCondition[] conditions) {
            NumberProvider numberProvider = GsonHelper.getAsObject(jsonObject, "percentage", context, NumberProvider.class);
            return new PercentageSetDamageFunction(conditions, numberProvider);
        }
    }
}
