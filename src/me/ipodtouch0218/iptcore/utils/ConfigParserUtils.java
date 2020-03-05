package me.ipodtouch0218.iptcore.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.google.common.primitives.Ints;

import me.ipodtouch0218.iptcore.inventory.GuiBuilder;
import me.ipodtouch0218.iptcore.inventory.GuiInventory;
import me.ipodtouch0218.iptcore.inventory.elements.GuiBackButton;
import me.ipodtouch0218.iptcore.inventory.elements.GuiCloseButton;
import me.ipodtouch0218.iptcore.inventory.elements.GuiCommandButton;
import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;
import me.ipodtouch0218.iptcore.inventory.elements.GuiToggleButton;

public class ConfigParserUtils {

	@SuppressWarnings("serial")
	private static final HashMap<String,Class<? extends GuiElement>> DEFAULT_ELEMENTS = 
	new HashMap<String,Class<? extends GuiElement>>()
	{{
		put("close", GuiCloseButton.class);
		put("back", GuiBackButton.class);
		put("toggle", GuiToggleButton.class);
		put("command", GuiCommandButton.class);
	}};
	
	public static GuiInventory parseInventory(ConfigurationSection section) {
		return parseInventory(section, null, null);
	}
	
	public static GuiInventory parseInventory(ConfigurationSection section, Map<String,Class<? extends GuiElement>> classes) {
		return parseInventory(section, classes, null);
	}
	
	public static GuiInventory parseInventory(ConfigurationSection section, Map<String,Class<? extends GuiElement>> classes, Map<Object,Object> titleReplMap) {
		if (section == null) { return null; }
		if (classes == null) {
			classes = DEFAULT_ELEMENTS;
		} else {
			classes.putAll(DEFAULT_ELEMENTS);
		}
		
		try {
			int rows = section.getInt("rows", 3);
			String title = ChatColor.translateAlternateColorCodes('&', section.getString("title", "n/a"));
		
			if (titleReplMap != null) {
				title = FormatUtils.stringReplaceColor(title, titleReplMap);
			}
			
			GuiBuilder builder = new GuiBuilder(rows);
			builder.setTitle(title);
			
			if (!section.isConfigurationSection("items")) {		
				return builder.build();
			}
			
			ConfigurationSection itemSection = section.getConfigurationSection("items");
			for (String path : itemSection.getKeys(false)) {
				if (!itemSection.isConfigurationSection(path)) { continue; }
				ConfigurationSection element = itemSection.getConfigurationSection(path);
				
				Class<? extends GuiElement> elementClass = GuiElement.class;
				String elementType = element.getString("function.name", "").toLowerCase();
				if (classes != null) {
					elementClass = classes.getOrDefault(elementType, GuiElement.class);
				}
				
				try {
					GuiElement newElement = elementClass.getConstructor(ConfigurationSection.class).newInstance(element);
					String slots = element.getString("slots", element.getString("slot", ""));
					switch (slots) {
					case "": {
						break;
					}
					case "all": {
						for (int slot = 0; slot < rows*9; slot++) {
							builder.setItem(newElement, slot);
						}
						break;
					}
					default: {
						String[] slotInts = slots.split(",");
						Arrays.stream(slotInts)
							.map(Ints::tryParse)
							.filter(i -> i != null)
							.mapToInt(Integer::intValue)
							.forEach(slot -> builder.setItem(newElement, slot));
						break;
					}
					}
				} catch (Exception e2) { 
					
					// Unable to parse...
					e2.printStackTrace();
					
				}
				
			}
			
			return builder.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static ItemStack parseItem_v1_8(ConfigurationSection section) {
		if (section == null) { return null; }
		Material mat = GenericUtils.getEnumFromString(Material.class, section.getString("type", "").toUpperCase());
		if (mat == null) {
			System.err.println("Unable to parse item at '" + section.getCurrentPath() + "' - Unknown Material Type '" + section.getString("type", null) + "'");
			return null;
		}
		ItemBuilder builder = new ItemBuilder(new ItemStack(mat, section.getInt("amount", 1), (short) section.getInt("data", 0)));
		
		if (mat == Material.SKULL_ITEM && section.isSet("texture")) {
			builder.setTexture(section.getString("texture"));
		}
		
		if (section.isSet("name")) {
			if (section.getString("name").equals("")) {
				builder.setDisplayName("\u00A7r");
			} else {
				builder.setDisplayName(section.getString("name"));
			}
		}
		if (section.isSet("lore")) {
			builder.setLore(section.getStringList("lore"));
		}
		builder.setUnbreakable(section.getBoolean("unbreakable", false));

		for (Map<?,?> enchs : section.getMapList("enchantments")) {
			@SuppressWarnings("unchecked")
			Map<Object,Object> casted = (Map<Object,Object>) enchs;
			try {
				Enchantment name = Enchantment.getByName((String) enchs.get("type"));
				int level = (Integer) (casted.get("level"));
				builder.addEnchantment(name, level);
			} catch (Exception e) {}
		}
		builder.setGlowing(section.getBoolean("enchanted", false));
		
		return builder.build();
	}
}
