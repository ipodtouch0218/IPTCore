package me.ipodtouch0218.iptcore.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;
import me.ipodtouch0218.iptcore.inventory.runnables.GuiRunnable;

@Getter
public class GuiInventory {

	@Setter(value=AccessLevel.NONE)
	private PagedUpdater updater;
	
	private Inventory inventory;
	private int size;
	private String title;
	@Setter
	private GuiElement[] elements;
	private HashSet<GuiRunnable> runnables = new HashSet<>();
	private HashMap<Object,Object> customVars = new HashMap<>(); 
	
	@Setter
	private PagedGuiManager pagedManager;
	@Setter
	private int pageNumber = -1;
	
	public GuiInventory(int size, String title, GuiElement... elements) {
		if (size % 9 != 0 || size <= 0) {
			throw new IllegalArgumentException("Size must be a multiple of 9!");
		}
		this.size = size;
		this.elements = elements;

		if (title != null) {
			inventory = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', title));
		} else {
			inventory = Bukkit.createInventory(null, size);
		}
		this.title = title;
		updateInventory(false);
	}
	
	//---METHODS---//
	public void updateInventory() {
		updateInventory(true);
	}
	public void updateInventory(boolean consumer) {
		if (updater != null && consumer) {
			updater.accept(this);
		}
		for (int i = 0; i < elements.length; i++) {
			ItemStack item = null;
			if (elements[i] != null) {
				item = elements[i].getItem(this);
			}
			inventory.setItem(i, item);
		}
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
	
	public void setRunnables(HashSet<GuiRunnable> runnables) {
		this.runnables = runnables;
	}
	
	//---GETTERS---//
	public GuiElement getElement(int slot) { 
		if (slot < 0 || slot >= size) { return null; }
		return elements[slot];
	}
	
	public GuiInventory clone() {
		GuiInventory newinv = new GuiInventory(size, title, Arrays.copyOf(elements, size));
		newinv.setUpdateConsumer(updater);
		newinv.setRunnables(runnables);
		return newinv;
	}
	
	public long getBlankSlots() {
		return Arrays.stream(elements)
				.filter(e -> e == null)
				.count();
	}

	public void setUpdateConsumer(PagedUpdater updater) {
		this.updater = updater;
	}
}
