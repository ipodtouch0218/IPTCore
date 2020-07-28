package me.ipodtouch0218.iptcore.inventory;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import lombok.Getter;
import lombok.Setter;
import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;
import me.ipodtouch0218.iptcore.utils.FormatUtils;

@Getter
public class GuiInventory {
	
	protected Inventory inventory;
	protected int size;
	protected String title;
	
	@Setter
	protected GuiElement[] elements;
	
	public GuiInventory(int size, String title, GuiElement... elements) {
		if (size % 9 != 0 || size <= 0) {
			throw new IllegalArgumentException("Size must be a multiple of 9!");
		}
		this.size = size;
		this.elements = elements;

		if (title != null) {
			inventory = Bukkit.createInventory(null, size, FormatUtils.stringColor(title));
		} else {
			inventory = Bukkit.createInventory(null, size);
		}
		this.title = title;
		updateInventory();
	}
	
	//---METHODS---//
	public void updateInventory() {
		for (int slot = 0; slot < elements.length; slot++) {
			GuiElement element = elements[slot];
			if (element != null) {
				inventory.setItem(slot, elements[slot].getItem(this));
			} else {
				inventory.setItem(slot, null);
			}
		}
		inventory.getViewers().stream()
			.filter(Player.class::isInstance)
			.map(Player.class::cast)
			.forEach(Player::updateInventory);
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
		return new GuiInventory(size, title, Arrays.copyOf(elements, size));
	}
	
	public long getBlankSlots() {
		return Arrays.stream(elements)
				.filter(e -> e == null)
				.count();
	}
}
