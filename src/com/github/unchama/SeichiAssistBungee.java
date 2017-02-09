package com.github.unchama;

import com.github.unchama.makejson.MakeJson;
import com.github.unchama.seichiinfo.SeichiinfoCommand;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

public class SeichiAssistBungee extends Plugin {
	public SeichiinfoCommand seichiinfo;
	public MakeJson makejson;

	@Override
	public void onEnable() {
		// インスタンス初期化
		seichiinfo = new SeichiinfoCommand(this);
		makejson = new MakeJson(this);

		// コマンドの登録
		BungeeCord.getInstance().getPluginManager().registerCommand(this, seichiinfo);
		// イベントリスナーの登録
		BungeeCord.getInstance().getPluginManager().registerListener(this, seichiinfo);
		// メッセージ送受信チャンネルの登録
		BungeeCord.getInstance().registerChannel("SeichiAssistBungee");
//		System.out.println("debug");
	}

}
