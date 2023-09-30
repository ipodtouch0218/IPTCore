package me.ipodtouch0218.iptcore.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface NullElementListener {

	public void onClick(InventoryClickEvent event);
	
}
