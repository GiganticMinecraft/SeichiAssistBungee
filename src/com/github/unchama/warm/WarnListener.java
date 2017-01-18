package com.github.unchama.warm;

import java.util.Map;

import com.github.unchama.main.SeichiAssistBungee;

import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class WarnListener implements Listener {
	private SeichiAssistBungee plugin;
	private Map<String, String> violator;

	public WarnListener(SeichiAssistBungee plugin) {
		this.plugin = plugin;
		refresh();
	}

	@EventHandler
	public void onServerSwitchEvent(final ServerSwitchEvent event) {
		String pname = event.getPlayer().getName().toLowerCase();
		if (violator.containsKey(pname)) {
			plugin.warn.sendWarningOnJoin(pname, violator.get(pname));
		}
	}

	public void refresh() {
		violator = plugin.config.getViolator();
	}
}
