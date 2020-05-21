package com.aaronpb.veteranias.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.aaronpb.veteranias.ConfigManager;
import com.aaronpb.veteranias.Veteranias;
import com.aaronpb.veteranias.listeners.VeteraniasInventories;
import com.aaronpb.veteranias.utils.Utils;

public class PromoteCommands implements Listener, CommandExecutor{
  
  public String promotecmd = "ascender";
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label,
      String[] args) {

    if (cmd.getName().equalsIgnoreCase(promotecmd)) {
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
                VeteraniasInventories inv = new VeteraniasInventories();
                inv.openPromoteInv(player);
              }
            });

        return true;
      } else {
        sender.sendMessage(
            Utils.chat("&cSolo los jugadores pueden ejecutar este comando!"));
        return false;
      }

    }
    return false;
  }

}
