package me.ipodtouch0218.iptcore.inventory;

@FunctionalInterface
public interface PagedUpdater {

	public boolean accept(GuiInventory inv);
	
}
