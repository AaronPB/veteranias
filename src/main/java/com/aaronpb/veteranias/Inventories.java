package com.aaronpb.veteranias;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.aaronpb.veteranias.utils.Utils;

public class Inventories implements Listener {

  private HashMap<String, Rank> ranksmap = ConfigManager.ranksmap;

  public void openInvAscenso(Player player, String currentgroup, String genre,
      Boolean hasmoney) {

    if (!ConfigManager.configloaded) {
      return;
    }

    String nextgroup = ranksmap.get(currentgroup).getRanklpgroupascend();

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
          + String.valueOf(ranksmap.get(currentgroup).getRankCost())
          + " dracmas."));
      loreaccept.add(Utils.chat("&a\u2714 &2Tienes el dinero necesario!"));
      loreaccept.add(Utils.chat("&7Haz click para ascender"));
    } else {
      loreaccept.add(Utils.chat("&c> &6Coste de ascenso: &e"
          + String.valueOf(ranksmap.get(currentgroup).getRankCost())
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

    ItemStack topbackground1   = createBackgroundItem(
        Material.LIME_STAINED_GLASS_PANE, 1);
    ItemStack topbackground2   = createBackgroundItem(
        Material.WHITE_STAINED_GLASS_PANE, 1);
    ItemStack bottombackground = createBackgroundItem(
        Material.RED_STAINED_GLASS_PANE, 1);

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
    ArrayList<ItemStack> itemlist = ranksmap.get(currentgroup).getDescription();
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

  public void openInvGenre(Player player) {
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

    ItemStack malebackground    = createBackgroundItem(
        Material.CYAN_STAINED_GLASS_PANE, 1);
    ItemStack femalebackground  = createBackgroundItem(
        Material.PURPLE_STAINED_GLASS_PANE, 1);
    ItemStack nogenrebackground = createBackgroundItem(
        Material.GRAY_STAINED_GLASS_PANE, 1);

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

  public ItemStack createItem(Material material, int amount, String displayname,
      ArrayList<String> lore) {
    ItemStack itemid   = new ItemStack(material, amount);
    ItemMeta  itemmeta = itemid.getItemMeta();
    itemmeta.setDisplayName(Utils.chat(displayname));
    itemmeta.setLore(lore);
    itemid.setItemMeta(itemmeta);
    return itemid;
  }

  public ItemStack createBackgroundItem(Material material, int amount) {
    ItemStack itemid   = new ItemStack(material, amount);
    ItemMeta  itemmeta = itemid.getItemMeta();
    itemmeta.setDisplayName("");
    itemid.setItemMeta(itemmeta);
    return itemid;
  }
}
