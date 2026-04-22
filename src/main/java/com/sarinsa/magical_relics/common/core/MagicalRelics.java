package com.sarinsa.magical_relics.common.core;

import com.mojang.brigadier.CommandDispatcher;
import com.sarinsa.magical_relics.common.block.CamoDispenserBlock;
import com.sarinsa.magical_relics.common.command.MRBaseCommand;
import com.sarinsa.magical_relics.common.core.config.Config;
import com.sarinsa.magical_relics.common.core.config.sync.SyncedProperties;
import com.sarinsa.magical_relics.common.core.registry.*;
import com.sarinsa.magical_relics.common.event.MREventListener;
import com.sarinsa.magical_relics.common.event.ServerEventListener;
import com.sarinsa.magical_relics.common.network.PacketHandler;
import com.sarinsa.magical_relics.common.worldgen.WorldgenHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod( MagicalRelics.MODID )
public class MagicalRelics {
    
    // TODO LIST
    //
    // - Make it so slimes can spawn from spawner blocks regardless of vanilla spawn rules
    //
    // - Make separate models for Thick Tripwire (consider a slightly different THICCER texture as well?
    //
    // - More configurability for the anti-builder (specific blocked actions?)
    
    
    public static final String MODID = "magical_relics";
    public static final Logger LOG = LogManager.getLogger( MODID );
    
    
    public MagicalRelics( FMLJavaModLoadingContext context ) {
        IEventBus modBus = context.getModEventBus();
        ModContainer modContainer = context.getContainer();
        
        modBus.addListener( this::onCommonSetup );
        modBus.addListener( MRItems::onCreativeTabPopulate );
        
        MinecraftForge.EVENT_BUS.register( new MREventListener() );
        MinecraftForge.EVENT_BUS.register( new ServerEventListener() );
        MinecraftForge.EVENT_BUS.addListener( this::registerCommands );
        
        MRBlocks.BLOCKS.register( modBus );
        MRItems.ITEMS.register( modBus );
        MRCreativeTabs.CREATIVE_TABS.register( modBus );
        MRBlockEntities.BLOCK_ENTITIES.register( modBus );
        MREntities.ENTITIES.register( modBus );
        MRParticles.PARTICLES.register( modBus );
        MRContainers.CONTAINERS.register( modBus );
        MRMobEffects.MOB_EFFECTS.register( modBus );
        MRArtifactAbilities.ARTIFACT_ABILITIES.register( modBus );
        MRLootItemFunctions.LOOT_ITEM_FUNCTIONS.register( modBus );
        MRGlobalLootMods.GLOBAL_LOOT_MODS.register( modBus );
        MRStructureTypes.STRUCTURES.register( modBus );
        MRStructureProcessors.PROCESSORS.register( modBus );
        MRConfiguredFeatures.CF_REGISTRY.register( modBus );
        MRConfiguredFeatures.P_REGISTRY.register( modBus );
        MRArgumentTypes.ARGUMENT_TYPES.register( modBus );
        
        // Enqueue config init
        ModLoadingStage.COMMON_SETUP.getDeferredWorkQueue().enqueueWork( modContainer, Config::initialize );
        
        // Load synced properties class
        SyncedProperties.init();
        
        // Init and register packets and network stuff
        PacketHandler.register();
    }
    
    
    public void onCommonSetup( FMLCommonSetupEvent event ) {
        event.enqueueWork( () -> {
            WorldgenHelper.bootstrap();
            CamoDispenserBlock.setupBehaviors();
        } );
    }
    
    private void registerCommands( RegisterCommandsEvent event ) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        MRBaseCommand.register( dispatcher );
    }
    
    
    public static ResourceLocation rl( String path ) {
        return ResourceLocation.fromNamespaceAndPath( MODID, path );
    }
}
