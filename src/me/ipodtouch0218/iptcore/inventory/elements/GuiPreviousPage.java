package me.ipodtouch0218.iptcore.inventory.elements;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.IPTCore;
import me.ipodtouch0218.iptcore.inventory.GuiInventory;
import me.ipodtouch0218.iptcore.inventory.PagedGuiManager;

public class GuiPreviousPage extends GuiElement {

	public GuiPreviousPage(ItemStack stack, boolean closeOnClick) {
		super(stack, closeOnClick);
	}

	public GuiPreviousPage(ConfigurationSection section) {
		super(section);
	}
	
	@Override
	public void onClick(Player player, GuiInventory inventory, ClickType click) {
		PagedGuiManager pages = inventory.getPagedManager();
		if (pages == null) return;
		
		IPTCore.openGui(player, pages.getPage(inventory.getPageNumber() - 1));
	}
	
}
