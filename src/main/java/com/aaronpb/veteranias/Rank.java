package com.aaronpb.veteranias;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class Rank {

  private int cost;
  private String lpgroup, lpgroupascend;
  private String title_male, title_female, title_color;
  private ArrayList<String> commandslist = new ArrayList<String>();
  private ArrayList<ItemStack> iteminvlist = new ArrayList<ItemStack>();

  public void setRanklpgroup(String lpgroup) {
    this.lpgroup = lpgroup;
  }

  public void setRanklpgroupascend(String lpgroupascend) {
    this.lpgroupascend = lpgroupascend;
  }

  public void setRankTitleMale(String title_male) {
    this.title_male = title_male;
  }

  public void setRankTitleFemale(String title_female) {
    this.title_female = title_female;
  }

  public void setRankTitleColor(String title_color) {
    this.title_color = title_color;
  }

  public void setRankCost(int cost) {
    this.cost = cost;
  }

  public void addCommands(String command) {
    this.commandslist.add(command);
  }

  public void addDescription(ItemStack item) {
    this.iteminvlist.add(item);
  }

  public String getRanklpgroup() {
    return this.lpgroup;
  }

  public String getRanklpgroupascend() {
    return this.lpgroupascend;
  }

  public String getRankTitleMale() {
    return this.title_male;
  }

  public String getRankTitleFemale() {
    return this.title_female;
  }

  public String getRankTitleColor() {
    return this.title_color;
  }

  public int getRankCost() {
    return this.cost;
  }

  public ArrayList<String> getCommands() {
    return this.commandslist;
  }

  public ArrayList<ItemStack> getDescription() {
    return this.iteminvlist;
  }

}
