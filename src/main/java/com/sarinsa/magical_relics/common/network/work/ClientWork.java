package com.sarinsa.magical_relics.common.network.work;

import com.sarinsa.magical_relics.common.network.message.S2CJukeboxAbility;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.ForgeRegistries;

public class ClientWork {

    public static void handleJukeboxAbilityUse(S2CJukeboxAbility message) {
        BlockPos pos = new BlockPos(message.x, message.y, message.z);
        boolean play = message.play;

        LocalPlayer player = Minecraft.getInstance().player;
        LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;

        if (player == null) return;

        ItemStack itemStack = player.getMainHandItem();
        CompoundTag modDataTag = itemStack.getOrCreateTag().getCompound(ArtifactUtils.MOD_DATA_KEY);
        RecordItem record = (RecordItem) ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(modDataTag.getString("JUKEBOXMusicDiscId")));

        if (!play) {
            levelRenderer.playStreamingMusic(null, pos);
        }
        else {
            if (record != null)
                levelRenderer.playStreamingMusic(record.getSound(), pos, record);
        }
    }
}
