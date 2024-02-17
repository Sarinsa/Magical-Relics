package com.sarinsa.magical_relics.common.network.work;

import com.sarinsa.magical_relics.client.screen.AlterationNegatorScreen;
import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import com.sarinsa.magical_relics.common.network.message.S2CJukeboxAbility;
import com.sarinsa.magical_relics.common.network.message.S2COpenBEScreen;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class ClientWork {

    public static void handleJukeboxAbilityUse(S2CJukeboxAbility message) {
        BlockPos pos = new BlockPos(message.x, message.y, message.z);
        boolean playMusic = message.play;

        LocalPlayer player = Minecraft.getInstance().player;
        LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;

        if (player == null) return;

        ItemStack itemStack = player.getMainHandItem();
        CompoundTag modDataTag = itemStack.getOrCreateTag().getCompound(ArtifactUtils.MOD_DATA_KEY);
        RecordItem record = (RecordItem) ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(modDataTag.getString("JUKEBOXMusicDiscId")));

        if (!playMusic) {
            levelRenderer.playStreamingMusic(null, pos);
        }
        else {
            if (record != null) {
                levelRenderer.playStreamingMusic(record.getSound(), pos, record);
                RandomSource random = player.level.getRandom();

                for (int i = 0; i < 10; i++) {
                    player.level.addParticle(
                            ParticleTypes.NOTE,
                            (player.getX() + 0.5D) + (random.nextGaussian() / 2),
                            (player.getY() + 1.2D) + (random.nextGaussian() / 4),
                            (player.getZ() + 0.5D) + (random.nextGaussian() / 2),
                            random.nextInt(25) / 24.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    public static void handleOpenBEScreen(S2COpenBEScreen message) {
        int screenType = message.screenType;
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) return;

        if (screenType == 0) {
            BlockPos blockPos = new BlockPos(message.x, message.y, message.z);
            BlockEntity blockEntity = player.level.getExistingBlockEntity(blockPos);

            if (blockEntity instanceof AntiBuilderBlockEntity antiBuilder) {
                Minecraft.getInstance().setScreen(new AlterationNegatorScreen(blockPos, antiBuilder));
            }
        }
    }
}
