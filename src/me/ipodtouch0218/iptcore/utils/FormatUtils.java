package me.ipodtouch0218.iptcore.utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FormatUtils {

	private FormatUtils() {}
	
	
	public static String formatLocation(Location loc, boolean decimal, boolean includeWorld) {
		String output = "";
		if (loc == null) {
			return output;
		}
		
		if (includeWorld) {
			output += loc.getWorld().getName() + " - ";
		}
		
		if (decimal) {
			output += String.format("%.2d, %.2d, %.2d", loc.getX(), loc.getY(), loc.getZ());
		} else {
			output += String.format("%d, %d, %d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		}
		
		return output;
	}
	
	public static String stringReplace(String input, Map<String,?> replMap) {
		if (input == null || input.equals("")) { return ""; }
		
		for (Entry<String,?> replacement : replMap.entrySet()) {
			input = input.replace(replacement.getKey(), "" + replacement.getValue());
		}
		return input;
	}
	
	public static String stringReplaceColor(String input, Map<String,?> replMap) {
		return ChatColor.translateAlternateColorCodes('&', stringReplace(input, replMap));
	}
	
	public static void stringReplaceNameLore(ItemStack input, Map<String,?> replMap) {
		ItemMeta meta = input.getItemMeta();
		if (meta.hasDisplayName()) {
			meta.setDisplayName(stringReplace(meta.getDisplayName(), replMap));
		}
		if (meta.hasLore()) {
			meta.setLore(meta.getLore().stream().map(str -> stringReplace(str, replMap)).collect(Collectors.toList()));
		}
		input.setItemMeta(meta);
	}
}
