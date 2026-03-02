package com.sarinsa.magical_relics.client;

import com.sarinsa.magical_relics.client.particle.OrePingParticle;
import com.sarinsa.magical_relics.client.renderer.block.AntiBuilderRenderer;
import com.sarinsa.magical_relics.client.renderer.block.CamoTrapRenderer;
import com.sarinsa.magical_relics.client.renderer.block.DisplayPedestalRenderer;
import com.sarinsa.magical_relics.client.renderer.entity.SwungSwordRenderer;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.*;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

@Mod.EventBusSubscriber( value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = MagicalRelics.MODID )
public class ClientRegister {
    
    
    @SubscribeEvent
    public static void onClientSetup( FMLClientSetupEvent event ) {
        MinecraftForge.EVENT_BUS.register( new ClientEventListener() );
        
        // TODO - put this in a model file instead
        ItemBlockRenderTypes.setRenderLayer( MRBlocks.THICK_TRIPWIRE.get(), RenderType.tripwire() );
        
        ItemModelProps.register();
    }
    
    @SubscribeEvent
    public static void registerRenderers( EntityRenderersEvent.RegisterRenderers event ) {
        event.registerBlockEntityRenderer( MRBlockEntities.DISPLAY_PEDESTAL.get(), DisplayPedestalRenderer::new );
        event.registerBlockEntityRenderer( MRBlockEntities.CAMO_DISPENSER.get(), CamoTrapRenderer::new );
        event.registerBlockEntityRenderer( MRBlockEntities.CAMO_TRIPWIRE_HOOK.get(), CamoTrapRenderer::new );
        event.registerBlockEntityRenderer( MRBlockEntities.ILLUSIONARY_BLOCK.get(), CamoTrapRenderer::new );
        event.registerBlockEntityRenderer( MRBlockEntities.ANTI_BUILDER.get(), AntiBuilderRenderer::new );
        
        event.registerEntityRenderer( MREntities.VOLATILE_FIREBALL.get(), ( context ) -> new ThrownItemRenderer<>( context, 3.0F, true ) );
        event.registerEntityRenderer( MREntities.SWUNG_SWORD.get(), SwungSwordRenderer::new );
    }
    
    @SubscribeEvent
    public static void registerParticles( RegisterParticleProvidersEvent event ) {
        event.registerSpriteSet( MRParticles.ORE_PING.get(), OrePingParticle.Factory::new );
    }
    
    @SubscribeEvent
    public static void onItemColors( RegisterColorHandlersEvent.Item event ) {
        // All artifact items (excluding armor)
        for( List<RegistryObject<? extends Item>> list : MRItems.ARTIFACTS_BY_CATEGORY.values() ) {
            for( RegistryObject<? extends Item> regObj : list ) {
                if( regObj.get() instanceof ArmorItem ) continue;
                
                event.register( ( itemStack, index ) -> {
                    if( index > 0 ) {
                        CompoundTag stackTag = itemStack.getTag();
                        
                        if( stackTag == null ) return -1;
                        
                        if( stackTag.contains( ArtifactUtils.TAG_MOD_DATA, Tag.TAG_COMPOUND ) && stackTag.getCompound( ArtifactUtils.TAG_MOD_DATA ).contains( ArtifactUtils.TAG_ITEM_COLOR ) ) {
                            return stackTag.getCompound( ArtifactUtils.TAG_MOD_DATA ).getInt( ArtifactUtils.TAG_ITEM_COLOR );
                        }
                    }
                    return -1;
                }, regObj.get() );
            }
        }
        
        // Dyable leather items
        for( RegistryObject<Item> regObj : MRItems.ITEMS.getEntries() ) {
            Item item = regObj.get();
            
            if( item instanceof DyeableLeatherItem dyableItem ) {
                event.register( ( itemStack, index ) -> index > 0 ? -1 : dyableItem.getColor( itemStack ), regObj.get() );
            }
        }
    }
    
    @SubscribeEvent
    public static void onRegisterAdditionalModels( ModelEvent.RegisterAdditional event ) {
        event.register( MagicalRelics.rl( "blockentity/anti_builder_dir_indicator" ) );
    }
}
