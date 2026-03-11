package dk.bjom.feedableSheep;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItem(event.getHand());
        if (itemInHand.getType() != Material.SHORT_GRASS && itemInHand.getType() != Material.GRASS_BLOCK && itemInHand.getType() != Material.TALL_GRASS) return;

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

            if (itemInHand.getType() == Material.GRASS_BLOCK) {
                player.give(ItemStack.of(Material.DIRT, 1));
            }
        }
    }
}
