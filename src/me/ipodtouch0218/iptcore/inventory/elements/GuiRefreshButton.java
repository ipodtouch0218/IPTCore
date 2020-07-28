package me.ipodtouch0218.iptcore.inventory.elements;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.inventory.GuiInventory;

public class GuiRefreshButton extends GuiElement {

	public GuiRefreshButton(ItemStack stack, boolean closeOnClick) {
		super(stack, closeOnClick);
	}

	public GuiRefreshButton(ConfigurationSection section) {
		super(section);
	}

	@Override
	public void onClick(Player player, GuiInventory inventory, ClickType click) {
		inventory.updateInventory();
	}
	
}
