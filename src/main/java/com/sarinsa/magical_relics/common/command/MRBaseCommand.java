package com.sarinsa.magical_relics.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class MRBaseCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("magicalrelics")
                .then(DebugBaseCommand.register())
                .then(ArtifactBaseCommand.register())
                .then(AbilityBaseCommand.register()));
    }
}
