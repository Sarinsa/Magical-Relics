package com.sarinsa.magical_relics.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sarinsa.magical_relics.client.screen.widget.AbstractTextField;
import com.sarinsa.magical_relics.client.screen.widget.IntegerTextField;
import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.network.NetworkHelper;
import com.sarinsa.magical_relics.common.util.References;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;

public class AlterationNegatorScreen extends Screen {

    private static final int DEFAULT_TEXT_COLOR = 10526880;

    private final AntiBuilderBlockEntity antiBuilder;
    private final BlockPos pos;

    private IntegerTextField xSizeEdit;
    private IntegerTextField ySizeEdit;
    private IntegerTextField zSizeEdit;

    private Button doneButton;


    public AlterationNegatorScreen(BlockPos pos, AntiBuilderBlockEntity antiBuilder) {
        super(GameNarrator.NO_TITLE);
        this.antiBuilder = antiBuilder;
        this.pos = pos;
    }

    @Override
    public void tick() {
        doneButton.active = xSizeEdit.isValueValid() && ySizeEdit.isValueValid() && zSizeEdit.isValueValid();

        xSizeEdit.tick();
        ySizeEdit.tick();
        zSizeEdit.tick();
    }

    private void onDone() {
        antiBuilder.setEffectiveArea(new AABB(pos).inflate(xSizeEdit.getCurrentValue() - 1, ySizeEdit.getCurrentValue() - 1, zSizeEdit.getCurrentValue() - 1));
        sendNBTToServer();
        minecraft.setScreen(null);
    }

    private void onCancel() {
        minecraft.setScreen(null);
    }

    private void sendNBTToServer() {
        if (minecraft.player != null)
            NetworkHelper.sendSaveALTNEGData(minecraft.player, pos, xSizeEdit.getCurrentValue() - 1, ySizeEdit.getCurrentValue() - 1, zSizeEdit.getCurrentValue() - 1);
    }

    public void onClose() {
        this.onCancel();
    }

    @Override
    protected void init() {
        minecraft.keyboardHandler.setSendRepeatsToGui(true);

        xSizeEdit = new IntegerTextField(
                font,
                (int) antiBuilder.getEffectiveArea().maxX - pos.getX(),
                1,
                30,
                width / 2,
                height / 2 - 60,
                40,
                 20,
                null,
                null);

        ySizeEdit = new IntegerTextField(
                font,
                (int) antiBuilder.getEffectiveArea().maxY - pos.getY(),
                1,
                30,
                width / 2,
                height / 2 - 30,
                40,
                20,
                null,
                null);

        zSizeEdit = new IntegerTextField(
                font,
                (int) antiBuilder.getEffectiveArea().maxZ - pos.getZ(),
                1,
                30,
                width / 2,
                height / 2,
                40,
                20,
                null,
                null);

        addRenderableWidget(xSizeEdit);
        addRenderableWidget(ySizeEdit);
        addRenderableWidget(zSizeEdit);

        doneButton = addRenderableWidget(new Button(width / 2 - 4 - 150, 210, 150, 20, CommonComponents.GUI_DONE, (button) -> {
            onDone();
        }));
        addRenderableWidget(new Button(width / 2 + 4, 210, 150, 20, CommonComponents.GUI_CANCEL, (button) -> {
            onCancel();
        }));
        setInitialFocus(xSizeEdit);
    }

    @Override
    public void resize(Minecraft minecraft, int p_98961_, int p_98962_) {
        String xValue = xSizeEdit.getValue();
        String yValue = ySizeEdit.getValue();
        String zValue = zSizeEdit.getValue();

        init(minecraft, p_98961_, p_98962_);

        xSizeEdit.setValue(xValue);
        ySizeEdit.setValue(yValue);
        zSizeEdit.setValue(zValue);
    }

    @Override
    public void removed() {
        minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int p_98951_, int p_98952_, int p_98953_) {
        if (super.keyPressed(p_98951_, p_98952_, p_98953_)) {
            return true;
        }
        else if (!doneButton.active || p_98951_ != 257 && p_98951_ != 335) {
            return false;
        }
        else {
            onDone();
            return true;
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);

        drawCenteredString(poseStack, font, Component.translatable(MRBlocks.ANTI_BUILDER.get().getDescriptionId()), width / 2, height / 2 - 90, DEFAULT_TEXT_COLOR);

        try {
            drawString(poseStack, font, References.ALTNEG_X_SIZE, width / 2 - 40, height / 2 - 55, DEFAULT_TEXT_COLOR);
            xSizeEdit.render(poseStack, mouseX, mouseY, partialTicks);

            drawString(poseStack, font, References.ALTNEG_Y_SIZE, width / 2 - 40, height / 2 - 25, DEFAULT_TEXT_COLOR);
            ySizeEdit.render(poseStack, mouseX, mouseY, partialTicks);

            drawString(poseStack, font, References.ALTNEG_Z_SIZE, width / 2 - 40, height / 2 + 5, DEFAULT_TEXT_COLOR);
            zSizeEdit.render(poseStack, mouseX, mouseY, partialTicks);
        }
        catch (Exception e) {

        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
