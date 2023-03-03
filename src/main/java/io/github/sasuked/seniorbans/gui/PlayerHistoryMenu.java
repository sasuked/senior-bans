package io.github.sasuked.seniorbans.gui;

import fr.mrmicky.fastinv.FastInv;
import io.github.sasuked.seniorbans.ban.PlayerBan;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Comparator;
import java.util.List;

public class PlayerHistoryMenu extends FastInv {

  private final List<PlayerBan> bans;

  public PlayerHistoryMenu(String playerName, List<PlayerBan> bans) {
    super(54, "Player history of " + playerName);
    this.bans = bans;

    bans.stream()
      .sorted(Comparator.comparingLong(PlayerBan::getCreationTime).reversed())
      .map(PlayerBan::asItemStack)
      .forEachOrdered(this::addItem);
  }

  @Override
  protected void onClick(InventoryClickEvent event) {
    event.setCancelled(true);
  }

}
