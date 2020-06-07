package com.aaronpb.veteranias.listeners;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.aaronpb.veteranias.ConfigManager;
import com.aaronpb.veteranias.LuckPermsManager;
import com.aaronpb.veteranias.Rank;
import com.aaronpb.veteranias.VaultManager;
import com.aaronpb.veteranias.Veteranias;
import com.aaronpb.veteranias.utils.Utils;

public class VeteraniasInventories implements Listener {

  private LuckPermsManager LPmng = new LuckPermsManager();
  private VaultManager ECONmng = new VaultManager();
  private HashMap<String, Rank> ranksmap = ConfigManager.ranksmap;

  public void openPromoteInv(Player player) {

    String playergroup = LPmng.getPlayerGroup(player);

    if (playergroup == null) {
      player.sendMessage(Utils.userPluginTag() + Utils.chat(
          "&cNo perteneces a un rango valido de veteranias para poder ascender. Consulta este problema con kalhon89!"));
      Utils.sendToServerConsole("warn", player.getName()
          + " tried to ascend without being into a valid group defined in config: "
          + playergroup);
      return;
    }

    if (ConfigManager.ranksmap.get(playergroup).getRanklpgroupascend()
        .equals("END")) {
      player.sendMessage(Utils.userPluginTag() + Utils
          .chat("&aYa has llegado a la cima de las veteranias!! Enhorabuena!"));
      return;
    }

    String playergenre = LPmng.playerGenreNode(player);
    if (playergenre == null) {
      player.sendMessage(Utils.userPluginTag()
          + Utils.chat("&aAntes debes seleccionar un genero!"));
      Utils.sendToServerConsole("warn", player.getName()
          + " tried to ascend without a valid genre! Opening genre inventory selection.");
      openGenreInv(player);
      return;
    }

    String nextgroup = ConfigManager.ranksmap.get(playergroup)
        .getRanklpgroupascend();

    Boolean hasmoney = ECONmng.hasPlayerMoney(player,
        ConfigManager.ranksmap.get(nextgroup).getRankCost());

    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);

    Bukkit.getScheduler().runTask(Veteranias.plugin, new Runnable() {
      @Override
      public void run() {
        promoteInv(player, nextgroup, playergenre, hasmoney);
      }
    });
  }

  public void openGenreInv(Player player) {
    Bukkit.getScheduler().runTask(Veteranias.plugin, new Runnable() {
      @Override
      public void run() {
        genreInv(player);
      }
    });
  }

  public boolean isPromoteInv(Inventory inventory) {
    if (inventory.getSize() != 45) {
      return false;
    }
    if (inventory.getItem(4) == null) {
      return false;
    }
    String   checkblockname     = inventory.getItem(4).getItemMeta()
        .getDisplayName();
    Material checkblockmaterial = inventory.getItem(4).getType();
    if (checkblockname == null || checkblockmaterial == null) {
      return false;
    }
    if (!checkblockname.equals(Utils.chat("&e&l>&6&l>&e&l ACEPTAR &6&l<&e&l<"))
        && !checkblockname
            .equals(Utils.chat("&7&l>&8&l>&7&l ACEPTAR &8&l<&7&l<"))) {
      return false;
    }
    switch (checkblockmaterial) {
      case GOLD_BLOCK:
      case IRON_BLOCK:
        break;
      default:
        return false;
    }
    return true;
  }

  public boolean isGenreInv(Inventory inventory) {
    if (inventory.getSize() != 18) {
      return false;
    }
    if (inventory.getItem(1) == null) {
      return false;
    }
    String   checkblockname     = inventory.getItem(1).getItemMeta()
        .getDisplayName();
    Material checkblockmaterial = inventory.getItem(1).getType();
    if (checkblockname == null || checkblockmaterial == null) {
      return false;
    }
    if (!checkblockname.equals(Utils.chat("&3&l         Soy un chico"))
        || !checkblockmaterial.equals(Material.CYAN_CONCRETE)) {
      return false;
    }
    return true;
  }

  private void promoteInv(Player player, String nextgroup, String genre,
      Boolean hasmoney) {

    if (!ConfigManager.configloaded) {
      return;
    }

    String totitle = ranksmap.get(nextgroup).getRankTitleMale();
    if (genre == "female") {
      totitle = ranksmap.get(nextgroup).getRankTitleFemale();
    }

    String totitlecolored       = ranksmap.get(nextgroup).getRankTitleColor()
        + totitle;
    String totitlecoloredgapped = ConfigManager.prebracket + totitlecolored
        + ConfigManager.postbracket;

    Inventory i = Veteranias.plugin.getServer().createInventory(null, 45,
        Utils.chat("&8\u272A Ascender a &l" + totitle));
    // Item to
    ArrayList<String> lorenewrankinfo = new ArrayList<String>();
    int               rankinfoplace   = 22;
    lorenewrankinfo
        .add(Utils.chat("&e&l&m==&e Nueva etiqueta en chat &e&l&m=="));
    lorenewrankinfo.add(Utils
        .chat(totitlecoloredgapped + player.getDisplayName() + ": &fHey!"));

    // Item Aceptar
    ArrayList<String> loreaccept        = new ArrayList<String>();
    Material          acceptmaterial    = Material.GOLD_BLOCK;
    String            acceptdisplayname = "&e&l>&6&l>&e&l ACEPTAR &6&l<&e&l<";
    if (hasmoney) {
      loreaccept.add(Utils.chat("&a> &6Coste de ascenso: &e"
          + String.valueOf(ranksmap.get(nextgroup).getRankCost())
          + " dracmas."));
      loreaccept.add(Utils.chat("&a\u2714 &2Tienes el dinero necesario!"));
      loreaccept.add(Utils.chat("&7Haz click para ascender"));
    } else {
      loreaccept.add(Utils.chat("&c> &6Coste de ascenso: &e"
          + String.valueOf(ranksmap.get(nextgroup).getRankCost())
          + " dracmas."));
      loreaccept.add(Utils.chat("&c\u2718 &4No tienes suficiente dinero!"));
      acceptmaterial = Material.IRON_BLOCK;
      acceptdisplayname = "&7&l>&8&l>&7&l ACEPTAR &8&l<&7&l<";
    }
    ItemStack acceptascend = createItem(acceptmaterial, 1, acceptdisplayname,
        loreaccept);

    // Item Rechazar
    ArrayList<String> lorecancel = new ArrayList<String>();
    lorecancel.add(Utils.chat("&7Haz click para cancelar y salir"));
    ItemStack cancelascend = createItem(Material.RED_CONCRETE, 1,
        "&c&l>&4&l>&c&l CANCELAR &4&l<&c&l<", lorecancel);

    ItemStack topbackground1   = createItem(Material.LIME_STAINED_GLASS_PANE,
        1);
    ItemStack topbackground2   = createItem(Material.WHITE_STAINED_GLASS_PANE,
        1);
    ItemStack bottombackground = createItem(Material.RED_STAINED_GLASS_PANE, 1);

    // GUI distribution
    // TOP Decoration
    for (int cont = 0; cont <= 2; cont++) {
      i.setItem(cont, topbackground1);
    }
    i.setItem(3, topbackground2);
    i.setItem(5, topbackground2);
    for (int cont = 6; cont <= 8; cont++) {
      i.setItem(cont, topbackground1);
    }

    // Description item info
    ArrayList<ItemStack> itemlist = ranksmap.get(nextgroup).getDescription();
    if (!itemlist.isEmpty()) {
      int pointer = 22;
      if (itemlist.size() < 5) {
        if (itemlist.size() < 3) {
          pointer = 23;
        }
      } else if (itemlist.size() > 8) {
        pointer = 13;
      }

      for (ItemStack item : itemlist) {
        i.setItem(pointer, item);
        pointer++;
        if (pointer == 17 || pointer == 26) {
          pointer = pointer + 5;
        }
      }

      lorenewrankinfo.add(Utils.chat(""));
      lorenewrankinfo
          .add(Utils.chat("&e&l&m==&e Desbloqueo de mejoras &e&l&m=="));
      lorenewrankinfo.add(Utils.chat("&7Consultalas en el lado derecho!"));

      rankinfoplace = 19;
    }

    // Newrank info
    ItemStack newrankinfo = createItem(Material.PAINTING, 1,
        "&f&l¡NUEVO ESTILO!", lorenewrankinfo);
    i.setItem(rankinfoplace, newrankinfo);

    // BOTTOM Decoration
    for (int cont = 36; cont <= 44; cont++) {
      i.setItem(cont, bottombackground);
    }

    // ACCEPT & CANCEL BUTTONS
    i.setItem(4, acceptascend);
    i.setItem(40, cancelascend);
    player.openInventory(i);
  }

  private void genreInv(Player player) {

    if (!ConfigManager.configloaded) {
      return;
    }

    Inventory i = Veteranias.plugin.getServer().createInventory(null, 18,
        Utils.chat("&8 ¡Elige tu &8&lgenero &8en MinExilon!"));

    // Male selection
    ArrayList<String> loremale = new ArrayList<String>();
    loremale.add(Utils.chat("&7&m-----------------------------"));
    loremale.add(Utils.chat("&7Tendras los rangos en masculino."));
    loremale.add(Utils.chat("&7&oAlgunos ejemplos:"));
    loremale.add(Utils.chat("  &9Constructor       &2Conquistador"));
    loremale.add(Utils.chat("   &eEmperador           &dSupremo"));
    ItemStack selectmale = createItem(Material.CYAN_CONCRETE, 1,
        "&3&l         Soy un chico", loremale);

    // Female selection
    ArrayList<String> lorefemale = new ArrayList<String>();
    lorefemale.add(Utils.chat("&7&m----------------------------"));
    lorefemale.add(Utils.chat("&7Tendras los rangos en femenino."));
    lorefemale.add(Utils.chat("&7&oAlgunos ejemplos:"));
    lorefemale.add(Utils.chat(" &9Constructora    &2Conquistadora"));
    lorefemale.add(Utils.chat("   &eEmperatriz          &dSuprema"));
    ItemStack selectfemale = createItem(Material.PURPLE_CONCRETE, 1,
        "&5&l        Soy una chica", lorefemale);

    // No genre selection
    ArrayList<String> lorenogenre = new ArrayList<String>();
    lorenogenre.add(Utils.chat("&7&m----------------------------"));
    lorenogenre.add(Utils.chat("&7Si te es indiferente tendras los"));
    lorenogenre.add(Utils.chat("&7rangos en masculino para una"));
    lorenogenre.add(Utils.chat("&7mejor gestion administrativa."));
    ItemStack selectnogenre = createItem(Material.GRAY_CONCRETE, 1,
        "&f&l     Me es indiferente", lorenogenre);

    ItemStack malebackground    = createItem(Material.CYAN_STAINED_GLASS_PANE,
        1);
    ItemStack femalebackground  = createItem(Material.PURPLE_STAINED_GLASS_PANE,
        1);
    ItemStack nogenrebackground = createItem(Material.GRAY_STAINED_GLASS_PANE,
        1);

    i.setItem(1, selectmale);
    i.setItem(7, selectfemale);
    i.setItem(4, selectnogenre);

    for (int cont = 9; cont <= 11; cont++) {
      i.setItem(cont, malebackground);
    }

    for (int cont = 12; cont <= 14; cont++) {
      i.setItem(cont, nogenrebackground);
    }

    for (int cont = 15; cont <= 17; cont++) {
      i.setItem(cont, femalebackground);
    }

    player.openInventory(i);
  }

  private ItemStack createItem(Material material, int amount,
      String displayname, ArrayList<String> lore) {
    ItemStack itemid   = new ItemStack(material, amount);
    ItemMeta  itemmeta = itemid.getItemMeta();
    itemmeta.setDisplayName(Utils.chat(displayname));
    itemmeta.setLore(lore);
    itemid.setItemMeta(itemmeta);
    return itemid;
  }

  private ItemStack createItem(Material material, int amount) {
    ItemStack itemid   = new ItemStack(material, amount);
    ItemMeta  itemmeta = itemid.getItemMeta();
    itemmeta.setDisplayName(Utils.chat("&0|"));
    itemid.setItemMeta(itemmeta);
    return itemid;
  }
}
