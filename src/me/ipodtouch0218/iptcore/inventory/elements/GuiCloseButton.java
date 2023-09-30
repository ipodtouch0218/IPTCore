package me.ipodtouch0218.iptcore.inventory.elements;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.inventory.GuiInventory;

public class GuiCloseButton extends GuiElement {

	public GuiCloseButton(ItemStack stack, boolean closeOnClick) {
		super(stack, closeOnClick);
	}
	public GuiCloseButton(ConfigurationSection section) {
		super(section);
	}
	
	@Override
	public void onClick(Player player, GuiInventory inventory, ClickType type) {
		player.closeInventory();
	}

}
