package com.sarinsa.magical_relics.common.util.mixin_hooks;

import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.core.registry.MRStructureProcessors;
import com.sarinsa.magical_relics.common.mixin.StructureProcessorAccessor;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class CommonMixinHooks {
    
    public static void inject_onClimbable( CallbackInfoReturnable<Boolean> cir, LivingEntity livingEntity ) {
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
    
    public static void inject_placeInWorld( StructurePlaceSettings settings ) {
        if( settings.getProcessors().stream().anyMatch( processor ->
                ((StructureProcessorAccessor) processor).callGetType() == MRStructureProcessors.NO_WATERLOGGING.get() ) ) {
            settings.setKeepLiquids( false );
        }
    }
}
