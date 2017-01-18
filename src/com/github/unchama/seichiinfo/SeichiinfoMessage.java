package com.github.unchama.seichiinfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SeichiinfoMessage {
	// playerに対しLevel upのsound再生を要求する
	public static void getLocation(ProxiedPlayer player, CommandSender wanter) {
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
}
