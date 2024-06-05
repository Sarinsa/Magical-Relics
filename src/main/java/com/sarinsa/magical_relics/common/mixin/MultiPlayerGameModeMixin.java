package com.sarinsa.magical_relics.common.mixin;

import com.sarinsa.magical_relics.common.util.mixin_hooks.ClientMixinHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Effectively stops artifact items from stopping block break progress
 * whenever their NBT changes from ability cooldown ticking or something else.
 * Call me crazy, but I could not for the life of me figure out a different way
 * of doing this.
 */
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow private ItemStack destroyingItem;

    @Shadow private BlockPos destroyBlockPos;

    @Inject(method = "sameDestroyTarget", at = @At("HEAD"), cancellable = true)
    public void onSameDestroyTarget(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ClientMixinHooks.onSameDestroyTargetHook(pos, destroyBlockPos, minecraft.player.getMainHandItem(), destroyingItem, cir);
    }
}
