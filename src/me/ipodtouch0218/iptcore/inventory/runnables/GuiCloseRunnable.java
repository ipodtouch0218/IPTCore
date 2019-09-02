package me.ipodtouch0218.iptcore.inventory.runnables;

import org.bukkit.event.inventory.InventoryCloseEvent;

public interface GuiCloseRunnable extends GuiRunnable {

	public void run(InventoryCloseEvent e);
	
}
