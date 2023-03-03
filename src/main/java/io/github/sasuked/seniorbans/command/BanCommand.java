package io.github.sasuked.seniorbans.command;

import io.github.sasuked.seniorbans.SeniorBans;
import io.github.sasuked.seniorbans.ban.PlayerBan;
import io.github.sasuked.seniorbans.util.ParseableTime;
import io.github.sasuked.seniorbans.util.ParseableTime.TimeParseException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BanCommand extends Command {

  private final SeniorBans plugin;

  public BanCommand(SeniorBans plugin) {
    super("ban");
    this.plugin = plugin;
  }

  // /ban <player> [duration] [reason]
  @Override
  public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
    if (!sender.hasPermission("seniorbans.ban")) {
      sender.sendMessage("§cYou do not have permission to use this command.");
      return false;
    }

    if (args.length == 0) {
      sender.sendMessage("§cUsage: /ban <player> [duration] [reason]");
      return false;
    }

    if (args[0].equalsIgnoreCase(sender.getName())) {
      sender.sendMessage("§cYou can't ban yourself.");
      return false;
    }

    Player target = Bukkit.getPlayer(args[0]);
    if (target == null) {
      sender.sendMessage("§cPlayer not found.");
      return false;
    }

    if (plugin.getBanRegistry().hasActiveBan(target.getUniqueId())) {
      sender.sendMessage("§cPlayer is already banned.");
      return false;
    }

    if (args.length == 1) {
      sender.sendMessage("§cPlease specify a valid duration. (e.g. 1mo, 1d, 1h, 1m, 1m10s)");
      return false;
    }

    ParseableTime banTime = null;
    String reason = null;
    try {
      banTime = ParseableTime.parseString(args[1]);
    } catch (TimeParseException ex) {
      // if the duration is not valid, we assume the reason is the first argument
      reason = String.join(" ", args[1].split(" "));
    }

    // if the duration is valid, we assume the reason is the second argument
    if (banTime != null) {
      reason = args.length > 2 ? String.join(" ", args[2].split(" ")) : "No reason specified.";
    }

    var playerBan = PlayerBan.builder()
      .uniqueId(UUID.randomUUID())
      .bannedPlayerId(target.getUniqueId())
      .authorId(sender instanceof Player ? ((Player) sender).getUniqueId() : null)
      .creationTime(System.currentTimeMillis())
      .expirationTime(banTime != null ? (System.currentTimeMillis() + banTime.toMilliseconds()) : -1)
      .reason(reason)
      .build();


    plugin.getBanRepository()
      .updateBan(playerBan)
      .whenComplete((result, throwable) -> {
        if (throwable != null) {
          throwable.printStackTrace();
          sender.sendMessage("§cAn error occurred while trying to ban the player.");
        } else if (result){
          plugin.getBanRegistry().register(playerBan);
          sender.sendMessage("§aPlayer banned successfully.");
          Bukkit.getScheduler().runTask(plugin, () -> target.kickPlayer("§cYou have been banned from this server."));
        } else {
          sender.sendMessage("§cAn error occurred while trying to ban the player.");
        }
      });

    return false;
  }
}
