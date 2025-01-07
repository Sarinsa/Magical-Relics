package com.sarinsa.magical_relics.common.core;

import com.mojang.brigadier.CommandDispatcher;
import com.sarinsa.magical_relics.common.block.CamoDispenserBlock;
import com.sarinsa.magical_relics.common.command.MRBaseCommand;
import com.sarinsa.magical_relics.common.core.config.ConfigReloadListener;
import com.sarinsa.magical_relics.common.core.config.MRAbilitiesConfig;
import com.sarinsa.magical_relics.common.core.config.MRGeneralConfig;
import com.sarinsa.magical_relics.common.core.registry.*;
import com.sarinsa.magical_relics.common.event.MREventListener;
import com.sarinsa.magical_relics.common.network.PacketHandler;
import com.sarinsa.magical_relics.common.tag.MRBlockTags;
import com.sarinsa.magical_relics.common.worldgen.WorldgenHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MagicalRelics.MODID)
public class MagicalRelics {

    // TODO LIST
    //
    // - Make it so slimes can spawn from spawner blocks regardless of vanilla spawn rules
    //
    // - Make separate models for Thick Tripwire (consider a slightly different THICCER texture as well?
    //
    // - More configurability for the anti-builder (specific blocked actions?)
    //
    // - Figure out the sailor ability (might be kinda sucky to make)

    public static final String MODID = "magical_relics";
    public static final Logger LOG = LogManager.getLogger(MODID);


    public MagicalRelics() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::onCommonSetup);
        modBus.addListener(MRItems::onCreativeTabPopulate);

        MinecraftForge.EVENT_BUS.register(new MREventListener());
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        MRBlockTags.init();

        PacketHandler.registerMessages();

        MRBlocks.BLOCKS.register(modBus);
        MRItems.ITEMS.register(modBus);
        MRCreativeTabs.CREATIVE_TABS.register(modBus);
        MRBlockEntities.BLOCK_ENTITIES.register(modBus);
        MREntities.ENTITIES.register(modBus);
        MRParticles.PARTICLES.register(modBus);
        MRContainers.CONTAINERS.register(modBus);
        MRMobEffects.MOB_EFFECTS.register(modBus);
        MRDamageTypes.DAMAGE_TYPES.register(modBus);
        MRArtifactAbilities.ARTIFACT_ABILITIES.register(modBus);
        MRLootItemFunctions.LOOT_ITEM_FUNCTIONS.register(modBus);
        MRGlobalLootMods.GLOBAL_LOOT_MODS.register(modBus);
        MRStructureTypes.STRUCTURES.register(modBus);
        MRStructureProcessors.PROCESSORS.register(modBus);
        MRConfiguredFeatures.CF_REGISTRY.register(modBus);
        MRConfiguredFeatures.P_REGISTRY.register(modBus);
        MRArgumentTypes.ARGUMENT_TYPES.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MRAbilitiesConfig.CONFIG_SPEC, "magical_relics/ability-properties.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MRGeneralConfig.CONFIG_SPEC, "magical_relics/general.toml");

        modBus.register(new ConfigReloadListener());
    }


    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            WorldgenHelper.bootstrap();
            CamoDispenserBlock.setupBehaviors();
        });
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        MRBaseCommand.register(dispatcher);
    }


    public static ResourceLocation resLoc(String path) {
        return new ResourceLocation(MODID, path);
    }
}
