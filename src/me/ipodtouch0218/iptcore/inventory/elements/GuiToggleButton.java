package me.ipodtouch0218.iptcore.inventory.elements;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.inventory.GuiInventory;

public class GuiToggleButton extends GuiElement {
	
	protected ItemStack offStack;
	protected boolean currentValue;
	
	public GuiToggleButton(ItemStack onStack, ItemStack offStack, boolean defaultValue) {
		super(onStack, false);
		this.offStack = offStack;
		this.currentValue = defaultValue;
	}
	public GuiToggleButton(ConfigurationSection section) {
		super(section);
		currentValue = section.getBoolean("function.default", false);
	}

	@Override
	public void onClick(Player player, GuiInventory inventory, ClickType type) {
		currentValue = !currentValue;
		inventory.updateInventory();
		onValueChange();
	}
	
	public void onValueChange() {}
	
	//---GETTERS---//
	public ItemStack getItem() { return (currentValue ? stack : offStack); }
	public boolean getValue() { return currentValue; }
	
}
