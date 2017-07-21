package com.ulfric.etruscans;

import net.md_5.bungee.chat.ComponentSerializer;

import org.bukkit.command.CommandSender;

import com.ulfric.andrew.Sender;
import com.ulfric.etruscans.locale.Locale;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Messages {

	private static final Map<String, CompiledMessage> COMPILED_TELLRAW = new HashMap<>();

	public static void send(Sender sender, String message) {
		send(sender(sender), message);
	}

	public static void send(Sender sender, String message, Map<String, String> context) {
		send(sender(sender), message, context);
	}

	private static CommandSender sender(Sender sender) {
		Object handle = sender.handle();
		if (handle instanceof CommandSender) {
			return (CommandSender) handle;
		}
		throw new IllegalArgumentException("Not a BukkitSender: " + sender);
	}

	public static void send(CommandSender sender, String message) {
		send(sender, message, Collections.emptyMap());
	}

	public static void send(CommandSender sender, String message, Map<String, String> context) {
		message = Locale.lookup(message);
		CompiledMessage send = getCompiledTellrawMessage(message);
		message = send.apply(sender, context);
		sender.spigot().sendMessage(ComponentSerializer.parse(message)); // TODO proper caching (requires whole Etruscans rewrite)
		return;
		// TODO
	}

	private static CompiledMessage getCompiledTellrawMessage(String message) {
		return COMPILED_TELLRAW.computeIfAbsent(message, CompiledMessage::compileRaw);
	}

}