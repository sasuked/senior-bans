package io.github.sasuked.seniorbans.ban;

import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerBanRegistry {

  @Getter
  private final Map<UUID, PlayerBan> playerBanMap = new ConcurrentHashMap<>();

  public void register(PlayerBan playerBan) {
    playerBanMap.put(playerBan.getUniqueId(), playerBan);
  }

  public void unregister(PlayerBan playerBan) {
    playerBanMap.remove(playerBan.getUniqueId());
  }

  public boolean hasActiveBan(UUID playerId) {
    return playerBanMap.values()
      .stream()
      .filter(playerBan -> playerBan.getBannedPlayerId().equals(playerId))
      .anyMatch(PlayerBan::isActive);
  }

  public PlayerBan getMostRecentActiveBan(UUID playerId) {
    return playerBanMap.values()
      .stream()
      .filter(playerBan -> playerBan.getBannedPlayerId().equals(playerId))
      .filter(PlayerBan::isActive)
      .max(Comparator.comparingLong(PlayerBan::getCreationTime))
      .orElse(null);
  }

  public List<PlayerBan> getActivePlayerBans(UUID playerId) {
    return playerBanMap.values()
      .stream()
      .filter(PlayerBan::isActive)
      .filter(playerBan -> playerBan.getBannedPlayerId().equals(playerId))
      .toList();
  }

  public List<PlayerBan> getPlayerBans(UUID playerId) {
    return playerBanMap.values()
      .stream()
      .filter(playerBan -> playerBan.getBannedPlayerId().equals(playerId))
      .toList();
  }

  public void removePlayerBans(List<PlayerBan> activePlayerBans) {
    activePlayerBans.forEach(this::unregister);
  }
}
