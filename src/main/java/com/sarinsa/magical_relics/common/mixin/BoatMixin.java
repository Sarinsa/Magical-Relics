package com.sarinsa.magical_relics.common.mixin;

import com.sarinsa.magical_relics.common.util.mixin_hooks.ClientMixinHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( Boat.class )
public abstract class BoatMixin extends Entity implements IForgeBoat {
    
    public BoatMixin( EntityType<?> type, Level level ) {
        super( type, level );
    }
    
    @Inject(
            method = "controlBoat",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/vehicle/Boat;setPaddleState(ZZ)V",
                    ordinal = 0
            )
    )
    public void inject_controlBoat( CallbackInfo ci ) {
        ClientMixinHooks.inject_controlBoat( (Boat) (Object) this, ci );
    }
}
