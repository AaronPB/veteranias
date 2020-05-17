package com.aaronpb.veteranias;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.aaronpb.veteranias.commands.Commands;
import com.aaronpb.veteranias.events.EventInventories;
import com.aaronpb.veteranias.utils.Utils;

import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;

public class Veteranias extends JavaPlugin {

  private static final Logger log = Logger.getLogger("Minecraft");
  public static Veteranias plugin;
  public static LuckPerms luckPerms;
  public static Economy economy;

  private ConfigManager cfgm = new ConfigManager();
  private Commands commands = new Commands();

  // Initiate plugin
  @Override
  public void onEnable() {
    plugin = this;
    Utils.sendToServerConsole("info", "PLUGIN - &7Checking dependencies...");
    if (!setupLuckPerms()) {
      log.severe(
          String.format("[%s] - Disabled due to no LuckPerms dependency found!",
              getDescription().getName()));
      getServer().getPluginManager().disablePlugin(plugin);
      return;
    }
    Utils.sendToServerConsole("info",
        "PLUGIN - &eHoocked successfully into LuckPerms");
    if (!setupEconomy()) {
      log.severe(
          String.format("[%s] - Disabled due to no Vault dependency found!",
              getDescription().getName()));
      getServer().getPluginManager().disablePlugin(plugin);
      return;
    }
    Utils.sendToServerConsole("info",
        "PLUGIN - &eHoocked successfully into Vault");

    cfgm.setup();

    getCommand(commands.cmd1).setExecutor(commands);
    getCommand(commands.cmd2).setExecutor(commands);

    Utils.sendToServerConsole("info", "PLUGIN - &aLoaded successfully!");

    getServer().getPluginManager().registerEvents(new EventInventories(),
        plugin);
  }

  // Disable plugin
  @Override
  public void onDisable() {
    Utils.sendToServerConsole("info",
        "PLUGIN - &cLas veteranias han sido desactivadas!");
    plugin = null;
  }

  // Setup dependencies
  private boolean setupLuckPerms() {
    if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
      return false;
    }
    RegisteredServiceProvider<LuckPerms> luckpermsProvider = Bukkit
        .getServicesManager().getRegistration(LuckPerms.class);
    if (luckpermsProvider == null) {
      return false;
    }

    luckPerms = luckpermsProvider.getProvider();
    return luckPerms != null;
  }

  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> vaultProvider = getServer()
        .getServicesManager().getRegistration(Economy.class);
    if (vaultProvider == null) {
      return false;
    }
    economy = vaultProvider.getProvider();
    return economy != null;
  }

}
