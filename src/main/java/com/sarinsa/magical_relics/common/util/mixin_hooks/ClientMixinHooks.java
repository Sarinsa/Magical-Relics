package com.sarinsa.magical_relics.common.util.mixin_hooks;

import com.sarinsa.magical_relics.client.ClientRegister;
import com.sarinsa.magical_relics.common.core.config.sync.SyncedProperties;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ClientMixinHooks {
    
    public static void inject_controlBoat( Boat boat, CallbackInfo callbackInfo ) {
        if( boat.getControllingPassenger() instanceof Player player ) {
            if( ArtifactUtils.hasAbility( player.getItemBySlot( EquipmentSlot.CHEST ), MRArtifactAbilities.SAILOR.get() ) ) {
                final double multiplier = 1.0D + SyncedProperties.SAILOR_ABILITY_SPEED_MULT.getValue();
                boat.setDeltaMovement( boat.getDeltaMovement().multiply( multiplier, 1.0D, multiplier ) );
            }
        }
    }
    
    public static float modify_renderEffects( float original, MobEffectInstance effectInstance ) {
        if( effectInstance.isInfiniteDuration() ) return original;
        if( ClientRegister.CLIENT_CONFIG.MISC.noEffectIconFlicker.get() && effectInstance.getDuration() <= 1 )
            return 1.0F;
        return original;
    }
}
