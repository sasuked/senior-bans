package io.github.sasuked.seniorbans.ban;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlayerBanRepository {

  private final Plugin plugin;

  private final Dao<PlayerBan, UUID> playerBanDao;

  private final Executor executor = Executors.newCachedThreadPool();

  public PlayerBanRepository(Plugin plugin, Dao<PlayerBan, UUID> playerBanDao) {
    this.plugin = plugin;
    this.playerBanDao = playerBanDao;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static PlayerBanRepository sqliteRepository(Plugin plugin) throws Exception {
    File dataFolder = plugin.getDataFolder();
    if (!dataFolder.exists()) {
      dataFolder.mkdirs();
    }

    File databaseFile = new File(dataFolder, "player-wallets.db");
    if (!databaseFile.exists()) {
      databaseFile.createNewFile();
    }

    JdbcConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + databaseFile);
    Dao<PlayerBan, UUID> walletDao = DaoManager.createDao(connectionSource, PlayerBan.class);

    TableUtils.createTableIfNotExists(connectionSource, PlayerBan.class);
    return new PlayerBanRepository(plugin, walletDao);
  }


  public CompletableFuture<Boolean> updateBan(PlayerBan playerBan) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        var status = playerBanDao.createOrUpdate(playerBan);
        return status.isCreated() || status.isUpdated();
      } catch (Exception e) {
        return false;
      }
    }, executor);
  }

  public CompletableFuture<List<PlayerBan>> selectAllBans() {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return playerBanDao.queryForAll();
      } catch (Exception e) {
        return Collections.emptyList();
      }
    }, executor);
  }

  public CompletableFuture<Boolean> deleteMany(List<PlayerBan> playerBans) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return playerBanDao.delete(playerBans) > 0;
      } catch (Exception e) {
        return false;
      }
    }, executor);
  }
}
