package me.ipodtouch0218.iptcore.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import me.ipodtouch0218.iptcore.IPTCore;
import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;

public class InventoryListener implements Listener {

	private HashMap<UUID, Stack<GuiInventory>> histories = new HashMap<>();
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		UUID playerUUID = e.getWhoClicked().getUniqueId();
		
		Stack<GuiInventory> history = histories.get(playerUUID);
		if (history == null) { return; }
		if (history.size() <= 0) { return; }
		GuiInventory currentInv = history.peek();
		Inventory topInv = e.getView().getTopInventory();
		
		boolean equals = Arrays.equals(currentInv.getInventory().getContents(), 
				topInv.getContents());
		
		if (!equals) { return; }
		
		e.setCancelled(true);
		GuiElement element = currentInv.getElement(e.getSlot());
		
		if (element != null) {
			element.onClick((Player) e.getWhoClicked(), currentInv);
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		UUID playerUUID = e.getPlayer().getUniqueId();
		if (!histories.containsKey(playerUUID)) { return; }
		Stack<GuiInventory> history = histories.get(playerUUID);
		
		boolean equals = Arrays.equals(history.peek().getInventory().getContents(), 
				e.getInventory().getContents());
		
		if (!equals) {
			//different inventory opened than expected one, remove history
			histories.remove(playerUUID);
		}
	}
	
	
	//---OPEN/CLOSE GUIS---//
	public void openGui(Player player, GuiInventory inv) {
		Stack<GuiInventory> history = histories.get(player.getUniqueId());
		if (history == null) {
			history = new Stack<GuiInventory>();
			histories.put(player.getUniqueId(), history);
		}
		player.openInventory(history.push(inv).getInventory());
	}
	public void openPreviousGui(Player player) {
		player.closeInventory();
		Stack<GuiInventory> history = histories.get(player.getUniqueId());
		if (history == null) { return; }
		history.pop();
		if (history.isEmpty()) {
			return;
		}
		Bukkit.getScheduler().runTaskLater(IPTCore.plugin, ()-> {
			player.openInventory(history.peek().getInventory());
		}, 1);
	}
	
	//---CLOSEALL---//
	public void closeAll() {
		for (Entry<UUID,Stack<GuiInventory>> e : histories.entrySet()) {
			Player pl = Bukkit.getPlayer(e.getKey());
			if (pl == null) { continue; }
			if (pl.getOpenInventory().getTopInventory() == e.getValue().peek().getInventory()) {
				pl.closeInventory();
			}
		}
		histories.clear();
	}
}
