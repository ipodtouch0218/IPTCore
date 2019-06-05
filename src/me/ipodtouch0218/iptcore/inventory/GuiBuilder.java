package me.ipodtouch0218.iptcore.inventory;

import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;

public class GuiBuilder {

	private HashMap<Integer, GuiElement> elements = new HashMap<>();
	private int size = 9;
	private String title;
	
	public GuiBuilder(int size) {
		this.size = size;
	}
	
	//---SETTINGS---//
	public GuiBuilder setSize(int size) { 
		this.size = size;
		return this;
	}
	
	public GuiBuilder setTitle(String title) { 
		this.title = title; 
		return this;
	}
	
	public GuiBuilder setItem(ItemStack stack, int slot) { 
		return setItem(new GuiElement(stack), slot);
	}
	public GuiBuilder setItem(ItemStack stack, int x, int y) {
		return setItem(stack, x+(y*9));
	}

	public GuiBuilder setItem(GuiElement element, int slot) {
		elements.put(slot, element);
		return this;
	}

	public GuiBuilder setItem(GuiElement element, int x, int y) {
		return setItem(element, x+(y*9));
	}
	
	//---BORDER---//
	public GuiBuilder fillBorder(ItemStack stack, int x1, int y1, int x2, int y2) {
		return fillBorder(new GuiElement(stack), x1, y1, x2, y2);
	}
	public GuiBuilder fillBorder(GuiElement element, int x1, int y1, int x2, int y2) {
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				
				if (!(x == x1 || x == x2 || y == y1 || y == y2)) {
					continue;
				}
				
				int slot = x + y*9;
				if (slot < 0) { continue; } //dont draw into the negatives
				elements.put(slot, element);
			}
		}
		return this;
	}
	
	//---FILL AREA---//
	public GuiBuilder fillArea(ItemStack stack, int x1, int y1, int x2, int y2) {
		return fillArea(new GuiElement(stack), x1, y1, x2, y2);
	}
	public GuiBuilder fillArea(GuiElement element, int x1, int y1, int x2, int y2) {
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				int slot = x + y*9;
				if (slot < 0) { continue; } //dont draw into the negatives.
				elements.put(slot, element);
			}
		}
		return this;
	}
	
	//---BUILD---//
	public GuiInventory build() {
		if (size % 9 != 0 || size < 9) {
			throw new IllegalArgumentException("Size must be a multiple of 9 and at least 9!");
		}
		GuiElement[] elementArray = new GuiElement[size];
		elements.entrySet().stream().filter(e -> (e.getKey() < size && e.getKey() >= 0)).forEach(e -> elementArray[e.getKey()] = e.getValue());
		GuiInventory inv = new GuiInventory(size, title, elementArray);
		
		return inv;
	}
}
