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

import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;
import me.ipodtouch0218.iptcore.inventory.runnables.GuiRunnable;

public class GuiInventory {

	private Inventory inv;
	private int size;
	private String title;
	private GuiElement[] elements;
	private HashSet<GuiRunnable> runnables = new HashSet<>();
	private HashMap<Object, Object> data = new HashMap<>();
	
	public GuiInventory(int size, String title, GuiElement... elements) {
		if (size % 9 != 0 || size <= 0) {
			throw new IllegalArgumentException("Size must be a multiple of 9!");
		}
		this.size = size;
		this.elements = elements;

		if (title != null) {
			inv = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', title));
		} else {
			inv = Bukkit.createInventory(null, size);
		}
		this.title = title;
		updateInventory();
	}
	
	//---METHODS---//
	public void updateInventory() {
		if (elements == null) { return; }
		for (int i = 0; i < elements.length; i++) {
			ItemStack item = null;
			if (elements[i] != null) {
				item = elements[i].getItem(this);
			}
			inv.setItem(i, item);
		}
	}
	
	//---SETTERS---//
	public void setElement(int slot, GuiElement element) {
		if (slot < 0 || slot >= size) {
			throw new IllegalArgumentException("Slot # must be between 0 and the containers size! (" + size + ")");
		}
		elements[slot] = element;
		updateInventory();
	}	
	public void addElement(GuiElement guiElement) {
		OptionalInt indexOpt = IntStream.range(0, elements.length)
			     .filter(i -> (elements[i] == null || elements[i].getItem().getType() == Material.AIR))
			     .findFirst();
		
		if (!indexOpt.isPresent()) { return; }
		elements[indexOpt.getAsInt()] = guiElement;
		updateInventory();
	}
	
	public void setRunnables(HashSet<GuiRunnable> runnables) {
		this.runnables = runnables;
	}
	
	//---GETTERS---//
	public HashSet<GuiRunnable> getRunnables() { return runnables; }
	public HashMap<Object, Object> getData() { return data; }
	public Inventory getInventory() { return inv; }
	public int getSize() { return size; }
	public GuiElement[] getElements() { return elements; }
	public GuiElement getElement(int slot) { 
		if (slot < 0 || slot >= size) { return null; }
		return elements[slot];
	}
	
	public GuiInventory clone() {
		return new GuiInventory(size, title, Arrays.copyOf(elements, elements.length));
	}
}
