package me.ipodtouch0218.iptcore.utils.objects;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SoundInfo {
	private Sound sound;
	private float pitch, volume;
	SoundInfo(Sound sound, float pitch, float volume) {
		this.sound = sound;
		this.pitch = pitch;
		this.volume = volume;
	}
	
	public Sound getSound() { return sound; }
	public float getPitch() { return pitch; }
	public float getVolume() { return volume; }
	
	public void play(Player... targets) {
		Arrays.stream(targets).forEach(pl -> pl.playSound(pl.getLocation(), sound, volume, pitch));
	}
	public void playAtLocation(Location loc) {
		Bukkit.getOnlinePlayers().forEach(pl -> pl.playSound(loc, sound, volume, pitch));
	}
	public void playAtLocation(Location loc, Player... targets) {
		Arrays.stream(targets).forEach(pl -> pl.playSound(loc, sound, volume, pitch));
	}

	// Static
	public static SoundInfo parseSound(ConfigurationSection section) {
		if (section == null) { return null; }
		try {
			Sound sound = Sound.valueOf(section.getString("name", "").toUpperCase());
			float pitch = (float) section.getDouble("pitch", 1d);
			float volume = (float) section.getDouble("volume", 1d);
			
			return new SoundInfo(sound, pitch, volume);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}