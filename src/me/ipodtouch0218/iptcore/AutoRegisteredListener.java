package me.ipodtouch0218.iptcore;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class AutoRegisteredListener implements Listener {

	public AutoRegisteredListener(Plugin plugin) {	
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
}
