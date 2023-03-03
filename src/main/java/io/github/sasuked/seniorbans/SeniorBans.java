package io.github.sasuked.seniorbans;

import fr.mrmicky.fastinv.FastInvManager;
import io.github.sasuked.seniorbans.ban.PlayerBanRegistry;
import io.github.sasuked.seniorbans.ban.PlayerBanRepository;
import io.github.sasuked.seniorbans.command.BanCommand;
import io.github.sasuked.seniorbans.command.HistoryCommand;
import io.github.sasuked.seniorbans.command.UnbanCommand;
import io.github.sasuked.seniorbans.listener.LoginListener;
import io.github.sasuked.seniorbans.util.CommandMapProvider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
public class SeniorBans extends JavaPlugin {

  private PlayerBanRegistry banRegistry;
  private PlayerBanRepository banRepository;


  @Override
  public void onEnable() {
    try {
      FastInvManager.register(this);

      banRegistry = new PlayerBanRegistry();

      banRepository = PlayerBanRepository.sqliteRepository(this);
      banRepository.selectAllBans()
        .whenComplete((bans, throwable) -> {
          if (throwable != null) {
            throwable.printStackTrace();
          } else {
            bans.forEach(banRegistry::register);
            Bukkit.getConsoleSender().sendMessage("§aLoaded " + bans.size() + " bans.");
          }
        });


      Bukkit.getPluginManager().registerEvents(new LoginListener(this), this);

      CommandMapProvider.getCommandMap().registerAll("seniorbans", Arrays.asList(
        new BanCommand(this),
        new UnbanCommand(this),
        new HistoryCommand(this)
      ));

    } catch (Exception e) {
      e.printStackTrace();
      Bukkit.getPluginManager().disablePlugin(this);
    }
  }

  @Override
  public void onDisable() {
    banRegistry = null;
    banRepository = null;
  }
}
