package me.ipodtouch0218.iptcore.bungee;

import java.util.UUID;

import com.google.common.io.ByteArrayDataInput;

import lombok.Data;

@Data
public class BungeeMessageWrapper {

	private final String channel;
	private final UUID player;
	private final ByteArrayDataInput message;
	
}
