package me.ipodtouch0218.iptcore.inventory.elements;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.inventory.GuiInventory;

public class GuiElement {

	protected ItemStack stack;
	
	public GuiElement(ItemStack stack) {
		this.stack = stack;
	}
	
	public void onClick(Player player, GuiInventory inventory) {}
	
	//---SETTERS---//
	public void setItem(ItemStack stack) {
		this.stack = stack; 
	}
	
	//---GETTERS---//
	public ItemStack getItem() { 
		return stack; 
	}
	
}
