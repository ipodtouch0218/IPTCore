package me.ipodtouch0218.iptcore.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder {

	
	private ItemStack currentStack;
	private ItemMeta meta;

	
	//---Constructors---//
	/**
	 * Creates a new ItemBuilder with the given Material.
	 * @param mat Material type to create a new ItemStack with.
	 */
	public ItemBuilder(Material mat) {
		this(new ItemStack(mat));
	}
	
	/**
	 * Creates an ItemBuilder with the given ItemStack. Does not clone
	 * the itemstack, see {@link ItemBuilder#copyOf(ItemStack)}
	 * @param stack
	 */
	public ItemBuilder(ItemStack stack) {
		currentStack = stack;
		meta = currentStack.getItemMeta();
	}
	
	//---BUILD---//
	/**
	 * Applies all changes to the itemmeta and returns the item.
	 * @return The built ItemStack instance
	 */
	public ItemStack build() {
		currentStack.setItemMeta(meta);
		return currentStack;
	}
	
	
	//---ItemStack Properties---//
	
	/**
	 * @param mat Material type to change to.
	 * @return Current ItemBuilder instance.
	 */
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
	
	/**
	 * Adds a valid enchantment to the item.
	 * @param ench The enchantment to add.
	 * @param level The enchantment's level.
	 * @return Current ItemBuilder instance.
	 */
	public ItemBuilder addEnchantment(Enchantment ench, int level) {
		meta.addEnchant(ench, level, true);
		return this;
	}
	
	//---Display-related Methods---//
	
	/**
	 * Sets the name of the item. Automatically translates Minecraft color codes using
	 * {@link ChatColor#translateAlternateColorCodes(char, String)}.
	 * @param name The name to set the ItemStack to.
	 * @return Current ItemBuilder instance.
	 */
	public ItemBuilder setDisplayName(String name) {
		if (name == null) { 
			//we cant translate a null string for whatever reason, we need a null check.
			meta.setDisplayName(null);
		} else {
			meta.setDisplayName(FormatUtils.stringColor(name));
		}
		return this; 
	}
	
	/**
	 * Replaces the current item lore with a new lore. Automatically translates Minecraft color codes using
	 * {@link ChatColor#translateAlternateColorCodes(char, String)}.
	 * @param newLore Lore to change the item's lore to.
	 * @return Current ItemBuilder instance.
	 */
	public ItemBuilder setLore(String... newLore) {
		setLore(Arrays.asList(newLore));
		return this;
	}
	/**
	 * Replaces the current item lore with a new lore. Automatically translates Minecraft color codes using
	 * {@link ChatColor#translateAlternateColorCodes(char, String)}.
	 * @param newLore Lore to change the item's lore to.
	 * @return Current ItemBuilder instance.
	 */
	public ItemBuilder setLore(List<String> newLore) {

		newLore = newLore.stream()
				.map(s -> ChatColor.translateAlternateColorCodes('&',(s==null?"":s)))
				.collect(Collectors.toList());
		
		meta.setLore(newLore);
		return this;
	}
	/**
	 * Adds additional lore lines to the item's existing lore. Automatically translates Minecraft color codes using
	 * {@link ChatColor#translateAlternateColorCodes(char, String)}.
	 * @param additionalLore Lore to add to the item's existing lore.
	 * @return Current ItemBuilder instance.
	 */
	public ItemBuilder addLore(String... additionalLore) {
		addLore(Arrays.asList(additionalLore));
		return this;
	}
	/**
	 * Adds additional lore lines to the item's existing lore. Automatically translates Minecraft color codes using
	 * {@link ChatColor#translateAlternateColorCodes(char, String)}.
	 * @param additionalLore Lore to add to the item's existing lore.
	 * @return Current ItemBuilder instance.
	 */
	public ItemBuilder addLore(List<String> additionalLore) {
		
		additionalLore = additionalLore.stream()
				.map(s -> ChatColor.translateAlternateColorCodes('&',(s==null?"":s)))
				.collect(Collectors.toList());

		List<String> finalLore = new ArrayList<>();
		if (meta.hasLore()) {
			finalLore.addAll(meta.getLore());
		}
		finalLore.addAll(additionalLore);
		meta.setLore(finalLore);
		return this;
	}
	
	public ItemBuilder setUnbreakable(boolean value) {
		try {
			meta.setUnbreakable(value);
		} catch (Throwable e) {
			if (value) {
				meta.addEnchant(Enchantment.DURABILITY, 10, true);
			} else {
				meta.removeEnchant(Enchantment.DURABILITY);
			}
		}
		return this;
	}
	
	@SuppressWarnings("deprecation")
	public ItemBuilder setTexture(String texture) {
		if (meta instanceof SkullMeta) {
			if (GenericUtils.isValidPlayerName(texture)) {
				((SkullMeta) meta).setOwner(texture);
				return this;
			}
			ItemStack build = build();
			ItemUtils.applyCustomHeadTexture(build, texture);
			meta = build.getItemMeta();
		}
		return this;
	}
	
	@SuppressWarnings("deprecation")
	public ItemBuilder setTexture(OfflinePlayer player) {
		if (meta instanceof SkullMeta) {
			((SkullMeta) meta).setOwner(player.getName());
		}
		return this;
	}
	
	//---Misc Features---//
	public ItemBuilder setGlowing(boolean value) {
		if (value) {
			meta.addEnchant(Enchantment.ARROW_FIRE, 2, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		} else {
			if (meta.getEnchantLevel(Enchantment.ARROW_FIRE) == 2) {
				meta.removeEnchant(Enchantment.ARROW_FIRE);
			}
			meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		return this;
	}
	
	//---static---//
	public static ItemBuilder copyOf(ItemStack stack) {
		return new ItemBuilder(stack.clone());
	}
	
}
