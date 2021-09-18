package me.ipodtouch0218.iptcore.inventory.elements;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.inventory.GuiInventory;
import me.ipodtouch0218.iptcore.utils.ConfigParserUtils;

public class GuiElement {

	protected ItemStack stack;
	private boolean closeOnClick;
	
	public GuiElement(ItemStack stack, boolean closeOnClick) {
		this.stack = stack;
		this.closeOnClick = closeOnClick;
	}
	public GuiElement(ConfigurationSection section) {
		stack = ConfigParserUtils.parseItem(section);
		closeOnClick = section.getBoolean("close", false);
	}
	
	public void onClick(Player player, GuiInventory inventory, ClickType click) {}
	
	//---SETTERS---//
	public void setItem(ItemStack stack) {
		this.stack = stack; 
	}
	
	//---GETTERS---//
	public ItemStack getItem() {
		return stack;
	}
	public ItemStack getItem(Player player) { 
		return getItem(); 
	}
	public ItemStack getItem(Player player, GuiInventory inventory) {
		return getItem(player);
	}
	public boolean closeOnClick() { 
		return closeOnClick;
	}
}
