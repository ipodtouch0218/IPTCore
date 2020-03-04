package me.ipodtouch0218.iptcore.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

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
		//TODO: fix glow enchantment
		
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static void setLore(ItemStack item, String... lines) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(
			Arrays.stream(lines)
				.map(str -> ChatColor.translateAlternateColorCodes('&', str))
				.collect(Collectors.toList())
		);
		item.setItemMeta(meta);
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

    public static void applyCustomHeadTexture(SkullMeta meta, String texture) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", texture));
        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }
	
//	public static void glowItem(ItemStack item) {
//		if (GLOW_ENCHANTMENT == null) {
//			@SuppressWarnings("deprecation")
//			NamespacedKey key = new NamespacedKey("test.key", "glow");
//			if (Enchantment.getByKey(key) != null) {
//				GLOW_ENCHANTMENT = Enchantment.getByKey(key);
//			} else {
//				GLOW_ENCHANTMENT = new Enchantment(key) {
//					public String getName() { return null; }
//					public int getMaxLevel() { return 0; }
//					public int getStartLevel() { return 0; }
//					public EnchantmentTarget getItemTarget() { return EnchantmentTarget.ALL; }
//					public boolean isTreasure() { return false; }
//					public boolean isCursed() { return false; }
//					public boolean conflictsWith(Enchantment other) { return false; }
//					public boolean canEnchantItem(ItemStack item) { return true; }
//				};
//		        try {
//		            Field f = Enchantment.class.getDeclaredField("acceptingNew");
//		            f.setAccessible(true);
//		            f.set(null, true);
//		            Enchantment.registerEnchantment(GLOW_ENCHANTMENT);
//		        } catch (Exception e) {
//		            e.printStackTrace();
//		        }
//			}
//		}
//		ItemMeta meta = item.getItemMeta();
//		meta.addEnchant(GLOW_ENCHANTMENT, 0, true);
//		item.setItemMeta(meta);
//	}
}
