package com.sarinsa.magical_relics.common.mixin;

import com.sarinsa.magical_relics.common.util.mixin_hooks.CommonMixinHooks;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {

    protected LivingEntityMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "onClimbable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;blockPosition()Lnet/minecraft/core/BlockPos;"), cancellable = true)
    public void injectOnClimbable(CallbackInfoReturnable<Boolean> cir) {
        CommonMixinHooks.injectOnClimbable(cir, (LivingEntity) (Object) this);
    }
}
