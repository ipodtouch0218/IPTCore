package me.ipodtouch0218.iptcore.utils;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class GenericUtils {

	private GenericUtils() {}
	
	public static Entity getEntityViaUUID(UUID uuid) {
		try {
			return (Entity) ReflectionUtils.getRefClass(Bukkit.class).findMethodByName("getEntity").call(uuid);
		} catch (RuntimeException e) {
			//no getEntity method.
			for (World w : Bukkit.getWorlds()) {
				List<Entity> ens = w.getEntities();
				for (Entity en : ens) {
					if (en.getUniqueId().equals(uuid)) {
						return en;
					}
				}
			}
			return null;
		}
	}
	
	public static <T> T getRandomElement(Collection<T> collection) {
		return collection.stream()
				.skip((int) (Math.random()*collection.size()))
				.findFirst()
				.orElse(null);
	}
	public static <T> T getRandomElement(T[] t) {
		return t[(int) (Math.random()*t.length)];
	}
	
	public static boolean isValidPlayerName(String in) {
		return in.matches("[a-zA-Z0-9_]{3,16}");
	}
	
	public static <T extends Enum<T>> T getEnumFromString(Class<T> enumClass, String input) {
		try {
			return Enum.valueOf(enumClass, input);
		} catch (Exception e) {}
		return null;
	}
}
