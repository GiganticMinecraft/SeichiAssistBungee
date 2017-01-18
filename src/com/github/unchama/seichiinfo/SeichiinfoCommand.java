package com.github.unchama.seichiinfo;

import com.github.unchama.main.SeichiAssistBungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SeichiinfoCommand extends Command {
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
				SeichiinfoMessage.getLocation(p, sender);
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
}
