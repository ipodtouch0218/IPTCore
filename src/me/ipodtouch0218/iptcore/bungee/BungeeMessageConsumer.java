package me.ipodtouch0218.iptcore.bungee;

@FunctionalInterface
public interface BungeeMessageConsumer<T> {
	T accept(BungeeMessageWrapper incoming);
}