package me.ipodtouch0218.iptcore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.ipodtouch0218.iptcore.bungee.BungeeCommunicationHandler;
import me.ipodtouch0218.iptcore.inventory.GuiInventory;
import me.ipodtouch0218.iptcore.inventory.InventoryListener;

public class IPTCore {

	private static IPTCore instance;
	public static JavaPlugin plugin;
	
	private InventoryListener invListener = new InventoryListener();
	
	public IPTCore() {
		Bukkit.getPluginManager().registerEvents(invListener, plugin);
	}
	
	
	//---STATIC METHODS---//
	public static void initialize(JavaPlugin pluginn) {
		if (instance != null) { 
			onDisable();
		}
		plugin = pluginn;
		instance = new IPTCore();
	}
	public static void onDisable() {
		instance.invListener.closeAll();
		BungeeCommunicationHandler.uninitialize();
	}
	
	public static void openGui(Player player, GuiInventory gui) {
		instance.invListener.openGui(player, gui);
		gui.updateInventory();
	}
	
	public static void openPreviousGui(Player player) {
		instance.invListener.openPreviousGui(player);
	}
}
