package com.ulfric.plugin.locale.message;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.ulfric.commons.xml.XmlHelper;
import com.ulfric.fancymessage.Message;
import com.ulfric.i18n.content.Details;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CompiledMessage implements MessagePart {

	private static final Map<String, Appender> APPENDERS = new HashMap<>();

	static {
		APPENDERS.put("#text", TextAppender.INSTANCE);
		APPENDERS.put("strikethrough", StrikethroughAppender.INSTANCE);
		APPENDERS.put("underlined", UnderlinedAppender.INSTANCE);
		APPENDERS.put("italic", ItalicAppender.INSTANCE);
		APPENDERS.put("obfuscated", ObfuscatedAppender.INSTANCE);
		APPENDERS.put("hover", new HoverAppender());
		APPENDERS.put("url", new UrlAppender());
		APPENDERS.put("suggest", new SuggestAppender());
		APPENDERS.put("command", new CommandAppender());
		APPENDERS.put("foreach", ForEachAppender.INSTANCE);
		APPENDERS.put("doc", SkipAppender.INSTANCE);
		registerColorAppenders();
	}

	private static void registerColorAppenders() {
		for (ChatColor color : ChatColor.values()) {
			if (color.isColor()) {
				registerColorAppender(color);
			}
		}
	}

	private static void registerColorAppender(ChatColor color) {
		ColorAppender appender = new ColorAppender(color);
		String name = color.getName().toLowerCase();
		APPENDERS.put(name, appender);
		APPENDERS.put(name.replace("_", ""), appender);
	}

	public static CompiledMessage compile(String message) {
		Element root = XmlHelper.parseHumanDocument(message).getDocumentElement();
		return compile(root);
	}

	static CompiledMessage compile(Node message) {
		CompiledMessage base = new CompiledMessage();
		append(base, message);
		return base;
	}

	private static void append(CompiledMessage base, Node message) {
		Appender appender = APPENDERS.get(message.getNodeName().toLowerCase());

		if (appender != null) {
			base = appender.apply(message, base);

			for (Node child : XmlHelper.asList(message.getChildNodes())) {
				append(base, child);
			}
		} else {
			throw new IllegalArgumentException("Cannot handle " + message.getNodeName());
		}
	}

	final Message base;
	private final List<MessagePart> parts;

	protected CompiledMessage() {
		this.base = new Message();
		this.base.setText("");
		this.parts = new ArrayList<>();
	}

	@Override
	public Message toMessage(CommandSender display, Details details) {
		Message base = createBase();

		if (parts.isEmpty()) {
			return base;
		}

		List<Message> extra = new ArrayList<>(parts.size());
		base.setExtra(extra);
		for (MessagePart part : parts) {
			extra.add(part.toMessage(display, details));
		}

		return base;
	}

	protected Message createBase() {
		return new Message(base);
	}

	protected CompiledMessage createChild() {
		CompiledMessage child = new CompiledMessage();
		addChild(child);
		return child;
	}

	protected void addChild(MessagePart child) {
		Objects.requireNonNull(child, "child");
		parts.add(child);
	}

}