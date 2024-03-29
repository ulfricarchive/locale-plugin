package com.ulfric.plugin.locale.message;

import org.w3c.dom.Node;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.commons.xml.XmlHelper;
import com.ulfric.fancymessage.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TextAppender implements Appender {

	INSTANCE;

	private final Pattern placeholder = Pattern.compile("\\$\\{([a-zA-Z0-9\\_\\.]+|import [a-zA-Z-]+)\\}");

	@Override
	public CompiledMessage apply(Node append, CompiledMessage to) {
		String message = XmlHelper.getNodeValue(append);
		if (StringUtils.isEmpty(message)) {
			return to;
		}

		Matcher variables = placeholder.matcher(message);
		if (variables.find()) {
			int textStart = 0;
			do {
				String text = message.substring(textStart, variables.start());

				if (!text.isEmpty()) {
					Message child = new Message();
					child.setText(text);
					
					to.addChild(new PlainMessagePart(child));
				}
				textStart = variables.end();

				String variable = variables.group(1);
				if (variable.startsWith("import ")) {
					to.addChild(new ImportMessagePart(variable.substring("import ".length())));
				} else {
					to.addChild(new PlaceholderMessagePart(variable));
				}
			} while (variables.find());

			String text = message.substring(textStart);
			if (!text.isEmpty()) {
				Message child = new Message();
				child.setText(text);
				to.addChild(new PlainMessagePart(child));
			}

			return to;
		}

		return applyRaw(message, to);
	}

	private CompiledMessage applyRaw(String raw, CompiledMessage to) {
		if (StringUtils.isEmpty(to.base.getText()) && !to.hasChildren()) {
			to.base.setText(raw);
			return to;
		}

		CompiledMessage continuation = to.createChild();
		continuation.base.setText(raw);
		return continuation;
	}

}