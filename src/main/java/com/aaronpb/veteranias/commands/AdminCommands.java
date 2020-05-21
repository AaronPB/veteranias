package com.aaronpb.veteranias.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import com.aaronpb.veteranias.ConfigManager;
import com.aaronpb.veteranias.Veteranias;
import com.aaronpb.veteranias.utils.Utils;

public class AdminCommands implements Listener, CommandExecutor {

  public String adminreload_cmd = "vetreload";
//  public String adminchangegenre_cmd = "changeplayergenre";

//  private LuckPermsManager LPmng = new LuckPermsManager();

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label,
      String[] args) {

//    if (cmd.getName().equalsIgnoreCase(adminchangegenre_cmd)) {
//      if (args == null) {
//        sender.sendMessage(Utils.userPluginTag() + Utils.chat(
//            "&cInvalid arguments!\n&fUsage: /changeplayergenre <player> <male|female>"));
//        return true;
//      }
//      if (args.length == 2) {
//        if (!args[1].equalsIgnoreCase("male")
//            && !args[1].equalsIgnoreCase("female")) {
//          sender.sendMessage(Utils.userPluginTag() + Utils.chat(
//              "&cInvalid genre " + args[1] + ". Choose between male or female!"));
//          return true;
//        }
//        Player player = null;
//        for(Player online : Bukkit.getOnlinePlayers()) {
//          if(args[0].equalsIgnoreCase(online.getName())) {
//            player = online;
//          }
//        }
//        if (player == null) {
//          sender.sendMessage(Utils.userPluginTag() + Utils.chat(
//              "&cInvalid player name " + args[0] + " or it is not online."));
//          return true;
//        }
//        Utils.sendToServerConsole("debug", "Player: " + player);
//        String playergenre = LPmng.playerGenreNode(player);
//        if (playergenre == null) {
//          sender.sendMessage(Utils.userPluginTag() + Utils
//              .chat("&c" + player.getName() + " has not selected a genre yet!"));
//          return true;
//        }
//        if (args[1].equalsIgnoreCase(playergenre)) {
//          sender.sendMessage(Utils.userPluginTag() + Utils
//              .chat("&c" + player.getName() + " is already in that genre!"));
//          return true;
//        }
//        LPmng.changePermission(player, "veteranias." + playergenre,
//            "veteranias." + args[1]);
//        sender.sendMessage(
//            Utils.userPluginTag() + Utils.chat("&aSe ha cambiado el genero de "
//                + player.getName() + " a " + args[1]));
//      }else {
//        sender.sendMessage(Utils.userPluginTag() + Utils.chat(
//            "&cInvalid arguments!\n&fUsage: /changeplayergenre <player> <male|female>"));
//        return true;
//      }
//      
//      return true;
//    }
    if (cmd.getName().equalsIgnoreCase(adminreload_cmd)) {
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
