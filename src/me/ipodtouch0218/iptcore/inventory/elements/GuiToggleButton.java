package me.ipodtouch0218.iptcore.inventory.elements;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.inventory.GuiInventory;
import me.ipodtouch0218.iptcore.utils.ItemUtils;

public class GuiToggleButton extends GuiElement {
	
	protected ItemStack offStack;
	protected boolean currentValue;
	
	public GuiToggleButton(String settingName, boolean defaultValue) {
		super(ItemUtils.nameItem(new ItemStack(Material.LIME_DYE), settingName + " &7- &aEnabled", false));
		this.offStack = ItemUtils.nameItem(new ItemStack(Material.GRAY_DYE), settingName + " &7- &cDisabled", false);
		this.currentValue = defaultValue;
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
