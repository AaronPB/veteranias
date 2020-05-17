package com.aaronpb.veteranias.utils;

import java.util.logging.Logger;

import org.bukkit.ChatColor;

import com.aaronpb.veteranias.ConfigManager;
import com.aaronpb.veteranias.Veteranias;

public class Utils {

  public static final Logger log = Logger.getLogger("Minecraft");
  private static String pluginname = Veteranias.plugin.getDescription()
      .getName();

  public static String chat(String s) {
    return ChatColor.translateAlternateColorCodes('&', s);
  }

  public static String userPluginTag() {
    return chat(Veteranias.plugin.getConfig().getString("HeaderTag"));
  }

  public static void sendToServerConsole(String level, String msg) {
    switch (level) {
      case "debug":
        if (ConfigManager.debugmode) {
          log.info(String.format("[%s][DEBUG] - %s", pluginname, chat(msg)));
        }
        break;
      case "info":
        log.info(String.format("[%s] - %s", pluginname, chat(msg)));
        break;
      case "warn":
        log.warning(String.format("[%s] - %s", pluginname, chat(msg)));
        break;
      case "error":
        log.severe(String.format("[%s] - %s", pluginname, chat(msg)));
        break;
      default:
        log.info(String.format("[%s] - %s", pluginname, chat(msg)));
        break;
    }
  }

}
