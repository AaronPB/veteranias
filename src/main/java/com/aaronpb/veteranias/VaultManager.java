package com.aaronpb.veteranias;

import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.Economy;

public class VaultManager {

  private Economy econAPI = Veteranias.economy;

  public boolean takePlayerMoney(Player player, double amount) {
    if (hasPlayerMoney(player, amount)) {
      econAPI.withdrawPlayer(player, amount);
      return true;
    }
    return false;
  }

  public boolean hasPlayerMoney(Player player, double amount) {
    if (getPlayerMoney(player) >= amount) {
      return true;
    }
    return false;
  }

  public double getPlayerMoney(Player player) {
    return econAPI.getBalance(player);
  }

}
