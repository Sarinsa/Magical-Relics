package com.sarinsa.magical_relics.client.screen;

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
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AntiBuilderScreen extends Screen {
    
    private static final int DEFAULT_TEXT_COLOR = 10526880;
    
    private final AntiBuilderBlockEntity antiBuilder;
    private final BlockPos pos;
    
    private IntegerTextField minXEdit;
    private IntegerTextField minYEdit;
    private IntegerTextField minZEdit;
    
    private IntegerTextField maxXEdit;
    private IntegerTextField maxYEdit;
    private IntegerTextField maxZEdit;
    
    private Button doneButton;
    
    
    public AntiBuilderScreen( BlockPos pos, AntiBuilderBlockEntity antiBuilder ) {
        super( GameNarrator.NO_TITLE );
        this.antiBuilder = antiBuilder;
        this.pos = pos;
    }
    
    @Override
    protected void init() {
        maxXEdit = createCoordField( (width / 2) - 70, (height / 3) + 50, 10 );
        maxYEdit = createCoordField( (width / 2) - 20, (height / 3) + 50, 10 );
        maxZEdit = createCoordField( (width / 2) + 30, (height / 3) + 50, 10 );
        minXEdit = createCoordField( (width / 2) - 70, (height / 3), -10 );
        minYEdit = createCoordField( (width / 2) - 20, (height / 3), -10 );
        minZEdit = createCoordField( (width / 2) + 30, (height / 3), -10 );
        
        maxXEdit.setResponder( this::updateDoneButton );
        maxYEdit.setResponder( this::updateDoneButton );
        maxZEdit.setResponder( this::updateDoneButton );
        minXEdit.setResponder( this::updateDoneButton );
        minYEdit.setResponder( this::updateDoneButton );
        minZEdit.setResponder( this::updateDoneButton );
        
        addRenderableWidget( minXEdit );
        addRenderableWidget( minYEdit );
        addRenderableWidget( minZEdit );
        addRenderableWidget( maxXEdit );
        addRenderableWidget( maxYEdit );
        addRenderableWidget( maxZEdit );
        
        // Done button
        Button.Builder doneButton = new Button.Builder( CommonComponents.GUI_DONE, ( button ) -> {
            onDone();
        } );
        doneButton.pos( width / 2 - 154, 210 );
        doneButton.size( 150, 20 );
        this.doneButton = doneButton.build();
        addRenderableWidget( this.doneButton );
        
        // Cancel button
        Button.Builder cancelButton = new Button.Builder( CommonComponents.GUI_CANCEL, ( button ) -> {
            onCancel();
        } );
        cancelButton.pos( width / 2 + 4, 210 );
        cancelButton.size( 150, 20 );
        addRenderableWidget( cancelButton.build() );
        
        setInitialFocus( minXEdit );
    }
    
    @Override
    public void tick() {
        minXEdit.tick();
        minYEdit.tick();
        minZEdit.tick();
        maxXEdit.tick();
        maxYEdit.tick();
        maxZEdit.tick();
    }
    
    @Override
    public void onClose() {
        this.onCancel();
    }
    
    /** Helper method for creating a coordinate text field. */
    private IntegerTextField createCoordField( int x, int y, int defaultValue ) {
        return new IntegerTextField( font, defaultValue,
                -20, 20,
                x, y,
                40, 20,
                null, null
        );
    }
    
    /** Used as each coordinate field's responder. */
    private void updateDoneButton( String value ) {
        boolean validValues = minXEdit.isValueValid() && minYEdit.isValueValid() && minZEdit.isValueValid()
                && maxXEdit.isValueValid() && maxYEdit.isValueValid() && maxZEdit.isValueValid();
        
        boolean validBounds = minXEdit.getCurrentValue() < maxXEdit.getCurrentValue()
                && minYEdit.getCurrentValue() < maxYEdit.getCurrentValue()
                && minZEdit.getCurrentValue() < maxZEdit.getCurrentValue();
        
        doneButton.active = validValues && validBounds;
    }
    
    private void onDone() {
        // Add offset
        int[] bbCoordinates = new int[] {
                minXEdit.getCurrentValue() - 1,
                minYEdit.getCurrentValue() - 1,
                minZEdit.getCurrentValue() - 1,
                maxXEdit.getCurrentValue() - 1,
                maxYEdit.getCurrentValue() - 1,
                maxZEdit.getCurrentValue() - 1
        };
        antiBuilder.recalculateEffectiveArea( bbCoordinates );
        sendNBTToServer( bbCoordinates );
        minecraft.setScreen( null );
    }
    
    private void onCancel() {
        minecraft.setScreen( null );
    }
    
    /** Sends any bounds changes back to the server. */
    private void sendNBTToServer( int[] bbCoordinates ) {
        if( minecraft.player != null )
            NetworkHelper.sendRecalcAntiBuilderBounds( minecraft.player, pos, bbCoordinates );
    }
    
    @Override
    public void resize( Minecraft minecraft, int width, int height ) {
        init( minecraft, width, height );
    }
    
    @Override
    public boolean keyPressed( int key, int scancode, int mods ) {
        if( super.keyPressed( key, scancode, mods ) ) {
            return true;
        }
        else if( !doneButton.active || key != 257 && key != 335 ) {
            return false;
        }
        else {
            onDone();
            return true;
        }
    }
    
    @Override
    public void render( GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks ) {
        renderBackground( guiGraphics );
        
        guiGraphics.drawCenteredString( font, Component.translatable( MRBlocks.ANTI_BUILDER.get().getDescriptionId() ), width / 2, (height / 2) - 90, DEFAULT_TEXT_COLOR );
        
        try {
            guiGraphics.drawString( font, References.ANTI_BUILDER_MIN_XYZ_SIZE, (width / 2) - 71, (height / 2) - 55, DEFAULT_TEXT_COLOR );
            guiGraphics.drawString( font, References.ANTI_BUILDER_MAX_XYZ_SIZE, (width / 2) - 71, (height / 2) - 5, DEFAULT_TEXT_COLOR );
            
            minXEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
            minYEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
            minZEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
            minXEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
            minYEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
            minZEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
        }
        catch( Exception ignored ) { }
        
        super.render( guiGraphics, mouseX, mouseY, partialTicks );
    }
}
