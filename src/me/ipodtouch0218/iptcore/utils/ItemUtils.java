package me.ipodtouch0218.iptcore.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
	
	private static Enchantment GLOW_ENCHANTMENT;
	
	private ItemUtils() {}
	
	public static ItemStack nameItem(ItemStack stack, String name, boolean glow) {		
		return nameItem(stack, name, glow, (String[]) null);
	}
	
	public static ItemStack nameItem(ItemStack stack, String name, boolean glow, String... lore) {
		ItemMeta meta = stack.getItemMeta();
		
		if (name != null) {
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		}
		if (lore != null) {
			for (int i = 0 ; i < lore.length; i++) {
				if (lore[i] == null) { continue; }
				lore[i] = ChatColor.translateAlternateColorCodes('&', lore[i]);
			}
			meta.setLore(Arrays.asList(lore));
		}
		if (glow) {
			glowItem(stack);
		}
		
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static void addLore(ItemStack item, String... lines) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = null;
		if (meta.hasLore()) {
			lore = meta.getLore();
		} else {
			lore = new ArrayList<>();
		}
		
		
		for (int i = 0 ; i < lines.length; i++) {
			if (lines[i] == null) { continue; }
			lines[i] = ChatColor.translateAlternateColorCodes('&', lines[i]);
		}
		
		lore.addAll(Arrays.asList(lines));
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public static void glowItem(ItemStack item) {
		if (GLOW_ENCHANTMENT == null) {
			@SuppressWarnings("deprecation")
			NamespacedKey key = new NamespacedKey("test.key", "glow");
			if (Enchantment.getByKey(key) != null) {
				GLOW_ENCHANTMENT = Enchantment.getByKey(key);
			} else {
				GLOW_ENCHANTMENT = new Enchantment(key) {
					public String getName() { return null; }
					public int getMaxLevel() { return 0; }
					public int getStartLevel() { return 0; }
					public EnchantmentTarget getItemTarget() { return EnchantmentTarget.ALL; }
					public boolean isTreasure() { return false; }
					public boolean isCursed() { return false; }
					public boolean conflictsWith(Enchantment other) { return false; }
					public boolean canEnchantItem(ItemStack item) { return true; }
				};
		        try {
		            Field f = Enchantment.class.getDeclaredField("acceptingNew");
		            f.setAccessible(true);
		            f.set(null, true);
		            Enchantment.registerEnchantment(GLOW_ENCHANTMENT);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		}
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(GLOW_ENCHANTMENT, 0, true);
		item.setItemMeta(meta);
	}
	
	public static ItemStack parseItem_v1_8(ConfigurationSection section) {
		Material mat = null;
		try {
			mat = Material.valueOf(section.getString("type", "BEDROCK").toUpperCase());
		} catch (Exception e) {
			System.err.println("Unable to parse item at '" + section.getCurrentPath() + "' - Unknown Material '" + section.getString("type", null) + "'");
			return null;
		}
		@SuppressWarnings("deprecation")
		ItemBuilder builder = new ItemBuilder(new ItemStack(mat, section.getInt("amount", 1), (short) section.getInt("data", 0)));
		
		if (section.isSet("name")) {
			if (section.getString("name").equals("")) {
				builder.setDisplayName("§r");
			} else {
				builder.setDisplayName(section.getString("name"));
			}
		}
		if (section.isSet("lore")) {
			builder.setLore(section.getStringList("lore"));
		}
		builder.setGlowing(section.getBoolean("enchanted", false));
		
		return builder.build();
	}
}
