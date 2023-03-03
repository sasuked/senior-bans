package io.github.sasuked.seniorbans.command;

import io.github.sasuked.seniorbans.SeniorBans;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

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



    return false;
  }
}
