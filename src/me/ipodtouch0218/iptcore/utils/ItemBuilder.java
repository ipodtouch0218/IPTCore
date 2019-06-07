package me.ipodtouch0218.iptcore.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

	private static Enchantment GLOW_ENCHANTMENT;
	
	////
	
	private ItemStack currentStack;
	private ItemMeta meta;

	
	//---Constructors---//
	public ItemBuilder(Material mat) {
		this(new ItemStack(mat));
	}
	public ItemBuilder(ItemStack stack) {
		currentStack = stack;
		meta = currentStack.getItemMeta();
	}
	
	//---BUILD---//
	public ItemStack build() {
		currentStack.setItemMeta(meta);
		return currentStack;
	}
	
	
	//---ItemStack Properties---//
	
	public ItemBuilder setType(Material mat) {
		//setType may change item meta, and if theyre incompatible then oops.
		currentStack.setItemMeta(meta);
		currentStack.setType(mat);
		meta = currentStack.getItemMeta();
		return this;
	}
	public ItemBuilder setAmount(int amount) {
		currentStack.setAmount(amount);
		return this;
	}
	
	//---Enchantment Methods---//
	
	public ItemBuilder addValidEnchantment(Enchantment ench, int level) {
		try {
			currentStack.addEnchantment(ench, level);
		} catch (Exception e) {}
		
		return this;
	}
	public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
		currentStack.addUnsafeEnchantment(ench, level);
		return this;
	}
	
	//---Display-related Methods---//
	
	public ItemBuilder setDisplayName(String name) {
		if (name == null) { 
			//we cant translate a null string for whatever reason, we need a null check.
			meta.setDisplayName(null);
		} else {
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		}
		return this; 
	}
	
	public ItemBuilder setLore(String... newLore) {
		setLore(Arrays.asList(newLore));
		return this;
	}
	public ItemBuilder setLore(List<String> newLore) {

		newLore = newLore.stream().filter(s -> (s != null))
				.map(s -> ChatColor.translateAlternateColorCodes('&', s))
				.collect(Collectors.toList());
		
		meta.setLore(newLore);
		return this;
	}
	public ItemBuilder addLore(String... additionalLore) {
		addLore(Arrays.asList(additionalLore));
		return this;
	}
	public ItemBuilder addLore(List<String> additionalLore) {
		
		additionalLore = additionalLore.stream().filter(s -> (s != null))
				.map(s -> ChatColor.translateAlternateColorCodes('&', s))
				.collect(Collectors.toList());

		List<String> finalLore = new ArrayList<>();
		if (meta.hasLore()) {
			finalLore.addAll(meta.getLore());
		}
		finalLore.addAll(additionalLore);
		meta.setLore(finalLore);
		return this;
	}
	
	//---Misc Features---//
	public ItemBuilder setGlowing(boolean value) {
		if (GLOW_ENCHANTMENT == null) {
			createGlowEnchantment();
		}
		//TODO: setGlowing false
		
		currentStack.addUnsafeEnchantment(GLOW_ENCHANTMENT, 0);
		return this;
	}

	//---static---//
	public static ItemBuilder copyOf(ItemStack stack) {
		return new ItemBuilder(stack.clone());
	}
	
	private static void createGlowEnchantment() {
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
	
}
