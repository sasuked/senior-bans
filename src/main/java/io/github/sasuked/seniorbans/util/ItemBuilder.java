package io.github.sasuked.seniorbans.util;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ItemBuilder {

  private static final Map<String, ItemStack> HEADS_BY_NAME = Maps.newHashMap();
  private static final Map<UUID, ItemStack> HEADS_BY_UUID = Maps.newHashMap();

  private final ItemStack itemStack;

  public static ItemBuilder fromHead(UUID playerId) {
    if (HEADS_BY_UUID.containsKey(playerId)) {
      return new ItemBuilder(HEADS_BY_UUID.get(playerId));
    }

    ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (!(itemMeta instanceof SkullMeta skullMeta)) {
      throw new IllegalStateException("ItemMeta is not a SkullMeta");
    }

    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerId));
    itemStack.setItemMeta(itemMeta);

    HEADS_BY_UUID.put(playerId, itemStack);

    return new ItemBuilder(itemStack);
  }

  public static ItemBuilder fromHead(String playerName) {
    if (HEADS_BY_NAME.containsKey(playerName)) {
      return new ItemBuilder(HEADS_BY_NAME.get(playerName));
    }

    ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (!(itemMeta instanceof SkullMeta skullMeta)) {
      throw new IllegalStateException("ItemMeta is not a SkullMeta");
    }

    skullMeta.setOwner(playerName);
    itemStack.setItemMeta(itemMeta);

    HEADS_BY_NAME.put(playerName, itemStack);

    return new ItemBuilder(itemStack);
  }

  public ItemBuilder(ItemStack itemStack) {
    this.itemStack = itemStack.clone();
  }

  public ItemBuilder(Material material) {
    this(new ItemStack(material));
  }

  public ItemBuilder name(String name) {
    return applyItemMeta(itemMeta -> itemMeta.setDisplayName(name));
  }


  public ItemBuilder lore(String... lore) {
    return applyItemMeta(itemMeta -> itemMeta.setLore(Stream.of(lore)
      .map(line -> line.replace("&", "§"))
      .toList()
    ));
  }

  public ItemBuilder applyItemStack(Consumer<ItemStack> itemStackConsumer) {
    itemStackConsumer.accept(itemStack);
    return this;
  }

  public ItemBuilder applyItemMeta(Consumer<ItemMeta> metaConsumer) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta == null) {
      itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
    }

    metaConsumer.accept(itemMeta);
    itemStack.setItemMeta(itemMeta);
    return this;
  }


  public ItemStack build() {
    return itemStack;
  }
}
