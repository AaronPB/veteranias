package com.aaronpb.veteranias.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.aaronpb.veteranias.ConfigManager;
import com.aaronpb.veteranias.Inventories;
import com.aaronpb.veteranias.LuckPermsManager;
import com.aaronpb.veteranias.VaultManager;
import com.aaronpb.veteranias.Veteranias;
import com.aaronpb.veteranias.utils.Utils;

public class Commands implements Listener, CommandExecutor {

  public String cmd1 = "ascender";
  public String cmd2 = "vetreload";

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label,
      String[] args) {
    LuckPermsManager LPmng   = new LuckPermsManager();
    VaultManager     ECONmng = new VaultManager();

    if (cmd.getName().equalsIgnoreCase(cmd1)) {
      if (sender instanceof Player) {

        Player player = (Player) sender;

        if (!ConfigManager.configloaded) {
          sender.sendMessage(Utils.userPluginTag() + Utils.chat(
              "&cEl ascenso en las veteranias esta en mantenimiento! Intentalo mas tarde!"));
          return true;
        }

        // Because of a bug when you are sleeping u can take all the
        // items from the generated inventory
        if (player.isSleeping() || player.isInsideVehicle()) {
          sender.sendMessage(Utils.userPluginTag() + Utils
              .chat("&aPara poder ascender debes estar de pie en el suelo!!"));
          return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Veteranias.plugin,
            new Runnable() {
              @Override
              public void run() {

                String playergroup = LPmng.getPlayerGroup(player);

                if (playergroup == null) {
                  sender.sendMessage(Utils.userPluginTag() + Utils.chat(
                      "&cNo perteneces a un rango valido de veteranias para poder ascender. Consulta este problema con kalhon89!"));
                  Utils.sendToServerConsole("warn", player.getName()
                      + " tried to ascend without being into a valid group defined in config: "
                      + playergroup);
                  return;
                }

                if (ConfigManager.ranksmap.get(playergroup)
                    .getRanklpgroupascend().equals("END")) {
                  sender.sendMessage(Utils.userPluginTag() + Utils.chat(
                      "&aYa has llegado a la cima de las veteranias!! Enhorabuena!"));
                  return;
                }

                Inventories i = new Inventories();

                String playergenre = LPmng.playerGenreNode(player);
                if (playergenre == null) {
                  sender.sendMessage(Utils.userPluginTag()
                      + Utils.chat("&aAntes debes seleccionar un genero!"));
                  Utils.sendToServerConsole("warn", player.getName()
                      + " tried to ascend without a valid genre! Opening genre inventory selection.");
                  Bukkit.getScheduler().runTask(Veteranias.plugin,
                      new Runnable() {
                        @Override
                        public void run() {
                          i.openInvGenre(player);
                        }
                      });
                  return;
                }

                String nextgroup = ConfigManager.ranksmap.get(playergroup)
                    .getRanklpgroupascend();

                Boolean hasmoney = ECONmng.hasPlayerMoney(player,
                    ConfigManager.ranksmap.get(nextgroup).getRankCost());

                player.playSound(player.getLocation(),
                    Sound.ENTITY_PLAYER_LEVELUP, 1, 2);

                Bukkit.getScheduler().runTask(Veteranias.plugin,
                    new Runnable() {
                      @Override
                      public void run() {
                        i.openInvAscenso(player, nextgroup, playergenre,
                            hasmoney);
                      }
                    });
              }
            });

        return true;
      } else {
        sender.sendMessage(
            Utils.chat("&cSolo los jugadores pueden ejecutar este comando!"));
        return false;
      }

    } else if (cmd.getName().equalsIgnoreCase(cmd2)) {
      Bukkit.getScheduler().runTaskAsynchronously(Veteranias.plugin,
          new Runnable() {
            @Override
            public void run() {
              ConfigManager cfgm = new ConfigManager();
              cfgm.reload();
              sender.sendMessage(Utils.userPluginTag()
                  + Utils.chat("&aSe ha reiniciado la configuracion!"));
            }
          });
      return true;
    }
    return false;
  }

}
