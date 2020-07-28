package me.ipodtouch0218.iptcore.inventory.paging;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.inventory.GuiInventory;
import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;

public class GuiNextPage extends GuiElement {

	public GuiNextPage(ItemStack stack, boolean closeOnClick) {
		super(stack, closeOnClick);
	}
	
	public GuiNextPage(ConfigurationSection section) {
		super(section);
	}
	
	@Override
	public void onClick(Player player, GuiInventory inventory, ClickType click) {
		if (!(inventory instanceof PagedGuiInventory)) return;
		((PagedGuiInventory) inventory).nextPage();
		inventory.updateInventory();
	}
	
}
