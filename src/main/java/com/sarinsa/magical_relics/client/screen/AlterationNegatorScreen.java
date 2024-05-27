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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
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

        // Done button
        Button.Builder doneButton = new Button.Builder(CommonComponents.GUI_DONE, (button) -> {
            onDone();
        });
        doneButton.pos(width / 2 - 154, 210);
        doneButton.size(150, 20);
        this.doneButton = doneButton.build();
        addRenderableWidget(this.doneButton);

        // Cancel button
        Button.Builder cancelButton = new Button.Builder(CommonComponents.GUI_CANCEL, (button) -> {
            onCancel();
        });
        cancelButton.pos(width / 2 + 4, 210);
        cancelButton.size(150, 20);
        addRenderableWidget(cancelButton.build());

        setInitialFocus(xSizeEdit);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String xValue = xSizeEdit.getValue();
        String yValue = ySizeEdit.getValue();
        String zValue = zSizeEdit.getValue();

        init(minecraft, width, height);

        xSizeEdit.setValue(xValue);
        ySizeEdit.setValue(yValue);
        zSizeEdit.setValue(zValue);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int mods) {
        if (super.keyPressed(key, scancode, mods)) {
            return true;
        }
        else if (!doneButton.active || key != 257 && key != 335) {
            return false;
        }
        else {
            onDone();
            return true;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics);

        guiGraphics.drawCenteredString(font, Component.translatable(MRBlocks.ANTI_BUILDER.get().getDescriptionId()), width / 2, height / 2 - 90, DEFAULT_TEXT_COLOR);

        try {
            guiGraphics.drawString(font, References.ALTNEG_X_SIZE, width / 2 - 40, height / 2 - 55, DEFAULT_TEXT_COLOR);
            xSizeEdit.render(guiGraphics, mouseX, mouseY, partialTicks);

            guiGraphics.drawString(font, References.ALTNEG_Y_SIZE, width / 2 - 40, height / 2 - 25, DEFAULT_TEXT_COLOR);
            ySizeEdit.render(guiGraphics, mouseX, mouseY, partialTicks);

            guiGraphics.drawString(font, References.ALTNEG_Z_SIZE, width / 2 - 40, height / 2 + 5, DEFAULT_TEXT_COLOR);
            zSizeEdit.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
        catch (Exception ignored) {

        }
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
