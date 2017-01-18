package com.github.unchama.main;

import com.github.unchama.seichiinfo.SeichiinfoCommand;
import com.github.unchama.warm.WarnListener;
import com.github.unchama.warm.WarnCommand;
import com.github.unchama.warm.WarnConfig;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class SeichiAssistBungee extends Plugin {
	public WarnConfig config;
	public WarnCommand warn;
	public WarnListener listener;

	@Override
	public void onEnable() {
		// config.ymlの読み込み
		config = new WarnConfig(this);
		// コマンドの登録
		ProxyServer.getInstance().getPluginManager().registerCommand(this, warn = new WarnCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new SeichiinfoCommand(this));
		// イベントリスナーの登録
		getProxy().getPluginManager().registerListener(this, listener = new WarnListener(this));
		BungeeCord.getInstance().getPluginManager().registerListener(this, new MessageReceiver());
		BungeeCord.getInstance().registerChannel("SeichiAssistBungee");
//		System.out.println("debug");
	}

}
