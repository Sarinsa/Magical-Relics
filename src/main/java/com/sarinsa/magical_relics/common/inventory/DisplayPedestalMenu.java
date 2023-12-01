package com.sarinsa.magical_relics.common.inventory;

import com.sarinsa.magical_relics.common.core.registry.MRMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class DisplayPedestalMenu extends AbstractContainerMenu {

    private Container displayPedestal;


    public DisplayPedestalMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(1));
    }

    public DisplayPedestalMenu(int id, Inventory inventory, Container container) {
        super(MRMenus.DISPLAY_PEDESTAL.get(), id);
        this.displayPedestal = container;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return displayPedestal.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        displayPedestal.stopOpen(player);
    }
}
