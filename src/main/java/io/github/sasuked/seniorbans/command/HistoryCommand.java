package io.github.sasuked.seniorbans.command;

import com.google.common.collect.ImmutableMap;
import io.github.sasuked.seniorbans.SeniorBans;
import io.github.sasuked.seniorbans.ban.PlayerBan;
import io.github.sasuked.seniorbans.gui.PlayerHistoryMenu;
import io.github.sasuked.seniorbans.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class HistoryCommand extends Command {

  private final SeniorBans plugin;

  public HistoryCommand(SeniorBans plugin) {
    super("history");
    this.plugin = plugin;
  }

  @Override
  public boolean execute(
    @NotNull CommandSender sender,
    @NotNull String commandLabel,
    @NotNull String[] args
  ) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("§cYou must be a player to use this command.");
      return false;
    }

    if (!sender.hasPermission("seniorbans.history")) {
      sender.sendMessage("§cYou do not have permission to use this command.");
      return false;
    }

    if (args.length == 0) {
      sender.sendMessage("§cUsage: /history <player>");
      return false;
    }

    UUID playerId = Players.fetchPlayerUniqueId(args[0]);
    if (playerId == null) {
      sender.sendMessage("§cThis player does not exist.");
      return false;
    }

    List<PlayerBan> playerBans = plugin.getBanRegistry().getPlayerBans(playerId);
    if (playerBans.isEmpty()) {
      sender.sendMessage("§cThis player has no history.");
      return false;
    }

    var menu = new PlayerHistoryMenu(Players.fetchPlayerName(playerId), playerBans);
    menu.open(player);
    return false;
  }
}
