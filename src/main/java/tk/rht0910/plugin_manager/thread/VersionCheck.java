package tk.rht0910.plugin_manager.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import tk.rht0910.plugin_manager.PluginManager;
import tk.rht0910.plugin_manager.language.Lang;
import tk.rht0910.plugin_manager.util.StringTool;
import tk.rht0910.tomeito_core.utils.Log;

public class VersionCheck extends Thread implements Runnable {
	private static Boolean player;
	private static CommandSender sender;
	private static URL formalurl;
	private static String edition;
	private static final char altColorChar = '&';
	public VersionCheck(Boolean byPlayer, CommandSender sender, String url) {
		VersionCheck.player = byPlayer;
		VersionCheck.sender = sender;
		VersionCheck.edition = "";
		try {
		VersionCheck.formalurl = new URL(url);
		} catch(MalformedURLException e) {
			Log.error("URL are malformed! This is development problem. Please report the ticket");
			e.printStackTrace();
			Log.error("Caused by:");
			e.getCause().printStackTrace();
		}
	}

	public VersionCheck(Boolean byPlayer, CommandSender sender, String url, String edition) {
		VersionCheck.player = byPlayer;
		VersionCheck.sender = sender;
		VersionCheck.edition = edition;
		try {
		VersionCheck.formalurl = new URL(url);
		} catch(MalformedURLException e) {
			Log.error("URL are malformed! This is development problem. Please report the ticket");
			e.printStackTrace();
			Log.error("Caused by:");
			e.getCause().printStackTrace();
		}
	}

	public VersionCheck() {
		throw new IllegalArgumentException("Not enough args[Boolean byplayer, CommandSender sender, String url(, String edition)]");
	}

	public void run() {
		String response = null;
		String line = null;
		URL url = null;
		Log.info(ChatColor.translateAlternateColorCodes(altColorChar, Lang.started_version_check) + ChatColor.RED + edition);
		if(player) {sender.sendMessage(ChatColor.translateAlternateColorCodes(altColorChar, Lang.started_version_check) + ChatColor.RED + edition);}
		url = formalurl;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36 Edge/15.16232");
			Log.info("Connection opened to " + formalurl.toString());
		} catch (IOException e2) {
			e2.printStackTrace();
			Log.error("failed to open connection.");
		}
		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			while ((response = rd.readLine()) != null) {
			    line += response;
			}
			line = line.replaceAll("null", "");
			Log.info("Version Checker: Status code: " + conn.getResponseCode() + ", Get: " + line);
		} catch (IOException e) {
			e.printStackTrace();
			Log.error(String.format(Lang.error_occured, "Failed to open connection or invalid response code: " + e));
			sender.sendMessage(String.format(Lang.error_occured, e));
		}
		try {
			if(StringTool.toVersion(Lang.version).compareTo(StringTool.toVersion(line)) == -1) {
				Log.info("New version available: " + line);
				PluginManager.is_available_new_version = true;
				PluginManager.newv = line;
				if(player) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes(altColorChar, Lang.version_check_complete_update1));
					sender.sendMessage(ChatColor.translateAlternateColorCodes(altColorChar, String.format(Lang.version_check_complete_update2, PluginManager.current)));
					sender.sendMessage(ChatColor.translateAlternateColorCodes(altColorChar, String.format(Lang.version_check_complete_update3, line + ChatColor.RED + edition)));
					if(edition.equals("(dev)")) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes(altColorChar, Lang.version_check_complete_update5));
					} else {
						sender.sendMessage(ChatColor.translateAlternateColorCodes(altColorChar, Lang.version_check_complete_update4));
					}
				}
				return;
			} else if(StringTool.toVersion(Lang.version).compareTo(StringTool.toVersion(line)) == 1) {
				Log.info("Oh you're a time traveller!");
				if(player) {
					sender.sendMessage("Hello Time Traveller!");
				}
			} else {
				Log.info("No updates found.");
				if(player) {sender.sendMessage(ChatColor.translateAlternateColorCodes(altColorChar, Lang.version_check_complete_update_no));}
				return;
			}
		} catch(IllegalArgumentException iae) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes(altColorChar, String.format(Lang.continue_error_catch, iae)));
			Log.error(ChatColor.translateAlternateColorCodes(altColorChar, Lang.error_occured));
			iae.printStackTrace();
			iae.getCause().printStackTrace();
		}
	}
}
