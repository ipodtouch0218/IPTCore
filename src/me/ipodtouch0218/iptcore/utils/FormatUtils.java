package me.ipodtouch0218.iptcore.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FormatUtils {

	private FormatUtils() {}
	
	private static final Comparator<TimeUnit> TIMEUNIT_COMPARATOR = new Comparator<TimeUnit>() {
		@Override
		public int compare(TimeUnit o1, TimeUnit o2) {
			return o1.compareTo(o2);
		}
	};
	private static final HashMap<TimeUnit, String> TIMEUNIT_SUFFIXES = new HashMap<>();
	static {
		TIMEUNIT_SUFFIXES.put(TimeUnit.NANOSECONDS, "ns");
		TIMEUNIT_SUFFIXES.put(TimeUnit.MICROSECONDS, "\u03BCs");
		TIMEUNIT_SUFFIXES.put(TimeUnit.MILLISECONDS, "ms");
		TIMEUNIT_SUFFIXES.put(TimeUnit.SECONDS, "s");
		TIMEUNIT_SUFFIXES.put(TimeUnit.MINUTES, "m");
		TIMEUNIT_SUFFIXES.put(TimeUnit.HOURS, "h");
		TIMEUNIT_SUFFIXES.put(TimeUnit.DAYS, "d");
	}
	public static final ChatColor[] ALL_COLORS = {ChatColor.AQUA, ChatColor.BLACK, ChatColor.BLUE, ChatColor.DARK_AQUA,
			ChatColor.DARK_BLUE, ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED,
			ChatColor.GOLD, ChatColor.GRAY, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.RED, ChatColor.YELLOW, ChatColor.WHITE};
	
	public static String formatDurationMillis(long duration, TimeUnit... units) {
		String output = "";
		List<TimeUnit> unitsList = Arrays.asList(units);
		unitsList.sort(TIMEUNIT_COMPARATOR);
		
		for (int i = unitsList.size()-1; i >= 0; i--) {
			TimeUnit unit = unitsList.get(i);
			long unitsInAMilli = unit.toMillis(1);
			long count = duration / unitsInAMilli;
			if (count == 0 && i != 0)
				continue;
			duration -= (unitsInAMilli * count);
			output += count + TIMEUNIT_SUFFIXES.get(unit) + " ";
		}
		
		return output.trim();
	}
	
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
	
	public static String formatItemName(ItemStack item) {
		if (item.getItemMeta().hasDisplayName())
			return item.getItemMeta().getDisplayName();
		return WordUtils.capitalizeFully(item.getType().name().replace("_", " "));
	}
	
	public static String stringColor(String input) {
		if (input == null || input.equals("")) { return ""; }
		
		String current = null;
		StringBuilder builder = new StringBuilder(input);
		while ((current = builder.toString()).matches(".*?&#[a-f0-9]{6}.*?")) {
			int index = current.indexOf("&#");
			String color = current.substring(index+2, index+8);
			StringBuilder colorBuilder = new StringBuilder("§x");
			color.chars().forEach(i -> colorBuilder.append("§").append((char) i));
			builder.replace(index, index+8, "§x" + colorBuilder.toString());
		}
		try {
			return ChatColor.translateAlternateColorCodes('&', builder.toString());
		} catch (Throwable t) {
			return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', builder.toString());
		}
	}
	
	public static String stringReplace(String input, Map<? extends Object,? extends Object> replMap) {
		if (input == null || input.equals("")) { return ""; }
		
		for (Entry<?,?> replacement : replMap.entrySet()) {
			input = input.replace("" + replacement.getKey(), "" + replacement.getValue());
		}
		return input;
	}
	
	public static String stringReplaceColor(String input, Map<? extends Object,? extends Object> titleReplMap) {
		return stringColor(stringReplace(input, titleReplMap));
	}
	
	public static void stringReplaceNameLore(ItemStack input, Map<? extends Object,? extends Object> replMap) {
		ItemMeta meta = input.getItemMeta();
		if (meta.hasDisplayName()) {
			meta.setDisplayName(stringReplace(meta.getDisplayName(), replMap));
		}
		if (meta.hasLore()) {
			meta.setLore(meta.getLore().stream().map(str -> stringReplace(str, replMap)).collect(Collectors.toList()));
		}
		input.setItemMeta(meta);
	}
	
	public static String translateCertainColorCodes(char altChar, String in, ChatColor... colors) {
		for (ChatColor cc : colors) {
			in = in.replace(altChar + "" + cc.getChar(), cc.toString());
		}
		return in;
	}
}
