package com.aaronpb.veteranias.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.aaronpb.veteranias.ConfigManager;
import com.aaronpb.veteranias.LuckPermsManager;
import com.aaronpb.veteranias.VaultManager;
import com.aaronpb.veteranias.Veteranias;
import com.aaronpb.veteranias.utils.Utils;

public class EventInventories implements Listener {

  private LuckPermsManager LPmng = new LuckPermsManager();
  private VaultManager ECONmng = new VaultManager();

  private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();

  @EventHandler
  public void InventoryClick(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();

    Inventory open = event.getClickedInventory();
    ItemStack item = event.getCurrentItem();

    Bukkit.getScheduler().isCurrentlyRunning(0);

    if (open == null) {
      return;
    }
    if (open.getSize() == 45 && (open.getItem(4).getItemMeta().getDisplayName()
        .equals(Utils.chat("&e&l>&6&l>&e&l ACEPTAR &6&l<&e&l<"))
        || open.getItem(4).getItemMeta().getDisplayName()
            .equals(Utils.chat("&7&l>&8&l>&7&l ACEPTAR &8&l<&7&l<")))) {
      event.setCancelled(true);

      if (!ConfigManager.configloaded) {
        player.closeInventory();
        player.sendMessage(Utils.userPluginTag() + Utils.chat(
            "&cNo se ha podido procesar la solicitud de ascenso debido a un mantenimiento de las veteranias. Intentalo mas tarde!"));
        return;
      }

      if (item == null || !item.hasItemMeta()) {
        return;
      }

      if (item.getType().equals(Material.GOLD_BLOCK)) {

        player.closeInventory();

        Bukkit.getScheduler().runTaskAsynchronously(Veteranias.plugin,
            new Runnable() {
              @Override
              public void run() {
                String playergroup = LPmng.getPlayerGroup(player);

                String nextgroup = ConfigManager.ranksmap.get(playergroup)
                    .getRanklpgroupascend();

                if (ECONmng.hasPlayerMoney(player,
                    ConfigManager.ranksmap.get(nextgroup).getRankCost())) {

                  if (cooldown.containsKey(player.getUniqueId())) {
                    Utils.sendToServerConsole("debug",
                        "COOLDOWN_SYS ON at player: " + player.getName());
                    long secondsleft = ((cooldown.get(player.getUniqueId())
                        / 1000 + ConfigManager.cooldown_time)
                        - (System.currentTimeMillis() / 1000));

                    if (secondsleft > 0) {
                      player.sendMessage(Utils.userPluginTag()
                          + Utils.chat("&cDebes esperar " + secondsleft
                              + " segundos para poder volver a ascender!"));
                      return;
                    } else {
                      cooldown.remove(player.getUniqueId());
                    }
                  }

                  // Upgrade process and effects
                  boolean moneytaken = ECONmng.takePlayerMoney(player,
                      ConfigManager.ranksmap.get(nextgroup).getRankCost());
                  boolean playerpromoted = LPmng.promotePlayer(player);
                  if (!moneytaken) {
                    player.sendMessage(Utils.userPluginTag() + Utils.chat(
                        "&cNo tienes suficientes dracmas para ascender!!"));
                  }
                  if (!playerpromoted) {
                    player.sendMessage(Utils.userPluginTag() + Utils.chat(
                        "&cNo hay una veterania valida para ascender!! Consulta este problema con kalhon89."));
                    return;
                  }
                  player.playSound(player.getLocation(),
                      Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                  if (ConfigManager.levitation) {
                    Utils.sendToServerConsole("debug",
                        "ASCENDACTION - Applying levitation effect...");
                    Bukkit.getScheduler().runTask(Veteranias.plugin,
                        new Runnable() {
                          @Override
                          public void run() {
                            player.addPotionEffect(
                                new PotionEffect(PotionEffectType.LEVITATION,
                                    ConfigManager.levitation_time * 20, 1));
                          }
                        });
                  }

                  String promotetitle = ConfigManager.ranksmap.get(nextgroup)
                      .getRankTitleMale();
                  String article  = "un";
                  if (LPmng.playerGenreNode(player) == "female") {
                    promotetitle = ConfigManager.ranksmap.get(nextgroup)
                        .getRankTitleFemale();
                    article = "una";
                  }

                  String thickpromotetile = ConfigManager.ranksmap
                      .get(nextgroup).getRankTitleColor() + "&l" + promotetitle;

                  promotetitle = ConfigManager.ranksmap.get(nextgroup)
                      .getRankTitleColor() + promotetitle;

                  player.sendTitle(Utils.chat("&aAscendiste a " + promotetitle),
                      Utils
                          .chat("&fSe han aplicado las mejoras de tu ascenso!"),
                      10, 80, 50);
                  Bukkit.broadcastMessage(Utils.userPluginTag()
                      + Utils.chat("&6&l" + player.getName()
                          + "&3 ha ascendido!! &6GG\n&a&l>>&f Ahora es "
                          + article + " " + thickpromotetile));
                  cooldown.put(player.getUniqueId(),
                      System.currentTimeMillis());

                  // Run pending commands synchronously
                  if (!ConfigManager.ranksmap.get(nextgroup).getCommands()
                      .isEmpty()) {
                    ArrayList<String> commandslist = ConfigManager.ranksmap
                        .get(nextgroup).getCommands();
                    ArrayList<String> commands = new ArrayList<String>();
                    for (String command : commandslist) {
                      String[] splitted = command.split("%");
                      String assembly = "";
                      for (String part : splitted) {
                        if (part.equals("player")) {
                          part = player.getName();
                        }
                        assembly = assembly + part;
                      }
                      Utils.sendToServerConsole("debug",
                          "Loaded command: " + assembly);
                      commands.add(assembly);
                    }
                    Bukkit.getScheduler().runTask(Veteranias.plugin,
                        new Runnable() {
                          @Override
                          public void run() {
                            for (String command : commands) {
                              Utils.sendToServerConsole("debug",
                                  " Ascension command for " + player.getName()
                                      + ": " + command);
                              Bukkit.getServer().dispatchCommand(
                                  Bukkit.getConsoleSender(), command);
                            }
                          }
                        });
                  }

                }
              }
            });

        return;
      }

      if (item.getType().equals(Material.RED_CONCRETE)) {
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1,
            1);
        return;
      }
    }
    if (open.getSize() == 18 && open.getItem(1).getItemMeta().getDisplayName()
        .equals(Utils.chat("&3&l         Soy un chico"))) {
      event.setCancelled(true);

      if (!ConfigManager.configloaded) {
        player.closeInventory();
        player.sendMessage(Utils.userPluginTag() + Utils.chat(
            "&cNo se ha podido procesar la seleccion de genero debido a un mantenimiento de las veteranias. Intentalo mas tarde!"));
        return;
      }

      if (item == null || !item.hasItemMeta()) {
        return;
      }

      player.closeInventory();

      Bukkit.getScheduler().runTaskAsynchronously(Veteranias.plugin,
          new Runnable() {

            @Override
            public void run() {
              if (item.getType().equals(Material.CYAN_CONCRETE)) {
                LPmng.addMalePermission(player);
                player.sendMessage(Utils.userPluginTag() + Utils.chat(
                    "&7Has seleccionado el genero masculino. Se ha guardado la informacion correctamente."));
                return;
              }

              if (item.getType().equals(Material.GRAY_CONCRETE)) {
                LPmng.addMalePermission(player);
                player.sendMessage(Utils.userPluginTag() + Utils.chat(
                    "&7Se te ha agregado al genero masculino. Se ha guardado la informacion correctamente."));
                return;
              }

              if (item.getType().equals(Material.PURPLE_CONCRETE)) {
                LPmng.addFemalePermission(player);
                player.sendMessage(Utils.userPluginTag() + Utils.chat(
                    "&7Has seleccionado el genero femenino. Se ha guardado la informacion correctamente."));
                return;
              }
            }
          });
      return;

    }
  }
}
