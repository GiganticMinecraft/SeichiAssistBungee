package com.github.unchama.main;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MessageReceiver implements Listener {
	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		if (e.getTag().equalsIgnoreCase("SeichiAssistBungee")) {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
			try {
				switch (in.readUTF()) {
				case "GetLocation":
					ProxiedPlayer p = BungeeCord.getInstance().getPlayer(in.readUTF());
					p.sendMessage(new ComponentBuilder(in.readUTF()).color(ChatColor.YELLOW).create());
					p.sendMessage(new ComponentBuilder(in.readUTF()).color(ChatColor.YELLOW).create());
					break;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}
}
