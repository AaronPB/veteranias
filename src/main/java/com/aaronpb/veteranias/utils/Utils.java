package com.aaronpb.veteranias.utils;

import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.bukkit.ChatColor;

import com.aaronpb.veteranias.ConfigManager;
import com.aaronpb.veteranias.Veteranias;

public class Utils {

  public static final Logger log = Logger.getLogger("Minecraft");
  public static final Logger promotionslogger = Logger.getLogger("Veteranias");
  private static FileHandler filehandler;
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

  public static void setupLogPromotion() {
    try {
      filehandler = new FileHandler(
          Veteranias.plugin.getDataFolder() + "/veteranias_actions.log", true);
    } catch (SecurityException | IOException e) {
      e.printStackTrace();
    }
    promotionslogger.addHandler(filehandler);
    promotionslogger.setUseParentHandlers(false);
    filehandler.setFormatter(new SimpleFormatter() {
      private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

      @Override
      public synchronized String format(LogRecord lr) {
        return String.format(format, new Date(lr.getMillis()),
            lr.getLevel().getLocalizedName(), lr.getMessage());
      }
    });
  }

}
