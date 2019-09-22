package me.ipodtouch0218.iptcore.inventory.elements;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.inventory.GuiInventory;

public class GuiCommandButton extends GuiElement {

	private List<String> commands;
	private boolean asPlayer;
	
	public GuiCommandButton(ItemStack stack, boolean closeOnClick, List<String> commands) {
		super(stack, closeOnClick);
		this.commands = commands;
	}
	public GuiCommandButton(ConfigurationSection section) {
		super(section);
		commands = section.getStringList("function.commands");
		asPlayer = section.getBoolean("function.as-player", false);
	}

	@Override
	public void onClick(Player player, GuiInventory inventory, ClickType click) {
		CommandSender sender = (asPlayer ? player : Bukkit.getConsoleSender());
		commands.forEach(str -> {
			Bukkit.dispatchCommand(sender, str);
		});
	}
	
}
