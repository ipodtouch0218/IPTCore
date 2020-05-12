package me.ipodtouch0218.iptcore.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
	
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

    @SuppressWarnings("deprecation")
	public static void applyCustomHeadTexture(ItemStack item, String texture) {
    	UUID hashAsId = new UUID(texture.hashCode(), texture.hashCode());
    	Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + texture + "\"}]}}}");
    }
}
