package com.sarinsa.magical_relics.common.util.mixin_hooks;

import com.sarinsa.magical_relics.common.tag.MRItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ClientMixinHooks {

    public static void onSameDestroyTargetHook(BlockPos pos, BlockPos destroyBlockPos, ItemStack itemStack, ItemStack destroyingItem, CallbackInfoReturnable<Boolean> cir) {
        if (itemStack.is(MRItemTags.ARTIFACTS)) {
            if (pos.equals(destroyBlockPos) && !destroyingItem.shouldCauseBlockBreakReset(itemStack)) {
                cir.setReturnValue(true);
            }
        }
    }
}
