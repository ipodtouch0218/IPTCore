package me.ipodtouch0218.iptcore.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;
import me.ipodtouch0218.iptcore.utils.FormatUtils;

@Getter
public class GuiInventory implements Cloneable {
	
	private HashMap<UUID, Inventory> inventories = new HashMap<>();
	protected int size;
	protected String title;
	@Setter
	protected NullElementListener nullElementListener;
	
	@Setter
	protected GuiElement[] elements;
	protected HashMap<Object,Object> customVars = new HashMap<>();
	
	public GuiInventory(int size, String title, GuiElement... elements) {
		if (size % 9 != 0 || size <= 0) {
			throw new IllegalArgumentException("Size must be a multiple of 9!");
		}
		this.size = size;
		this.elements = elements;
		this.title = FormatUtils.stringColor(title);
		
		updateInventory();
	}
	
	//---METHODS---//
	public void updateInventory() {
		for (Entry<UUID, Inventory> entry : inventories.entrySet()) {
			updateInventory(Bukkit.getPlayer(entry.getKey()), entry.getValue());
		}
	}
	
	public void updateInventory(Player player, Inventory inventory) {
		for (int slot = 0; slot < elements.length; slot++) {
			GuiElement element = elements[slot];
			if (element != null) {
				ItemStack item = elements[slot].getItem(player, this).clone();
				if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && inventory.getViewers().size() == 1) {
					Player pl = (Player) inventory.getViewers().get(0);
					ItemMeta meta = item.getItemMeta();
					if (meta.hasDisplayName()) {
						meta.setDisplayName(PlaceholderAPI.setPlaceholders(pl, meta.getDisplayName()));
					}
					if (meta.hasLore()) {
						meta.setLore(PlaceholderAPI.setPlaceholders(pl, meta.getLore()));
					}
					item.setItemMeta(meta);
				}
				inventory.setItem(slot, item);
			} else {
				inventory.setItem(slot, null);
			}
		}

		inventory.getViewers().stream()
			.filter(Player.class::isInstance)
			.map(Player.class::cast)
			.forEach(Player::updateInventory);
	}
	
	public Inventory getInventory(Player player) {
		if (inventories.containsKey(player.getUniqueId()))
			return inventories.get(player.getUniqueId());
		Inventory inv = Bukkit.createInventory(new GuiInventoryHolder(), size, (title != null ? FormatUtils.stringColor(title) : null));
		inventories.put(player.getUniqueId(), inv);
		updateInventory(player, inv);
		return inv;
	}
	
	//---SETTERS---//
	public void setElement(int slot, GuiElement element) {
		if (slot < 0 || slot >= size) {
			throw new IllegalArgumentException("Slot # must be between 0 and the containers size! (" + size + ")");
		}
		elements[slot] = element;
	}	
	public void addElement(GuiElement guiElement) {
		OptionalInt indexOpt = IntStream.range(0, elements.length)
			     .filter(i -> 
			     	elements[i] == null || 
			     	elements[i].getItem() == null ||
			     	elements[i].getItem().getType() == Material.AIR)
			     .findFirst();
		
		if (!indexOpt.isPresent()) { return; }
		elements[indexOpt.getAsInt()] = guiElement;
	}
	
	//---GETTERS---//
	public GuiElement getElement(int slot) { 
		if (slot < 0 || slot >= size) { return null; }
		return elements[slot];
	}
	
	public GuiInventory clone() {
		return new GuiInventory(size, title, elements.clone());
	}
	
	public long getBlankSlots() {
		return Arrays.stream(elements)
				.filter(e -> e == null)
				.count();
	}
}
