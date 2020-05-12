package me.ipodtouch0218.iptcore.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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
	
	public static String stringReplace(String input, Map<?,?> replMap) {
		if (input == null || input.equals("")) { return ""; }
		
		for (Entry<?,?> replacement : replMap.entrySet()) {
			input = input.replace("" + replacement.getKey(), "" + replacement.getValue());
		}
		return input;
	}
	
	public static String stringReplaceColor(String input, Map<?,?> titleReplMap) {
		return ChatColor.translateAlternateColorCodes('&', stringReplace(input, titleReplMap));
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
