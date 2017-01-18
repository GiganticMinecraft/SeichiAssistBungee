package com.github.unchama.warm;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.md_5.bungee.api.connection.ProxiedPlayer;

// Spigot pluginへメッセージを送信するクラス
public class WarnMessage {
	// playerに対しLevel upのsound再生を要求する
	public static void playSound(ProxiedPlayer player) {
		// ストリームの準備
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		try {
			// サブチャンネル名はPlaySound
			out.writeUTF("PlaySound");
			// メッセージ内容はプレイヤーのUUID
			out.writeUTF(player.getUniqueId().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// チャンネルPlaySoundとして送信
		player.getServer().sendData("SeichiAssistBungee", stream.toByteArray());
	}
}
