package com.aaronpb.veteranias;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.aaronpb.veteranias.utils.Utils;

public class ConfigManager {

  // File configs
  public FileConfiguration config;
  public File configfile;

  // Public config params
  public static boolean configloaded;
  public static boolean debugmode;
  public static boolean levitation, cooldown;
  public static int levitation_time, cooldown_time;
  public static String prebracket, postbracket;
  public static HashMap<String, Rank> ranksmap = new HashMap<String, Rank>();
  public static HashMap<String, Rank> preloadranksmap = new HashMap<String, Rank>();

  public void setup() {
    configloaded = false;
    if (!Veteranias.plugin.getDataFolder().exists()) {
      Veteranias.plugin.getDataFolder().mkdir();
    }

    configfile = new File(Veteranias.plugin.getDataFolder(), "config.yml");

    if (!configfile.exists()) {
      Veteranias.plugin.getConfig().options().copyDefaults(true);
      Veteranias.plugin.saveDefaultConfig();
      Utils.sendToServerConsole("info",
          "CONFIG - &aSe ha generado el archivo config.yml correctamente.");
    } else {
      Utils.sendToServerConsole("info",
          "CONFIG - &aNo ha sido necesario generarlo, ya existe.");
    }
    try {
      loadConfigParams();
      ranksmap = preloadranksmap;
      configloaded = true;
    } catch (NullPointerException e) {
      Utils.sendToServerConsole("error",
          "Could not read all the config params correctly. Correct mistakes in file and reload again!!");
    }
  }

  public void reload() {
    configloaded = false;
    config = Veteranias.plugin.getConfig();
    configfile = new File(Veteranias.plugin.getDataFolder(), "config.yml");
    if (!configfile.exists()) {
      config.options().copyDefaults(true);
      Veteranias.plugin.saveDefaultConfig();
      Utils.sendToServerConsole("warn",
          "CONFIG - No se ha detectado archivo config.yml en la carpeta veteranias. Se ha generado el archivo por defecto!");
    }
    try {
      loadConfigParams();
      Utils.sendToServerConsole("info",
          "CONFIG - &aSe ha guardado la configuracion correctamente!");
      ranksmap = preloadranksmap;
      Utils.sendToServerConsole("debug",
          "CONFIG - Nuevo hasmap cargado: " + ranksmap);
      configloaded = true;
    } catch (NullPointerException e) {
      Utils.sendToServerConsole("error",
          "Could not read all the config params correctly. Correct mistakes in file and reload again!!");
    }
  }

  public void loadConfigParams() throws NullPointerException {

    config = YamlConfiguration.loadConfiguration(configfile);

    preloadranksmap.clear();

    loadGeneralParams();

    for (String rank : config.getConfigurationSection("ascensionlist")
        .getKeys(false)) {

      Rank setRank = new Rank();
      setRank.setRanklpgroup(
          config.getString("ascensionlist." + rank + ".lpgroup"));
      setRank.setRanklpgroupascend(
          config.getString("ascensionlist." + rank + ".lpgroupascend"));

      String   datatitlemale    = config
          .getString("ascensionlist." + rank + ".title_male");
      String[] titlemale        = datatitlemale.split(":");
      String   datattitlefemale = config
          .getString("ascensionlist." + rank + ".title_female");
      String[] titlefemale      = datattitlefemale.split(":");

      setRank.setRankTitleMale(titlemale[1]);
      setRank.setRankTitleFemale(titlefemale[1]);
      setRank.setRankTitleColor(titlemale[0]);
      setRank.setRankCost(config.getInt("ascensionlist." + rank + ".cost"));

      if (config.isSet("ascensionlist." + rank + ".description")) {

        int lim = 0;

        for (String itemdesc : config
            .getConfigurationSection("ascensionlist." + rank + ".description")
            .getKeys(false)) {
          Utils.sendToServerConsole("debug",
              " Checking " + itemdesc + " of " + rank + " - Lim: " + lim);
          if (lim <= 12 && loadDescriptions(itemdesc, rank) != null) {
            ItemStack infoitem = loadDescriptions(itemdesc, rank);
            Utils.sendToServerConsole("debug",
                setRank.getRanklpgroup() + " Added info item " + infoitem);
            setRank.addDescription(infoitem);
            lim++;

          } else if (lim > 12) {
            Utils.sendToServerConsole("warn",
                rank + "Ignoring " + itemdesc + " due to the item-limit!");
          }
        }

      }
      
      if (config.isSet("ascensionlist." + rank + ".commands")) {
        Utils.sendToServerConsole("debug",
            " Checking command list of " + rank);
        for (String command : config
            .getStringList("ascensionlist." + rank + ".commands")) {
          setRank.addCommands(command);
          Utils.sendToServerConsole("debug",
              setRank.getRanklpgroup() + " Added command " + command);
        }
      }

      preloadranksmap.put(setRank.getRanklpgroup(), setRank);
      Utils.sendToServerConsole("info", "&a[" + rank + "] correctly loaded.");
    }
    Utils.sendToServerConsole("debug", "CONFIG - Hashmap: " + preloadranksmap);
  }

  private void loadGeneralParams() {
    Utils.sendToServerConsole("info", "CONFIG - Loading config params...");
    if (config.isSet("DebugMode")) {
      debugmode = config.getBoolean("DebugMode");
      Utils.sendToServerConsole("info",
          "CONFIG - &a[DebugMode] set to " + debugmode);
    } else {
      levitation = false;
      Utils.sendToServerConsole("warn",
          "CONFIG - [DebugMode] Not found in config. Setting to false");
    }
    if (config.isSet("levitation_effect")) {
      levitation = config.getBoolean("levitation_effect");
      Utils.sendToServerConsole("info",
          "CONFIG - &a[levitation_effect] set to " + levitation);
    } else {
      levitation = false;
      Utils.sendToServerConsole("warn",
          "CONFIG - [levitation_effect] Not found in config. Setting to false");
    }
    if (config.isSet("levitation_duration")) {
      levitation_time = config.getInt("levitation_duration");
      Utils.sendToServerConsole("info",
          "CONFIG - &a[levitation_duration] set to " + levitation_time);
    } else {
      levitation_time = 0;
      Utils.sendToServerConsole("warn",
          "CONFIG - [levitation_duration] Not found in config. Setting to 0 seconds");
    }
    if (config.isSet("setcooldown")) {
      cooldown = config.getBoolean("setcooldown");
      Utils.sendToServerConsole("info",
          "CONFIG - &a[cooldown] set to " + cooldown);
    } else {
      cooldown = true;
      Utils.sendToServerConsole("warn",
          "CONFIG - [cooldown] Not found in config. Setting to true");
    }
    if (config.isSet("cooldowntime")) {
      cooldown_time = config.getInt("cooldowntime");
      Utils.sendToServerConsole("info",
          "CONFIG - &a[cooldown_duration] set to " + cooldown_time);
    } else {
      cooldown_time = 30;
      Utils.sendToServerConsole("warn",
          "CONFIG - [cooldown_duration] Not found in config. Setting to 30 seconds");
    }
    if (config.isSet("titleformatpre") && config.isSet("titleformatpost")) {
      prebracket = config.getString("titleformatpre");
      postbracket = config.getString("titleformatpost");
      Utils.sendToServerConsole("info",
          "CONFIG - &a[rank brackets] correctly set!");
    } else {
      prebracket = "&7[";
      postbracket = "&7]&r";
      Utils.sendToServerConsole("warn",
          "CONFIG - [rank brackets] Not found in config. Setting to default []");
    }
  }

  private ItemStack loadDescriptions(String itemdesc, String rank) {
    String itemdescpath = "ascensionlist." + rank + ".description." + itemdesc;
    String material     = config.getString(itemdescpath + ".type");
    int    amount       = config.getInt(itemdescpath + ".amount");

    if (material != null && amount > 0 && amount <= 64) {

      Material materialid = Material.matchMaterial(material);
      if (materialid == null) {
        Utils.sendToServerConsole("warn",
            rank + " - Invalid material " + material + ". Ignoring item...");
        return null;
      }
      ItemStack item     = new ItemStack(materialid, amount);
      ItemMeta  itemmeta = item.getItemMeta();
      itemmeta.setDisplayName(
          Utils.chat(config.getString(itemdescpath + ".title")));

      if (!config.getStringList(itemdescpath + ".lore").isEmpty()) {
        ArrayList<String> lore = new ArrayList<String>();
        for (String line : config.getStringList(itemdescpath + ".lore")) {
          lore.add(Utils.chat(line));
        }
        itemmeta.setLore(lore);
      }
      item.setItemMeta(itemmeta);
      return item;
    } else {
      Utils.sendToServerConsole("warn",
          rank + " - Invalid amount or material of item " + itemdesc
              + ". Ignoring item...");
      return null;
    }
  }

}
