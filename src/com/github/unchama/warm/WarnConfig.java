package com.github.unchama.warm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.github.unchama.main.SeichiAssistBungee;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class WarnConfig {
	private SeichiAssistBungee plugin;
	private Configuration config;
	private final String CONFIGFILE = "warnconfig.yml";
	private final String ENCODE = "UTF-8";

	// コンストラクタ
	public WarnConfig(SeichiAssistBungee plugin) {
		this.plugin = plugin;
		loadConfig();
	}

	// ymlファイルからの読み込み
	// publicではreload時に呼び出されることを想定
	public void loadConfig() {
		try {
			// configフォルダが未作成の場合作成する
			if (!plugin.getDataFolder().exists()) {
				plugin.getDataFolder().mkdir();
			}
			// ymlを読み込む
			File cfile = new File(plugin.getDataFolder(), CONFIGFILE);
			// ファイルが存在しない場合
			if (!cfile.exists()) {
				// デフォルトのconfig.ymlを配置する
				Files.copy(plugin.getResourceAsStream(CONFIGFILE), cfile.toPath(), new CopyOption[0]);
			}
			// 文字コードの設定のため、InputStreamを経由する
			FileInputStream fis = new FileInputStream(cfile);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName(ENCODE));
			// configファイルを読み込む
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(isr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveConfig() {
		try {
			// 文字コードの設定のため、OutputStreamを経由する
			File cfile = new File(plugin.getDataFolder(), CONFIGFILE);
			FileOutputStream fos = new FileOutputStream(cfile);
			OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName(ENCODE));
			Writer writer = new BufferedWriter(osw);
			// configファイルを保存する
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getCommon() {
		return config.getStringList("common");
	}

	public List<List<String>> getMessage() {
		return config.get("message", new ArrayList<List<String>>());
	}

	public void setViolator(String name, String msgid) {
		Map<String, String> violator = getViolator();
		violator.put(name, msgid);
		// 内部でMap型をConfiguration<String, Object>に変換している
		config.set("violator", violator);
		saveConfig();
	}

	public Map<String, String> getViolator() {
		// Configuration型はvalueの型が不定のMapなのでStringに変換する
		Configuration section = config.getSection("violator");
		// Keyの一覧を取得
		LinkedHashSet<String> keys = (LinkedHashSet<String>) section.getKeys();
		// 違反者リストを取得
		Map<String, String> violator = new HashMap<String, String>();
		for (String key : keys) {
			// valueをgetStringすることでString型に変換している
			violator.put(key, section.getString(key));
		}
		return violator;
	}

	public boolean removeViolator(String name) {
		Map<String, String> violator = getViolator();
		// 削除対象を発見
		if (violator.remove(name) != null) {
			config.set("violator", violator);
			saveConfig();
			return true;
		}
		return false;
	}
}
