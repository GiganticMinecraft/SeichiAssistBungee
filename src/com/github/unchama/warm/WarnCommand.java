package com.github.unchama.warm;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.unchama.main.SeichiAssistBungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class WarnCommand extends Command {
	private SeichiAssistBungee plugin;

	public WarnCommand(SeichiAssistBungee plugin) {
		// スーパークラスでコマンドの登録
		super("warn", "seichiassistbungee.warn");
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 3 && args[0].equals("send")) {
			try {
				if (!sendWarning(sender, args[1], args[2])) {
					// プレイヤーがオフライン（プレイヤー名の誤入力含む）の時は次回ログイン時に警告
					sender.sendMessage(new ComponentBuilder("PlayerがOfflineです。").create());
					sender.sendMessage(new ComponentBuilder("次回ログイン時に警告を送信します。").create());
					sender.sendMessage(new ComponentBuilder("名前を間違えた場合はremしてください。").create());
					// 違反者リストに登録する
					plugin.config.setViolator(args[1].toLowerCase(), args[2]);
					// イベントリスナ―の違反者リストを更新しておく
					plugin.listener.refresh();
				}
			} catch (IllegalArgumentException e) {
				// 引数異常通知済みにつき何もしない
			}
		} else if (args.length == 2 && args[0].equals("rem")) {
			// 違反者リストからの削除を試みる
			if (plugin.config.removeViolator(args[1].toLowerCase())) {
				sender.sendMessage(new ComponentBuilder("違反者リストから削除しました: " + args[1]).create());
			} else {
				sender.sendMessage(new ComponentBuilder("違反者リストに存在しません: " + args[1]).create());
			}
		} else if (args.length == 1 && args[0].equals("list")) {
			// 違反者リストを表示する
			Map<String, String> violator = plugin.config.getViolator();
			sender.sendMessage(new ComponentBuilder("登録済み違反者 " + Integer.toString(violator.size()) + "名").create());
			// 全員分繰り返し
			int cnt = 0;
			for (Entry<String, String> vio : violator.entrySet()) {
				cnt++;
				sender.sendMessage(new ComponentBuilder("(" + String.format("%03d", cnt) + ") " + vio.getKey() + " : msgid[" + vio.getValue() + "]").create());
			}
		} else {
			sendUsage(sender);
		}
	}

	public void sendWarningOnJoin(String name, String msgid) {
		CommandSender console = ProxyServer.getInstance().getConsole();
		try {
			if (!sendWarning(console, name, msgid)) {
				// Joinしたはずのplayerが見つからない→想定外の異常
				console.sendMessage(new ComponentBuilder("Joinしたはずのplayerが見つかりませんでした： " + name).color(ChatColor.RED).create());
			}
			else {
			}
		} catch (IllegalArgumentException e) {
			// コマンド発行時に問題なかったmsgidが異常→config変更による異常
			console.sendMessage(new ComponentBuilder("configの変更によりmsgidが変更されました：  " + msgid).color(ChatColor.RED).create());
		}
		// 警告完了または想定外の異常のため、違反者リストから削除する
		plugin.config.removeViolator(name);
		console.sendMessage(new ComponentBuilder("違反者リストから削除しました: " + name).create());
		// イベントリスナ―の違反者リストを更新しておく
		plugin.listener.refresh();
	}

	private boolean sendWarning(CommandSender sender, String name, String msgid) throws IllegalArgumentException {
		// メッセージID
		int intid;
		try {
			// メッセージIDを取得
			intid = Integer.parseUnsignedInt(msgid);
		} catch (NumberFormatException e) {
			sender.sendMessage(new ComponentBuilder("msgidは正の整数値を入力してください。").color(ChatColor.RED).create());
			sendUsage(sender);
			throw new IllegalArgumentException();
		}

		// 手動更新を考慮し、コンフィグを再読み込み
		plugin.config.loadConfig();
		// メッセージ一覧の取得
		// ID別メッセージ
		List<List<String>> msg = plugin.config.getMessage();
		// 共通メッセージ
		List<String> cmn = plugin.config.getCommon();

		// メッセージID範囲チェック
		if (intid >= msg.size()) {
			sender.sendMessage(new ComponentBuilder("登録されているmsgidは " + Integer.toString(msg.size() - 1) + " までです。").color(ChatColor.RED).create());
			throw new IllegalArgumentException();
		}

		// プレイヤー名からプレイヤーを検索
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(name);
		if (p != null) {
			// Spigot側で警告音を鳴らす
			WarnMessage.playSound(p);

			// 対象メッセージを取得
			List<String> message = msg.get(intid);
			// 発行者に発行完了を通知しつつ、プレイヤーへ警告を発行する
			sender.sendMessage(new ComponentBuilder("下記警告を発行しました。").color(ChatColor.GREEN).create());
			for (int cnt = 0; cnt < message.size(); cnt++) {
				p.sendMessage(new ComponentBuilder(message.get(cnt)).color(ChatColor.RED).bold(true).create());
				sender.sendMessage(new ComponentBuilder(message.get(cnt)).color(ChatColor.RED).bold(true).create());
			}

			// 共通メッセージが登録されていれば共通メッセージを発行
			if (cmn.size() != 1 && !cmn.get(0).equals("")) {
				// 発行者に発行完了を通知しつつ、プレイヤーへ警告を発行する
				for (int cnt = 0; cnt < cmn.size(); cnt++) {
					p.sendMessage(new ComponentBuilder(cmn.get(cnt)).color(ChatColor.RED).bold(true).create());
					sender.sendMessage(new ComponentBuilder(cmn.get(cnt)).color(ChatColor.RED).bold(true).create());
				}
			}
		} else {
			return false;
		}
		return true;
	}

	private void sendUsage(CommandSender sender) {
		sender.sendMessage(new ComponentBuilder("[Usage]").color(ChatColor.WHITE).create());
		sender.sendMessage(new ComponentBuilder("/warn send <player> <msgid>").color(ChatColor.YELLOW).create());
		sender.sendMessage(new ComponentBuilder("<player>に<msgid>の警告を発行します。").color(ChatColor.GREEN).create());
		sender.sendMessage(new ComponentBuilder("<player>がofflineの場合、違反者リストへ登録し次回ログイン時に警告します。").color(ChatColor.GREEN).create());
		sender.sendMessage(new ComponentBuilder("/warn rem <player>").color(ChatColor.YELLOW).create());
		sender.sendMessage(new ComponentBuilder("<player>を違反者リストから削除します。").color(ChatColor.GREEN).create());
		sender.sendMessage(new ComponentBuilder("/warn list").color(ChatColor.YELLOW).create());
		sender.sendMessage(new ComponentBuilder("違反者リストを表示します。").color(ChatColor.GREEN).create());
	}
}
