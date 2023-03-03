package io.github.sasuked.seniorbans.util;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.Executor;


public class AsynchronousBukkitExecutor implements Executor {

  private final Plugin plugin;

  public AsynchronousBukkitExecutor(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void execute(Runnable command) {
    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, command);
  }
}
