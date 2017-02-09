package com.github.unchama.makejson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;

import com.github.unchama.SeichiAssistBungee;

// 出力対象
// ・BungeeCord全体での最大人数(bungeecord.player_limit)
// ・BungeeCord全体でのオンライン人数
// ・記録日時
// ・各サーバーのオンラインプレイヤー数
public class MakeJson {
	// ログ件数
	private static final int LOG_COUNT = 20;
	// 計測周期（秒）
	private static final int CYCLIC = 30;
	// 対象外ワールド
	private static final List<String> ignore = Arrays.asList("deb");

	public MakeJson(SeichiAssistBungee plugin) {
		BungeeCord.getInstance().getScheduler().schedule(plugin, new Runnable() {
			public void run() {
				// 各サーバーの情報を取得
				Map<String, ServerInfo> servers = BungeeCord.getInstance().getServers();
				List<String> svinfo = new ArrayList<String>();
				for (Iterator<Map.Entry<String, ServerInfo>> iterator = servers.entrySet().iterator(); iterator.hasNext();) {
					Map.Entry<String, ServerInfo> entry = iterator.next();
					// 除外ワールド判定
					if (ignore.contains(entry.getKey())) {
						continue;
					}
					// サーバー名とオンラインプレイヤー数を取得
					svinfo.add("\"" + entry.getKey() + "\":" + entry.getValue().getPlayers().size());
				}
				// 最終整形
				String jdata = "{\"response\":\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " +0900\","
						+ "\"max\":" + BungeeCord.getInstance().getConfig().getPlayerLimit()
						+ ",\"online\":" + BungeeCord.getInstance().getOnlineCount();
				for (String info : svinfo) {
					jdata += "," + info;
				}
				jdata += "}";

				try {
					// ディレクトリとファイルの初期化
					File file = new File("plugins/SeichiAssistBungee");
					if (!file.exists()) {
						file.mkdirs();
					}
					file = new File("plugins/SeichiAssistBungee/ServerStatus.json");
					if (!file.exists()) {
						file.createNewFile();
					}
					// 読み込み
					FileReader fr = new FileReader(file);
					BufferedReader br = new BufferedReader(fr);
					List<String> log = new ArrayList<String>();
					String line = br.readLine();
					while (line != null) {
						log.add(line);
						line = br.readLine();
					}
					br.close();
					// ログデータの整形
					while (log.size() >= LOG_COUNT) {
						log.remove(0);
					}
					log.add(jdata);
					// 書き込み
					FileWriter filewriter = new FileWriter(file);
					for (String buffer : log) {
						filewriter.write(buffer + "\r\n");
					}
					filewriter.close();
					// ログ
					// System.out.println("[SeichiAssistBungee] MakeJson: server
					// status wrote.");
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		}, 0, CYCLIC, TimeUnit.SECONDS);
	}
}
