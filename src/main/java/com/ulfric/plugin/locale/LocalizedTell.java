package com.ulfric.plugin.locale;

import org.bukkit.command.CommandSender;

import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.i18n.content.Details;

public class LocalizedTell implements TellService {

	@Inject
	private LocaleService locale;

	@Override
	public Class<TellService> getService() {
		return TellService.class;
	}

	@Override
	public void send(CommandSender display, String message) {
		send(display, message, Details.none());
	}

	@Override
	public void send(CommandSender display, String message, Details details) {
		BukkitMessageLocale locale = this.locale.defaultLocale(); // TODO using default locale
		display.sendMessage(locale.getMessage(display, message, details));
	}

}
