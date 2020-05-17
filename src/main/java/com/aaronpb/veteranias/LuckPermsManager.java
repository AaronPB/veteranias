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
    Utils.sendToServerConsole("debug", "LPmanager - Player para crear el user "
        + player + ", su nombre es: " + player.getName());
    User user = lpAPI.getUserManager().getUser(player.getName());
    if (user == null) {
      Utils.sendToServerConsole("error", "LPmanager - No existe el usuario "
          + player.getName() + " en LuckPerms!");
      return false;
    }
    return hasPermission(user, permission);
  }

  public void addMalePermission(Player player) {
    User user = lpAPI.getUserManager().getUser(player.getName());
    if (user == null) {
      Utils.sendToServerConsole("error", "LPmanager - No existe el usuario "
          + player.getName() + " en LuckPerms!");
      return;
    }
    addPermission(user, maleperm);
  }

  public void addFemalePermission(Player player) {
    User user = lpAPI.getUserManager().getUser(player.getName());
    if (user == null) {
      Utils.sendToServerConsole("error", "LPmanager - No existe el usuario "
          + player.getName() + " en LuckPerms!");
      return;
    }
    addPermission(user, femaleperm);
  }

  public boolean hasPermission(User user, String permission) {
    Utils.sendToServerConsole("debug", "LPmanager - Comprobando el permiso "
        + permission + " para el usuario " + user.getUsername());
    ContextManager      contextManager = lpAPI.getContextManager();
    ImmutableContextSet contextSet     = contextManager.getContext(user)
        .orElseGet(contextManager::getStaticContext);

    CachedPermissionData permissionData = user.getCachedData()
        .getPermissionData(QueryOptions.contextual(contextSet));
    Utils.sendToServerConsole("debug",
        "LPmanager - " + permission + " para " + user.getUsername() + " es "
            + permissionData.checkPermission(permission).asBoolean());
    return permissionData.checkPermission(permission).asBoolean();
  }

  public void addPermission(User user, String permission) {
    Utils.sendToServerConsole("debug", "LPmanager - Agregando permiso "
        + permission + " para el usuario " + user.getUsername());
    user.data().add(Node.builder(permission).build());
    lpAPI.getUserManager().saveUser(user);
  }

  // Methods for groups

  public String getPlayerGroup(Player player) {
    Utils.sendToServerConsole("debug",
        "LPmanager - Se ha cargado para revisar el siguiente hashmap: "
            + ConfigManager.ranksmap);
    ArrayList<String> usergroups = new ArrayList<String>();
    for (String group : ConfigManager.ranksmap.keySet()) {
      Utils.sendToServerConsole("debug", "LPmanager - Checkeando si "
          + player.getName() + " pertenece al grupo " + group);
      if (player.hasPermission("group." + group)) {
        usergroups.add(group);
        Utils.sendToServerConsole("debug",
            "LPmanager - " + player.getName() + " pertenece al grupo " + group);
      }
    }
    Utils.sendToServerConsole("debug",
        "LPmanager - " + player.getName() + " tiene esta lista de grupos: " + usergroups);
    if (usergroups.isEmpty()) {
      Utils.sendToServerConsole("warn",
          "LPmanager - No se han detectado grupos validos para "
              + player.getName());
      return null;
    }
    
    for (String group : usergroups) {
      Utils.sendToServerConsole("debug", "LPmanager - Revisando que el grupo " + group + " no tiene en el grupo de ascenso a: " + usergroups);
      if(!usergroups.contains(ConfigManager.ranksmap.get(group).getRanklpgroupascend())){
        return group;
      }
    }
    
    Utils.sendToServerConsole("warn", "LPmanager - " + player.getName() + " no pertenece a ningun grupo valido!");
    return null;
  }

  public boolean promotePlayer(Player player) {
    // Load user information
    User user = lpAPI.getUserManager().getUser(player.getName());
    if (user == null) {
      Utils.sendToServerConsole("error", "LPmanager - No existe el usuario "
          + player.getName() + " en LuckPerms!");
      return false;
    }

    // Load info from actual player group
    String playergroupfrom = getPlayerGroup(player);
    if (playergroupfrom == null) {
      Utils.sendToServerConsole("warn", "LPmanager - El usuario "
          + player.getName() + " no esta en ningun grupo aceptado en config!!");
      return false;
    }
    Group playeractualgroup = lpAPI.getGroupManager().getGroup(playergroupfrom);
    if (playeractualgroup == null) {
      Utils.sendToServerConsole("error", "LPmanager - No existe el grupo "
          + playergroupfrom + " en LuckPerms!");
      return false;
    }

    // Load info of the next group for player
    String playergroupto = ConfigManager.ranksmap.get(playergroupfrom)
        .getRanklpgroupascend();
    if (playergroupto == null) {
      Utils.sendToServerConsole("warn",
          "LPmanager - " + playergroupto + " no tiene una promocion valida!!");
      return false;
    }
    Group playerpromotegroup = lpAPI.getGroupManager().getGroup(playergroupto);
    if (playerpromotegroup == null) {
      Utils.sendToServerConsole("error",
          "LPmanager - No existe el grupo " + playergroupto + " en LuckPerms!");
      return false;
    }

    InheritanceNode newnode = InheritanceNode.builder(playerpromotegroup)
        .build();
    InheritanceNode oldnode = InheritanceNode.builder(playeractualgroup)
        .build();

    user.data().add(newnode);
    Utils.sendToServerConsole("info", "LPmanager - Se ha agregado a &3"
        + player.getName() + "&f al grupo &3" + playergroupto);

    user.data().remove(oldnode);
    Utils.sendToServerConsole("info", "LPmanager - Se ha eliminado a &3"
        + player.getName() + "&f del grupo &3" + playergroupfrom);

    lpAPI.getUserManager().saveUser(user);

    return true;
  }

}
