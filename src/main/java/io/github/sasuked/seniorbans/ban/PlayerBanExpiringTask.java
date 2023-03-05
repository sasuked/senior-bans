package io.github.sasuked.seniorbans.ban;

import io.github.sasuked.seniorbans.SeniorBans;
import org.bukkit.Bukkit;

public class PlayerBanExpiringTask {

  private final SeniorBans bans;

  public PlayerBanExpiringTask(SeniorBans bans) {
    this.bans = bans;
  }

  public void start() {
    Bukkit.getScheduler().runTaskTimerAsynchronously(bans, this::refreshBans, 0L, 20L);
  }

  private void refreshBans() {
    bans.getBanRegistry()
      .getPlayerBanMap()
      .values()
      .stream()
      .filter(PlayerBan::isExpired)
      .forEach(playerBan -> playerBan.setActive(false));
  }
}
