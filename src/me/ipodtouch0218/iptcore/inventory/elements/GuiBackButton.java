package me.ipodtouch0218.iptcore.inventory.elements;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.IPTCore;
import me.ipodtouch0218.iptcore.inventory.GuiInventory;

public class GuiBackButton extends GuiElement {

	public GuiBackButton(ItemStack stack) {
		super(stack);
	}
	
	@Override
	public void onClick(Player player, GuiInventory inventory) {
		player.closeInventory();
		Bukkit.getScheduler().runTaskLater(IPTCore.plugin, () -> {
			IPTCore.openPreviousGui(player);
		}, 1);
	}
	
}
