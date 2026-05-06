package com.sarinsa.magical_relics.common.util.mixin_hooks;

import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class CommonMixinHooks {
    
    public static void injectOnClimbable( CallbackInfoReturnable<Boolean> cir, LivingEntity livingEntity ) {
        if( livingEntity instanceof Player player ) {
            for( EquipmentSlot slot : ArtifactUtils.ARMOR_SLOTS ) {
                if( ArtifactUtils.hasAbility( player.getItemBySlot( slot ), MRArtifactAbilities.SPIDER.get() ) ) {
                    if( player.horizontalCollision ) {
                        cir.setReturnValue( true );
                        return;
                    }
                }
            }
        }
    }
}
