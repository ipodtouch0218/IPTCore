package me.ipodtouch0218.iptcore.bungee;

public class BungeeNoPlayersOnlineException extends RuntimeException {

	private static final long serialVersionUID = 6707194508567253205L;

	public BungeeNoPlayersOnlineException() {
		super("No players are online on this server, cannot send Bungee messages!");
	}
	
	
}
