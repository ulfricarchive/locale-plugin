package com.ulfric.etruscans.message;

import org.bukkit.command.CommandSender;

import com.ulfric.commons.collection.ListHelper;
import com.ulfric.fancymessage.Message;
import com.ulfric.i18n.content.Content;
import com.ulfric.i18n.content.Detail;
import com.ulfric.i18n.content.Details;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ForEachCompiledMessage extends CompiledMessage {

	private final String elements;
	private final String element;
	private final CompiledMessage delimiter;

	public ForEachCompiledMessage(String elements, String element, CompiledMessage delimiter) { // TODO validate
		super(CompiledMessage.emptyMessage());

		Objects.requireNonNull(elements, "elements");
		Objects.requireNonNull(element, "element");

		this.elements = elements;
		this.element = element;
		this.delimiter = delimiter;
	}

	@Override
	public Message toMessage(CommandSender display, Details details) { // TODO break method up, much too complex
		Detail existing = details.get(elements);
		if (existing == null) {
			return super.toMessage(display, details);
		}

		List<Content> content = existing.getAllContent();
		if (content.isEmpty()) {
			return super.toMessage(display, details);
		}

		Message base = CompiledMessage.emptyMessage();
		int contentSize = content.size();
		int size = contentSize * (delimiter == null ? 1 : 2);
		List<Message> extra = new ArrayList<>(size);
		base.setExtra(extra);

		int lastIndex = contentSize - 1;
		for (int x = 0; x < contentSize; x++) {
			Content element = content.get(x);
			Object potentialIterable = element.getValue();

			if (potentialIterable instanceof Iterable) {
				List<?> iterable = ListHelper.asList((Iterable<?>) potentialIterable);
				int iterableSize = iterable.size();
				int iterableLastIndex = iterableSize - 1;
				for (int y = 0; y < iterableSize; y++) {
					Object contentEntry = iterable.get(y);
					details.add(this.element, contentEntry);
					extra.add(super.toMessage(display, details));

					if (delimiter != null) {
						if (y != iterableLastIndex || x != lastIndex) {
							extra.add(delimiter.toMessage(display, details)); // TODO optimize to only use one new delimiter message if needed
						}
					}
				}
			} else {
				details.add(this.element, element);
				extra.add(super.toMessage(display, details));

				if (delimiter != null && x != lastIndex) {
					extra.add(delimiter.toMessage(display, details)); // TODO optimize to only use one new delimiter message if needed
				}
			}
		}

		return base;
	}

}