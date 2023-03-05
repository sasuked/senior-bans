package io.github.sasuked.seniorbans.listener;

import io.github.sasuked.seniorbans.SeniorBans;
import io.github.sasuked.seniorbans.ban.PlayerBan;
import io.github.sasuked.seniorbans.util.Players;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import java.util.Arrays;

public class LoginListener implements Listener {

  private final SeniorBans plugin;

  public LoginListener(SeniorBans plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void handlePlayerLogin(AsyncPlayerPreLoginEvent event) {
    PlayerBan mostRecentBan = plugin.getBanRegistry().getMostRecentActiveBan(event.getUniqueId());
    if (mostRecentBan == null) {
      return;
    }

    event.disallow(Result.KICK_BANNED, String.join("\n", Arrays.asList(
      "§cYou are banned from this server!",
      "§cReason: §f" + mostRecentBan.getReason(),
      "§cBanned by: §f" + Players.fetchPlayerName(mostRecentBan.getAuthorId()),
      "§cBanned until: §f" + mostRecentBan.getFormattedExpirationTime()
    )));
  }
}
