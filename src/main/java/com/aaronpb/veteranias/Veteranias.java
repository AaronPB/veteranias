package com.aaronpb.veteranias;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.aaronpb.veteranias.commands.AdminCommands;
import com.aaronpb.veteranias.commands.PromoteCommands;
import com.aaronpb.veteranias.events.EventInventories;
import com.aaronpb.veteranias.listeners.VeteraniasInventories;
import com.aaronpb.veteranias.utils.Utils;

import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;

public class Veteranias extends JavaPlugin {

  public static Veteranias plugin;
  public static LuckPerms luckPerms;
  public static Economy economy;

  private ConfigManager cfgm = new ConfigManager();
  private VeteraniasInventories vetinvs = new VeteraniasInventories();
  private AdminCommands admincommands = new AdminCommands();
  private PromoteCommands promotecommands = new PromoteCommands();

  // Initiate plugin
  @Override
  public void onEnable() {
    plugin = this;
    Utils.sendToServerConsole("info", "Checking Veteranias's dependencies...");
    if (!setupLuckPerms()) {
      Utils.sendToServerConsole("error",
          "Disabled due to no LuckPerms dependency found!");
      getServer().getPluginManager().disablePlugin(plugin);
      return;
    }
    Utils.sendToServerConsole("info", "Hoocked successfully into LuckPerms");
    if (!setupEconomy()) {
      Utils.sendToServerConsole("error",
          "Disabled due to no Vault dependency found!");
      getServer().getPluginManager().disablePlugin(plugin);
      return;
    }
    Utils.sendToServerConsole("info", "Hoocked successfully into Vault");

    cfgm.setup();

    getCommand(admincommands.adminreload_cmd).setExecutor(admincommands);
//    getCommand(admincommands.adminchangegenre_cmd).setExecutor(admincommands);
    getCommand(promotecommands.promotecmd).setExecutor(promotecommands);

    Utils.sendToServerConsole("info", "Loaded successfully!");

    getServer().getPluginManager().registerEvents(new EventInventories(),
        plugin);
  }

  // Disable plugin
  @Override
  public void onDisable() {
    ConfigManager.configloaded = false;
    closePlayerInvs();
    Utils.sendToServerConsole("info",
        "The plugin Veteranias has been correctly disabled!");
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

  private void closePlayerInvs() {
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (p.getOpenInventory() != null) {
        if (p.getOpenInventory().getTopInventory() != null) {
          Inventory pinv = p.getOpenInventory().getTopInventory();
          if (vetinvs.isPromoteInv(pinv) || vetinvs.isGenreInv(pinv)) {
            Utils.sendToServerConsole("info",
                "Inventory closer: Closing inventory for " + p.getName());
            p.closeInventory();
          }
        }
      }
    }
  }

}
