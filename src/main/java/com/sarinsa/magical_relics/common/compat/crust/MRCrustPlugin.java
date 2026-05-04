package com.sarinsa.magical_relics.common.compat.crust;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import fathertoast.crust.api.CrustPlugin;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.ICrustPlugin;
import fathertoast.crust.api.entity.IPlayerVelocityWatcher;
import net.minecraft.resources.ResourceLocation;

@CrustPlugin
public class MRCrustPlugin implements ICrustPlugin {
    
    public static final ResourceLocation ID = MagicalRelics.rl( "crust_plugin" );
    
    private static IPlayerVelocityWatcher VELOCITY_WATCHER;
    
    
    @Override
    public void onLoad( ICrustApi apiInstance ) {
        VELOCITY_WATCHER = apiInstance.getPlayerVelocityWatcher();
    }
    
    @Override
    public ResourceLocation getId() {
        return ID;
    }
    
    /** @return Crust's player velocity watcher instance. */
    public static IPlayerVelocityWatcher getPlayerVelocityWatcher() {
        return VELOCITY_WATCHER;
    }
}
