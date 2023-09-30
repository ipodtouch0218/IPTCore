package me.ipodtouch0218.iptcore.bungee;

import java.util.concurrent.CompletableFuture;

import lombok.Data;

@Data
public class BungeeFutureWrapper<T> {

	private final CompletableFuture<T> future;
	private final BungeeMessageConsumer<T> consumer;
	
}
