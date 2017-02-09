package com.github.unchama.seichiinfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.github.unchama.SeichiAssistBungee;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SeichiinfoCommand extends Command implements Listener {
	public SeichiinfoCommand(SeichiAssistBungee plugin) {
		// スーパークラスでコマンドの登録
		super("seichiinfo", "seichiassistbungee.seichiinfo");
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			// プレイヤー名からプレイヤーを検索
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(args[0]);
			// プレイヤーがオンラインなら居場所を取得
			if (p != null) {
				getLocation(p, sender);
			} else {
				sender.sendMessage(new ComponentBuilder(args[0] + ": Offline").color(ChatColor.YELLOW).create());
			}
		} else {
			sendUsage(sender);
		}
	}

	private void sendUsage(CommandSender sender) {
		sender.sendMessage(new ComponentBuilder("[Usage]").color(ChatColor.WHITE).create());
		sender.sendMessage(new ComponentBuilder("/seichiinfo <player>").color(ChatColor.YELLOW).create());
		sender.sendMessage(new ComponentBuilder("<player>のレベルと現在地を取得します。").color(ChatColor.GREEN).create());
	}

	// メッセージ送信イベント
	private static void getLocation(ProxiedPlayer player, CommandSender wanter) {
		// ストリームの準備
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		try {
			// サブチャンネル名はPlaySound
			out.writeUTF("GetLocation");
			// 捜索プレイヤーのBungeeCord内サーバー名
			out.writeUTF(player.getServer().getInfo().getName());
			// 捜索プレイヤーのUUID
			out.writeUTF(player.getUniqueId().toString());
			// 捜索元のプレイヤー名
			out.writeUTF(wanter.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// チャンネルPlaySoundとして送信
		player.getServer().sendData("SeichiAssistBungee", stream.toByteArray());
	}

	// メッセージ受信イベント
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
