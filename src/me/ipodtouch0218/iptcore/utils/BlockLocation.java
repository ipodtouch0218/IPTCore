package me.ipodtouch0218.iptcore.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class BlockLocation extends Location {

	public BlockLocation(World world, double x, double y, double z) {
		super(world, x, y, z);
	}
	public BlockLocation(Location loc) {
		super(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	
	@Override
	public void setX(double x) {
		super.setX((int) x);
	}
	
	@Override
	public void setY(double y) {
		super.setY((int) y);
	}
	
	@Override
	public void setZ(double z) {
		super.setZ((int) z);
	}
	
	@Override
	public void setPitch(float pitch) {
		//do nothing.
	}
	
	@Override
	public void setYaw(float yaw) {
		//do nothing.
	}
	
	public int getChunkX() {
		return getBlockX()/16;
	}
	
	public int getChunkZ() {
		return getBlockZ()/16;
	}
	
	public boolean isChunkLoaded() {
		return getWorld().isChunkLoaded(getChunkX(), getChunkZ());
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> newMap = new LinkedHashMap<>();
		newMap.put("world", getWorld().getName());
		newMap.put("x", (int) getX());
		newMap.put("y", (int) getY());
		newMap.put("z", (int) getZ());
		return newMap;
	}
	
	public static BlockLocation deserialize(Map<String,Object> map) {
		World world = Bukkit.getWorld((String) map.get("world"));
		int x = (int) map.get("x");
		int y = (int) map.get("y");
		int z = (int) map.get("z");
		return new BlockLocation(world, x, y, z);
	}
	
}
