package com.sarinsa.magical_relics.common.util.mixin_hooks;

import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.tag.MRItemTags;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ClientMixinHooks {
    
    public static void onSameDestroyTargetHook( BlockPos pos, BlockPos destroyBlockPos, ItemStack itemStack, ItemStack destroyingItem, CallbackInfoReturnable<Boolean> cir ) {
        if( itemStack.is( MRItemTags.ARTIFACTS ) ) {
            if( pos.equals( destroyBlockPos ) && !destroyingItem.shouldCauseBlockBreakReset( itemStack ) ) {
                cir.setReturnValue( true );
            }
        }
    }
    
    public static void onControlBoat( Boat boat, CallbackInfo callbackInfo ) {
        if( boat.getControllingPassenger() instanceof Player player ) {
            if( ArtifactUtils.hasAbility( player.getItemBySlot( EquipmentSlot.CHEST ), MRArtifactAbilities.SAILOR.get() ) ) {
                // TODO - Send value from server to client.
                final double multiplier = 1.0D + MRArtifactAbilities.SAILOR.get().getConfig().SAILOR.speedMultiplier.get();
                boat.setDeltaMovement( boat.getDeltaMovement().multiply( multiplier, 1.0D, multiplier ) );
            }
        }
    }
}
