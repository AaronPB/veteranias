package com.aaronpb.veteranias;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.aaronpb.veteranias.utils.Utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.query.QueryOptions;

public class LuckPermsManager {

  private LuckPerms lpAPI = Veteranias.luckPerms;
  private String maleperm = "veteranias.male";
  private String femaleperm = "veteranias.female";

  // Methods for permissions

  public String playerGenreNode(Player player) {

    if (playerHasPermission(player, maleperm)) {
      return "male";
    }
    if (playerHasPermission(player, femaleperm)) {
      return "female";
    }
    return null;
  }

  public boolean playerHasPermission(Player player, String permission) {
    User user = lpAPI.getUserManager().getUser(player.getName());
    if (user == null) {
      Utils.sendToServerConsole("warn", "LPmanager - " + player.getName()
          + " does no exist in LuckPerms! Returning false");
      return false;
    }
    return hasPermission(user, permission);
  }

  public void addMalePermission(Player player) {
    User user = lpAPI.getUserManager().getUser(player.getName());
    if (user == null) {
      Utils.sendToServerConsole("warn", "LPmanager - " + player.getName()
          + " does no exist in LuckPerms! Returning");
      return;
    }
    Utils.promotionslogger
        .info(player.getName() + " - Ha seleccionado el genero masculino");
    addPermission(user, maleperm);
  }

  public void addFemalePermission(Player player) {
    User user = lpAPI.getUserManager().getUser(player.getName());
    if (user == null) {
      Utils.sendToServerConsole("warn", "LPmanager - " + player.getName()
          + " does no exist in LuckPerms! Returning");
      return;
    }
    Utils.promotionslogger
        .info(player.getName() + " - Ha seleccionado el genero femenino");
    addPermission(user, femaleperm);
  }

  public boolean hasPermission(User user, String permission) {
    Utils.sendToServerConsole("debug", "LPmanager - Checking permission "
        + permission + " for " + user.getUsername());
    ContextManager      contextManager = lpAPI.getContextManager();
    ImmutableContextSet contextSet     = contextManager.getContext(user)
        .orElseGet(contextManager::getStaticContext);

    CachedPermissionData permissionData = user.getCachedData()
        .getPermissionData(QueryOptions.contextual(contextSet));
    Utils.sendToServerConsole("debug",
        "LPmanager - " + permission + " for " + user.getUsername() + " is "
            + permissionData.checkPermission(permission).asBoolean());
    return permissionData.checkPermission(permission).asBoolean();
  }

  private void addPermission(User user, String permission) {
    Utils.sendToServerConsole("debug", "LPmanager - Adding permission "
        + permission + " to " + user.getUsername());
    user.data().add(Node.builder(permission).build());
    lpAPI.getUserManager().saveUser(user);
  }

  // Methods for groups

  public String getPlayerGroup(Player player) {
    Utils.sendToServerConsole("debug",
        "LPmanager - The next hashmap has been loaded: "
            + ConfigManager.ranksmap);
    ArrayList<String> usergroups = new ArrayList<String>();
    for (String group : ConfigManager.ranksmap.keySet()) {
      Utils.sendToServerConsole("debug", "LPmanager - Checking if "
          + player.getName() + " is in group " + group);
      if (player.hasPermission("group." + group)) {
        usergroups.add(group);
        Utils.sendToServerConsole("debug",
            "LPmanager - " + player.getName() + " is in group " + group);
      }
    }
    if (usergroups.isEmpty()) {
      Utils.sendToServerConsole("warn",
          "LPmanager - There are no valid groups for " + player.getName());
      return null;
    }
    Utils.sendToServerConsole("debug", "LPmanager - " + player.getName()
        + " has the following group list: " + usergroups);

    for (String group : usergroups) {
      Utils.sendToServerConsole("debug", "LPmanager - Checking if " + group
          + " has no ascension to another group listed in: " + usergroups);
      if (!usergroups
          .contains(ConfigManager.ranksmap.get(group).getRanklpgroupascend())) {
        return group;
      }
    }

    Utils.sendToServerConsole("warn",
        "LPmanager - " + player.getName() + " is not into a valid group!");
    return null;
  }

  public boolean promotePlayer(Player player, String playergroupfrom) {
    // Load user information
    User user = lpAPI.getUserManager().getUser(player.getName());
    if (user == null) {
      Utils.sendToServerConsole("warn", "LPmanager - " + player.getName()
          + " does no exist in LuckPerms! Returning false");
      return false;
    }

    // Load info from actual player group
    Group playeractualgroup = lpAPI.getGroupManager().getGroup(playergroupfrom);
    if (playeractualgroup == null) {
      Utils.sendToServerConsole("warn", "LPmanager - Group " + playergroupfrom
          + " does not exist in LuckPerms!");
      return false;
    }

    // Load info of the next group for player
    String playergroupto = ConfigManager.ranksmap.get(playergroupfrom)
        .getRanklpgroupascend();
    if (playergroupto == null) {
      Utils.sendToServerConsole("warn",
          "LPmanager - " + playergroupto + " has no valid promotion!");
      return false;
    }
    Group playerpromotegroup = lpAPI.getGroupManager().getGroup(playergroupto);
    if (playerpromotegroup == null) {
      Utils.sendToServerConsole("error", "LPmanager - The group "
          + playergroupto + " does not exist in LuckPerms!");
      return false;
    }

    InheritanceNode newnode = InheritanceNode.builder(playerpromotegroup)
        .build();

    user.data().add(newnode);
    Utils.sendToServerConsole("info", "LPmanager - " + player.getName()
        + " has been added to " + playergroupto);

    if (!playeractualgroup.getName().equals("default")) {
      InheritanceNode oldnode = InheritanceNode.builder(playeractualgroup)
          .build();
      user.data().remove(oldnode);
      Utils.sendToServerConsole("info", "LPmanager - " + player.getName()
          + " has been removed from " + playergroupto);
    } else {
      Utils.sendToServerConsole("info",
          "LPmanager - " + player.getName() + "has not been removed from group "
              + playergroupfrom + " because it is the default group.");
    }

    Utils.promotionslogger.info(player.getName() + " - De " + playergroupfrom
        + " a " + playergroupto + " - Coste: "
        + ConfigManager.ranksmap.get(playergroupto).getRankCost()
        + " dracmas.");
    lpAPI.getUserManager().saveUser(user);

    return true;
  }

}
