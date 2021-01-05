package me.ipodtouch0218.iptcore.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import me.ipodtouch0218.iptcore.inventory.elements.GuiRefreshButton;
import me.ipodtouch0218.iptcore.inventory.elements.GuiToggleButton;
import me.ipodtouch0218.iptcore.inventory.paging.GuiNextPage;
import me.ipodtouch0218.iptcore.inventory.paging.GuiPreviousPage;

public class ConfigParserUtils {

	@SuppressWarnings("serial")
	private static final HashMap<String,Class<? extends GuiElement>> DEFAULT_ELEMENTS = 
	new HashMap<String,Class<? extends GuiElement>>()
	{{
		put("close", GuiCloseButton.class);
		put("back", GuiBackButton.class);
		put("toggle", GuiToggleButton.class);
		put("command", GuiCommandButton.class);
		put("next-page", GuiNextPage.class);
		put("previous-page", GuiPreviousPage.class);
		put("refresh", GuiRefreshButton.class);
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
			int rows = section.getInt("rows", section.getInt("size", 3));
			String title = FormatUtils.stringColor(section.getString("title", section.getString("name", "Not Set.")));
		
			if (titleReplMap != null) {
				title = FormatUtils.stringReplaceColor(title, titleReplMap);
			}
			
			GuiBuilder builder = new GuiBuilder(rows);
			builder.setTitle(title);
			
			if (section.isSet("paged-slots")) {
				List<Integer> pagedSlots = new ArrayList<>();
				
				String slots = section.getString("paged-slots");
				String[] slotInts = slots.split(",");
				for (String ints : slotInts) {
					if (ints.matches("\\d+-\\d+")) {
						String[] split = ints.split("-");
						Integer from = Ints.tryParse(split[0]);
						Integer to = Ints.tryParse(split[1]);
						if (from == null || to == null) break;
						for (; from < to; from++) {
							pagedSlots.add(from);
						}
					} else {
						Integer slot = Ints.tryParse(ints);
						if (slot == null) break;
						pagedSlots.add(slot);
					}
				}
				
				builder.setPagedSlots(pagedSlots);
				builder.setPaged(true);
			}
			
			if (!section.isConfigurationSection("items")) {		
				return builder.build();
			}
			
			ConfigurationSection itemSection = section.getConfigurationSection("items");
			for (String path : itemSection.getKeys(false)) {
				if (!itemSection.isConfigurationSection(path)) { continue; }
				ConfigurationSection element = itemSection.getConfigurationSection(path);
				
				String elementType = element.getString("function.name", "").toLowerCase();
				Class<? extends GuiElement> elementClass = classes.getOrDefault(elementType, GuiElement.class);
				
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
						for (String ints : slotInts) {
							if (ints.matches("\\d+-\\d+")) {
								String[] split = ints.split("-");
								Integer from = Ints.tryParse(split[0]);
								Integer to = Ints.tryParse(split[1]);
								if (from == null || to == null) break;
								for (; from < to; from++) {
									builder.setItem(newElement, from);
								}
							} else {
								Integer slot = Ints.tryParse(ints);
								if (slot == null) break;
								builder.setItem(newElement, slot);
							}
						}
					}
					}
				} catch (Exception e2) { 
					// Unable to parse...
					System.err.printf("Cannot initiate GuiElement type '%s' from config.\n", elementClass.getName());
					e2.printStackTrace();
				}
			}
			return builder.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack parseItem(ConfigurationSection section) {
		if (section == null) { return null; }
		if (!section.isSet("type")) {
			System.err.printf("Unable to parse item at '%s' - Material Type Not Specified.\n", section.getCurrentPath());
			return null;
		}
		Material mat = GenericUtils.getEnumFromString(Material.class, section.getString("type").toUpperCase());
		if (mat == null) {
			System.err.printf("Unable to parse item at '%s' - Unknown Material Type '%s'\n", section.getCurrentPath(), section.getString("type"));
			return null;
		}
		ItemBuilder builder = new ItemBuilder(new ItemStack(mat, section.getInt("amount", 1), (short) section.getInt("data", 0)));
		
		if (section.isSet("texture")) {
			builder.setTexture(section.getString("texture"));
		}
		
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
	
	@Deprecated
	public static ItemStack parseItem_v1_8(ConfigurationSection section) {
		return parseItem(section);
	}
}
