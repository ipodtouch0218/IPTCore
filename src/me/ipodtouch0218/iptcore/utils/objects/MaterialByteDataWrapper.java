package me.ipodtouch0218.iptcore.utils.objects;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import me.ipodtouch0218.iptcore.utils.GenericUtils;

public class MaterialByteDataWrapper {

	private Material material;
	private byte data;
	
	public MaterialByteDataWrapper(Material m) {
		this(m, (byte) -1);
	}
	public MaterialByteDataWrapper(Material m, byte d) {
		this.material = m;
		this.data = d;
	}
	
	public void setMaterial(Material m) { material = m; }
	public void setData(byte d) { data = d; }
	
	//---Getters---//
	public Material getMaterial() { return material; }
	public byte getData() { return data; }
	
	@SuppressWarnings("deprecation")
	public boolean matches(Block b) {
		return matches(b.getType(), b.getData());
	}
	@SuppressWarnings("deprecation")
	public boolean matches(ItemStack stack) {
		return matches(stack.getType(), (byte) stack.getDurability());
	}
	public boolean matches(Material m, byte d) {
		return (material == m && (data <= -1 || data == d));
	}
	
	//---Static---//
	public static Optional<MaterialByteDataWrapper> fromString(String string) {
		try {
			String[] in = string.split(":");
			
			Material mat = GenericUtils.getEnumFromString(Material.class, in[0]);
			byte data = (in.length > 1 ?  Byte.valueOf(in[1]) : -1);
			
			return Optional.of(new MaterialByteDataWrapper(mat, data));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
}

