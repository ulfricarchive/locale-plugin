package com.ulfric.etruscans.message;

import org.w3c.dom.Node;

import org.bukkit.ChatColor;

import java.util.Objects;

public class ColorAppender implements Appender {

	private final ChatColor color;

	public ColorAppender(ChatColor color) {
		Objects.requireNonNull(color, "color");

		this.color = color;
	}

	@Override
	public Result apply(Node append, CompiledMessage to) {
		if (to.base.getColor() == null) {
			to.base.setColor(color);
			return new Result.Continue(); // TODO optimize, remove object creation
		}

		CompiledMessage continuation = to.createChild();
		continuation.base.setColor(color);

		return new Result.Continue(continuation);
	}

}