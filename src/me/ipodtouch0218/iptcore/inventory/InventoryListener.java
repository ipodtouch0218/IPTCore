package me.ipodtouch0218.iptcore.inventory;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import me.ipodtouch0218.iptcore.IPTCore;
import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;

public class InventoryListener implements Listener {

	private HashMap<UUID, Stack<GuiInventory>> histories = new HashMap<>();
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		UUID playerUUID = e.getWhoClicked().getUniqueId();
		
		if (!isCustomInventory(e.getView().getTopInventory())) {
			histories.remove(playerUUID);
			return;
		}
		
		e.setCancelled(true);
		GuiInventory currentInv = histories.get(playerUUID).peek();
		GuiElement element = currentInv.getElement(e.getRawSlot());
		
		
		if (element != null) {
			element.onClick((Player) e.getWhoClicked(), currentInv, e.getClick());
			if (element.closeOnClick()) {
				e.getWhoClicked().closeInventory();
			}
		} else {
			if (currentInv.getNullElementListener() != null)
				currentInv.getNullElementListener().onClick(e);
		}
	}
	
	public boolean isCustomInventory(Inventory inv) {
		return inv.getHolder() instanceof GuiInventoryHolder;
	}
	
	//---OPEN/CLOSE GUIS---//
	public void openGui(Player player, GuiInventory inv) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Stack<GuiInventory> history = histories.get(player.getUniqueId());
				if (history == null) {
					history = new Stack<GuiInventory>();
					histories.put(player.getUniqueId(), history);
				}
				history.push(inv);
				player.openInventory(inv.getInventory(player));
			}
		}.runTaskLater(IPTCore.plugin, 1);
	}
	public void openPreviousGui(Player player) {
		player.closeInventory();
		Stack<GuiInventory> history = histories.get(player.getUniqueId());
		if (history == null) { return; }
		history.pop();
		if (history.isEmpty()) {
			return;
		}
		player.openInventory(history.peek().getInventory(player));
	}
	
	//---CLOSEALL---//
	public void closeAll() {
		for (Entry<UUID,Stack<GuiInventory>> e : histories.entrySet()) {
			Player pl = Bukkit.getPlayer(e.getKey());
			if (pl == null) { continue; }
			if (pl.getOpenInventory() == null || pl.getOpenInventory().getTopInventory() == null) { continue; }
			if (e.getValue() == null || e.getValue().isEmpty()) { continue; }
			if (pl.getOpenInventory().getTopInventory().equals(e.getValue().peek().getInventory(pl))) {
				pl.closeInventory();
			}
		}
		histories.clear();
	}
}
