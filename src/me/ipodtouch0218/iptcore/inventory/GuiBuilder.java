package me.ipodtouch0218.iptcore.inventory;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;
import me.ipodtouch0218.iptcore.inventory.runnables.GuiRunnable;

public class GuiBuilder {

	private HashMap<Integer, GuiElement> elements = new HashMap<>();
	private int size = 1;
	private String title;
	private HashSet<GuiRunnable> runnables = new HashSet<>();
	
	public GuiBuilder(int size) {
		this.size = size;
	}
	
	//---SETTINGS---//
	public GuiBuilder setRows(int size) { 
		this.size = size;
		return this;
	}
	
	public GuiBuilder setTitle(String title) { 
		this.title = title; 
		return this;
	}
	
	public GuiBuilder setItem(ItemStack stack, int slot) { 
		return setItem(new GuiElement(stack, false), slot);
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
	
	public GuiBuilder addItem(ItemStack stack) {
		return addItem(new GuiElement(stack, false));
	}
	
	public GuiBuilder addItem(GuiElement element) {
		for (int i = 0; i < size; i++) {
			if (!elements.containsKey(i)) {
				elements.put(i, element);
				break;
			}
		}
		return this;
	}
	
	//---BORDER---//
	public GuiBuilder fillBorder(ItemStack stack, int x1, int y1, int x2, int y2) {
		return fillBorder(new GuiElement(stack, false), x1, y1, x2, y2);
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
		return fillArea(new GuiElement(stack, false), x1, y1, x2, y2);
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
	
	//---FILL ALL---//
	public GuiBuilder fill(ItemStack stack) {
		return fill(new GuiElement(stack, false));
	}
	public GuiBuilder fill(GuiElement element) {
		for (int i = 0; i < size*9; i++) {
			elements.put(i, element);
		}
		return this;
	}
	
	//---EVENTS---//
	public GuiBuilder addGuiRunnable(GuiRunnable e) {
		runnables.add(e);
		return this;
	}
	
	
	//---BUILD---//
	public GuiInventory build() {
		GuiElement[] elementArray = new GuiElement[size*9];
		elements.entrySet().stream().filter(e -> (e.getKey() < size*9 && e.getKey() >= 0)).forEach(e -> elementArray[e.getKey()] = e.getValue());
		
		GuiInventory inv = new GuiInventory(size*9, title, elementArray);
		inv.setRunnables(runnables);
		
		return inv;
	}
}
