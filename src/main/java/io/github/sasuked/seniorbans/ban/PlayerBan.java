package io.github.sasuked.seniorbans.ban;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.github.sasuked.seniorbans.util.ItemBuilder;
import io.github.sasuked.seniorbans.util.Players;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@DatabaseTable(tableName = "player_bans")
@AllArgsConstructor
@NoArgsConstructor
public class PlayerBan {

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");


  @DatabaseField(id = true)
  private UUID uniqueId;

  @DatabaseField(columnName = "banned_player_id")
  private UUID bannedPlayerId;

  @DatabaseField(columnName = "author_id")
  private UUID authorId;

  @DatabaseField(columnName = "reason")
  private String reason;

  @DatabaseField(columnName = "creation_time")
  private long creationTime;

  //  -1 = permanent
  @DatabaseField(columnName = "expiration_time")
  private long expirationTime;

  public boolean isPermanent() {
    return expirationTime == -1;
  }

  public boolean isActive() {
    return isPermanent() || expirationTime > System.currentTimeMillis();
  }


  public ItemStack asItemStack() {

    return ItemBuilder.fromHead(bannedPlayerId)
      .name("§c" + Players.fetchPlayerName(bannedPlayerId))
      .lore(
        "",
        "§7Reason: §c" + reason,
        "§7Author: §c" + Players.fetchPlayerName(authorId),
        "§7Creation time: §c" + getFormattedCreationTime(),
        isActive() ? "§7Expiration time: §c" + getFormattedExpirationTime() : "§7Permanent",
        "§7Active: §c" + isActive(),
        "",
        "§8Ban id: §7" + uniqueId.toString()
      )
      .build();
  }


  public String getFormattedExpirationTime() {
    if (expirationTime == -1) {
      return "Permanent";
    }
    return DATE_FORMAT.format(new Date(expirationTime));
  }

  public String getFormattedCreationTime() {
    return DATE_FORMAT.format(new Date(creationTime));
  }
}
