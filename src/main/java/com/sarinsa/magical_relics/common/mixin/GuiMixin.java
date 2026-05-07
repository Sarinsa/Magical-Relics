package com.sarinsa.magical_relics.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.sarinsa.magical_relics.common.util.mixin_hooks.ClientMixinHooks;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin( Gui.class )
public abstract class GuiMixin {
    
    
    @ModifyVariable(
            method = "renderEffects",
            at = @At(
                    value = "LOAD",
                    target = "Lnet/minecraft/util/Mth;clamp(FFF)F",
                    ordinal = 0
            ),
            name = "f",
            index = 14,
            ordinal = 0
    )
    public float modify_renderEffects( float original, @Local( ordinal = 0 ) MobEffectInstance effectInstance ) {
        return ClientMixinHooks.modify_renderEffects( original, effectInstance );
    }
}
