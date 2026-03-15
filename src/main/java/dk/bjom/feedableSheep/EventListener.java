package dk.bjom.feedableSheep;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class EventListener implements Listener {
    private final FeedableSheep plugin;
    private final Set<Material> validItems;
    private final Map<Material, Material> itemDrops;

    public EventListener(FeedableSheep plugin) {
        this.plugin = plugin;
        validItems = getValidItems();
        itemDrops = getItemDrops();
    }

    private Set<Material> getValidItems() {
        Set<Material> items = EnumSet.noneOf(Material.class);
        plugin.getConfig().getStringList("valid_items").forEach(item ->
            items.add(Material.valueOf(item))
        );
        return items;
    }

    private Map<Material, Material> getItemDrops() {
        Map<Material, Material> drops = new EnumMap<>(Material.class);
        var section = plugin.getConfig().getConfigurationSection("item_drops");
        if (section == null) return drops;
        for (String key : section.getKeys(false)) {
            drops.put(Material.valueOf(key), Material.valueOf(section.getString(key)));
        }
        return drops;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItem(event.getHand());
        if (!validItems.contains(itemInHand.getType())) return;

        if (event.getRightClicked() instanceof Sheep sheep) {
            sheep.setSheared(false);

            Particle.HAPPY_VILLAGER.builder()
                    .location(sheep.getLocation())
                    .offset(0.5, 0.5, 0.5)
                    .count(10)
                    .extra(0.1)
                    .spawn();

            sheep.getWorld().playSound(sheep.getLocation(), "entity.llama.eat", 1.0f, 1.0f);

            if (player.getGameMode().isInvulnerable()) return;
            int amount = itemInHand.getAmount();

            // Reduce the item count by 1, or remove it if it was the last one
            if (amount > 1) {
                itemInHand.setAmount(amount - 1);
            } else {
                event.getPlayer().getInventory().setItem(event.getHand(), null);
            }

            Material drop = itemDrops.get(itemInHand.getType());
            if (drop != null) {
                player.give(ItemStack.of(drop, 1));
            }
        }
    }
}
