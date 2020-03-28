package me.ipodtouch0218.iptcore.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.ipodtouch0218.iptcore.IPTCore;

public class BungeeCommunicationHandler implements PluginMessageListener {

	private static boolean INITIALIZED = false;
	private static BungeeCommunicationHandler INSTANCE;
	private static HashMap<String, ArrayList<BungeeFutureWrapper<Object>>> QUEUED_FUTURES = new HashMap<>();
	private static HashMap<String, Consumer<BungeeMessageWrapper>> CUSTOM_LISTENERS = new HashMap<>();
	
	public void initialize() {
		INSTANCE = this;
		JavaPlugin plugin = IPTCore.plugin;
	    Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
	    Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", INSTANCE);
	    INITIALIZED = true;
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord"))
			return;
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		BungeeMessageWrapper bmw = new BungeeMessageWrapper(subchannel, player.getUniqueId(), in);
		
		if (QUEUED_FUTURES.containsKey(subchannel)) {
			ArrayList<BungeeFutureWrapper<Object>> futures = QUEUED_FUTURES.get(subchannel);
			if (futures.size() <= 0)
				return;
			
			BungeeFutureWrapper<Object> wrapper = futures.get(0);
			CompletableFuture<Object> future = wrapper.getFuture();
			
			if (wrapper.getConsumer() != null) {
				future.complete(wrapper.getConsumer().accept(bmw));
				futures.remove(0);
				return;
			}
			
			switch (subchannel) {
			case "IP": {
				String ip = in.readUTF();
				int port = in.readInt();
				future.complete(ip + ":" + port);
				break;
			}
			case "PlayerCount": {
				in.readUTF(); //Waste server name read.
				int count = in.readInt();
				future.complete(count);
				break;
			}
			case "PlayerList": {
				in.readUTF(); //Waste server name read.
				String[] players = in.readUTF().split(","); 
				future.complete(players);
				break;
			}
			case "GetServers": {
				String[] servers = in.readUTF().split(",");
				future.complete(servers);
				break;
			}
			case "GetServer": {
				String server = in.readUTF();
				future.complete(server);
				break;
			}
			case "UUIDOther":
				in.readUTF(); //waste player name response.
			case "UUID": {
				String uuid = in.readUTF();
				uuid = uuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"); //add dashes...
				future.complete(UUID.fromString(uuid));
				break;
			}
			}
			
			futures.remove(0);
		} else if (CUSTOM_LISTENERS.containsKey(subchannel)) {
			CUSTOM_LISTENERS.get(subchannel).accept(bmw);
		}
		
		
	}
	
	//---BUNGEE FUNCTIONS---//
	public static void connectPlayerToServer(String player, String server) {
		ByteArrayDataOutput out = getOutputData("ConnectOther");
		out.writeUTF(player);
		out.writeUTF(server);
		sendPluginMessage(out, null);
	}
	public static void kickPlayer(String player, String reason) {
		ByteArrayDataOutput out = getOutputData("KickPlayer");
		out.writeUTF(player);
		out.writeUTF(reason);
		sendPluginMessage(out, null);
	}
	public static void sendPlayerChatMessage(String player, String message) {
		ByteArrayDataOutput out = getOutputData("Message");
		out.writeUTF(player);
		out.writeUTF(message);
		sendPluginMessage(out, null);
	}
	public static void sendAllChatMessage(String message) {
		sendPlayerChatMessage("ALL", message);
	}
	public static void forwardMessageToServer(String subchannel, String server, Object... message) {
		ByteArrayDataOutput out = getOutputData("Forward");
		out.writeUTF(server);
		out.writeUTF(subchannel);
		
		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			for (Object obj : message) {
				if (obj instanceof Integer) {
					msgout.writeInt((Integer) obj);
				} else if (obj instanceof Short) {
					msgout.writeShort((Short) obj);
				} else if (obj instanceof Long) {
					msgout.writeLong((Long) obj);
				} else if (obj instanceof Double) {
					msgout.writeDouble((Double) obj);
				} else if (obj instanceof Float) {
					msgout.writeFloat((Float) obj);
				} else if (obj instanceof Character) {
					msgout.writeChar((Character) obj);
				} else {
					msgout.writeUTF(obj.toString());
				}
			}
		} catch (IOException exception){
			exception.printStackTrace();
		}
		
		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());
		sendPluginMessage(out, null);
	}
	public static void forwardMessageToOnlineServers(String subchannel, Object... message) {
		forwardMessageToServer(subchannel, "ONLINE", message);
	}
	public static void forwardMessageToAllServers(String subchannel, Object... message) {
		forwardMessageToServer(subchannel, "ALL", message);
	}
	
	public static <T> CompletableFuture<T> customMessage(String subchannel, BungeeMessageConsumer<T> consumer, Object... message) {
		customMessage(subchannel, message);
		return createResponseFuture(subchannel, consumer);
	}
	
	public static void customMessage(String subchannel, Object... message) {
		ByteArrayDataOutput out = getOutputData(subchannel);
		
		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			for (Object obj : message) {
				if (obj instanceof Integer) {
					msgout.writeInt((Integer) obj);
				} else if (obj instanceof Short) {
					msgout.writeShort((Short) obj);
				} else if (obj instanceof Long) {
					msgout.writeLong((Long) obj);
				} else if (obj instanceof Double) {
					msgout.writeDouble((Double) obj);
				} else if (obj instanceof Float) {
					msgout.writeFloat((Float) obj);
				} else if (obj instanceof Character) {
					msgout.writeChar((Character) obj);
				} else {
					msgout.writeUTF(obj.toString());
				}
			}
		} catch (IOException exception){
			exception.printStackTrace();
		}
		
		out.write(msgbytes.toByteArray());
		sendPluginMessage(out, null);
	}
	
	//---BUNGEE GETTERS---//
	public static CompletableFuture<String> getPlayerIP(String player) {
		ByteArrayDataOutput out = getOutputData("IP");
		out.writeUTF(player);
		sendPluginMessage(out, null);
		return createResponseFuture("IP");
	}
	public static CompletableFuture<Integer> getServerPlayerCount(String server) {
		ByteArrayDataOutput out = getOutputData("PlayerCount");
		out.writeUTF(server);
		sendPluginMessage(out, null);
		return createResponseFuture("PlayerCount");
	}
	public static CompletableFuture<Integer> getAllPlayerCount() {
		return getServerPlayerCount("ALL");
	}
	public static CompletableFuture<String[]> getServerPlayerList(String server) {
		ByteArrayDataOutput out = getOutputData("PlayerList");
		out.writeUTF(server);
		sendPluginMessage(out, null);
		return createResponseFuture("PlayerList");
	}
	public static CompletableFuture<String[]> getAllPlayerList() {
		return getServerPlayerList("ALL");
	}
	public static CompletableFuture<String[]> getServers() {
		sendPluginMessage(getOutputData("GetServers"), null);
		return createResponseFuture("GetServers");
	}
	public static CompletableFuture<String> getServerName() {
		sendPluginMessage(getOutputData("GetServer"), null);
		return createResponseFuture("GetServer");
	}
	public static CompletableFuture<UUID> getTrueUUID(String player) {
		ByteArrayDataOutput out = getOutputData("UUIDOther");
		out.writeUTF(player);
		sendPluginMessage(out, null);
		return createResponseFuture("UUIDOther");
	}
	public static CompletableFuture<String> getServerIP(String server) {
		ByteArrayDataOutput out = getOutputData("ServerIP");
		out.writeUTF(server);
		sendPluginMessage(out, null);
		return createResponseFuture("ServerIP");
	}
	
	
	//---HELPERS---//
	private static ByteArrayDataOutput getOutputData(String subchannel) {
		if (!INITIALIZED) {
			new BungeeCommunicationHandler().initialize();
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(subchannel);
		return out;
	}
	private static Player getRandomPlayer() {
		Player pl = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if (pl == null)
			throw new BungeeNoPlayersOnlineException();
		return pl;
	}
	private static <T> CompletableFuture<T> createResponseFuture(String subchannel) {
		return createResponseFuture(subchannel, null);
	}
	@SuppressWarnings("unchecked")
	private static <T> CompletableFuture<T> createResponseFuture(String subchannel, BungeeMessageConsumer<T> consumer) {
		CompletableFuture<T> future = new CompletableFuture<T>();
		BungeeFutureWrapper<T> wrapper = new BungeeFutureWrapper<T>(future, consumer);
		ArrayList<BungeeFutureWrapper<Object>> futures = QUEUED_FUTURES.get(subchannel);
		if (futures == null) {
			futures = new ArrayList<BungeeFutureWrapper<Object>>();
			QUEUED_FUTURES.put(subchannel, futures);
		}
		
		futures.add((BungeeFutureWrapper<Object>) wrapper);
		return future;
	}
	private static void sendPluginMessage(ByteArrayDataOutput out, Player player) {
		if (player == null)
			player = getRandomPlayer();
		player.sendPluginMessage(IPTCore.plugin, "BungeeCord", out.toByteArray());
	}
	
	public static void setCustomListener(String subchannel, Consumer<BungeeMessageWrapper> listener) {
		CUSTOM_LISTENERS.put(subchannel, listener);
	}
	
}
