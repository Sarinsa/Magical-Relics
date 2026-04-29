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
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;

public class AntiBuilderScreen extends Screen {
    
    private static final int DEFAULT_TEXT_COLOR = 10526880;
    
    private final AntiBuilderBlockEntity antiBuilder;
    private final BlockPos pos;
    
    private IntegerTextField corner1XEdit;
    private IntegerTextField corner1YEdit;
    private IntegerTextField corner1ZEdit;
    
    private IntegerTextField corner2XEdit;
    private IntegerTextField corner2YEdit;
    private IntegerTextField corner2ZEdit;
    
    private Button doneButton;
    
    
    public AntiBuilderScreen( BlockPos pos, AntiBuilderBlockEntity antiBuilder ) {
        super( GameNarrator.NO_TITLE );
        this.antiBuilder = antiBuilder;
        this.pos = pos;
    }
    
    @Override
    protected void init() {
        final AABB aabb = antiBuilder.getEffectiveArea() == null
                // Default box in case existing AoE is null for whatever reason.
                ? new AABB( -10, -10, -10,
                11, 11, 11 )
                : antiBuilder.getEffectiveArea();
        final Vec3i pos = antiBuilder.getBlockPos();
        
        corner1XEdit = createCoordField( (width / 2) - 70, (height / 3), (int) aabb.maxX - pos.getX() );
        corner1YEdit = createCoordField( (width / 2) - 20, (height / 3), (int) aabb.maxY - pos.getY() );
        corner1ZEdit = createCoordField( (width / 2) + 30, (height / 3), (int) aabb.maxZ - pos.getZ() );
        corner2XEdit = createCoordField( (width / 2) - 70, (height / 3) + 50, (int) aabb.minX - pos.getX() );
        corner2YEdit = createCoordField( (width / 2) - 20, (height / 3) + 50, (int) aabb.minY - pos.getY() );
        corner2ZEdit = createCoordField( (width / 2) + 30, (height / 3) + 50, (int) aabb.minZ - pos.getZ() );
        
        corner1XEdit.setResponder( this::updateDoneButton );
        corner1YEdit.setResponder( this::updateDoneButton );
        corner1ZEdit.setResponder( this::updateDoneButton );
        corner2XEdit.setResponder( this::updateDoneButton );
        corner2YEdit.setResponder( this::updateDoneButton );
        corner2ZEdit.setResponder( this::updateDoneButton );
        
        addRenderableWidget( corner1XEdit );
        addRenderableWidget( corner1YEdit );
        addRenderableWidget( corner1ZEdit );
        addRenderableWidget( corner2XEdit );
        addRenderableWidget( corner2YEdit );
        addRenderableWidget( corner2ZEdit );
        
        // Done button
        doneButton = new Button.Builder( CommonComponents.GUI_DONE, ( button ) -> onDone() )
                .pos( width / 2 - 154, 210 )
                .size( 150, 20 )
                .build();
        
        addRenderableWidget( doneButton );
        
        // Cancel button
        addRenderableWidget( new Button.Builder( CommonComponents.GUI_CANCEL, ( button ) -> onCancel() )
                .pos( width / 2 + 4, 210 )
                .size( 150, 20 )
                .build() );
        
        setInitialFocus( corner1XEdit );
    }
    
    @Override
    public void tick() {
        corner1XEdit.tick();
        corner1YEdit.tick();
        corner1ZEdit.tick();
        corner2XEdit.tick();
        corner2YEdit.tick();
        corner2ZEdit.tick();
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
        boolean validValues = corner1XEdit.isValueValid() && corner1YEdit.isValueValid() && corner1ZEdit.isValueValid()
                && corner2XEdit.isValueValid() && corner2YEdit.isValueValid() && corner2ZEdit.isValueValid();
        
        Vec3i corner1 = new Vec3i( corner1XEdit.getCurrentValue(), corner1YEdit.getCurrentValue(), corner1ZEdit.getCurrentValue() );
        Vec3i corner2 = new Vec3i( corner2XEdit.getCurrentValue(), corner2YEdit.getCurrentValue(), corner2ZEdit.getCurrentValue() );
        
        doneButton.active = validValues && !corner1.equals( corner2 );
    }
    
    private void onDone() {
        // Add offset
        final int[] bbSizes = new int[] {
                corner1XEdit.getCurrentValue(),
                corner1YEdit.getCurrentValue(),
                corner1ZEdit.getCurrentValue(),
                corner2XEdit.getCurrentValue(),
                corner2YEdit.getCurrentValue(),
                corner2ZEdit.getCurrentValue()
        };
        antiBuilder.recalculateEffectiveArea( bbSizes );
        sendNBTToServer( bbSizes );
        // noinspection ConstantConditions
        minecraft.setScreen( null );
    }
    
    private void onCancel() {
        // noinspection ConstantConditions
        minecraft.setScreen( null );
    }
    
    /** Sends any bounds changes back to the server. */
    private void sendNBTToServer( int[] bbCoordinates ) {
        // noinspection ConstantConditions
        if( minecraft.player != null )
            NetworkHelper.sendRecalcAntiBuilderBounds( minecraft.player, pos, bbCoordinates );
    }
    
    // TODO - Test if this override should be removed
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
            guiGraphics.drawString( font, References.ANTI_BUILDER_CORNER_1, (width / 2) - 71, (height / 2) - 55, DEFAULT_TEXT_COLOR );
            guiGraphics.drawString( font, References.ANTI_BUILDER_CORNER_2, (width / 2) - 71, (height / 2) - 5, DEFAULT_TEXT_COLOR );
            
            corner1XEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
            corner1YEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
            corner1ZEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
            corner1XEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
            corner1YEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
            corner1ZEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
        }
        catch( Exception ignored ) { }
        
        super.render( guiGraphics, mouseX, mouseY, partialTicks );
    }
}
