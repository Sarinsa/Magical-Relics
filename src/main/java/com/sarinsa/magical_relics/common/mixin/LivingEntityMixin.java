package com.sarinsa.magical_relics.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.sarinsa.magical_relics.common.util.mixin_hooks.ClientMixinHooks;
import com.sarinsa.magical_relics.common.util.mixin_hooks.CommonMixinHooks;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( LivingEntity.class )
public abstract class LivingEntityMixin extends Entity implements Attackable {
    
    
    protected LivingEntityMixin( EntityType<? extends LivingEntity> type, Level level ) {
        super( type, level );
    }
    
    
    @Inject(
            method = "onClimbable",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;blockPosition()Lnet/minecraft/core/BlockPos;" ),
            cancellable = true
    )
    public void inject_onClimbable( CallbackInfoReturnable<Boolean> cir ) {
        CommonMixinHooks.inject_onClimbable( cir, (LivingEntity) (Object) this );
    }
    
    @Inject(
            method = "forceAddEffect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
            )
    )
    public void inject_forceAddEffect( CallbackInfo ci, @Local( argsOnly = true ) LocalRef<MobEffectInstance> localRef ) {
        ClientMixinHooks.inject_forceAddEffect( localRef );
    }
}
