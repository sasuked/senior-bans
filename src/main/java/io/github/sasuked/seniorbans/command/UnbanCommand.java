package io.github.sasuked.seniorbans.command;

import io.github.sasuked.seniorbans.SeniorBans;
import io.github.sasuked.seniorbans.ban.PlayerBan;
import io.github.sasuked.seniorbans.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class UnbanCommand extends Command {

  private final SeniorBans plugin;

  public UnbanCommand(SeniorBans plugin) {
    super("unban");
    this.plugin = plugin;
  }

  @Override
  public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
    if (!sender.hasPermission("seniorbans.unban")) {
      sender.sendMessage("§cYou do not have permission to use this command.");
      return false;
    }

    if (args.length == 0) {
      sender.sendMessage("§cUsage: /unban <player>");
      return false;
    }

    UUID bannedPlayerId = Players.fetchPlayerUniqueId(args[0]);
    if (bannedPlayerId == null) {
      sender.sendMessage("§cThis player does not exist.");
      return false;
    }

    List<PlayerBan> activePlayerBans = plugin.getBanRegistry().getActivePlayerBans(bannedPlayerId);
    if (activePlayerBans.isEmpty()) {
      sender.sendMessage("§cThis player is not banned.");
      return false;
    } else {
      activePlayerBans.forEach(ban -> ban.setActive(false));


      // do not delete the ban from the database, just set the end time to now
      plugin.getBanRepository()
        .updateMany(activePlayerBans)
        .whenComplete((unused, throwable) -> {
          if (throwable != null) {
            sender.sendMessage("§cAn error occurred while unbanning " + args[0] + ".");
          } else {
            sender.sendMessage("§aYou have unbanned " + args[0] + ".");
          }
        });
    }

    return false;
  }
}
