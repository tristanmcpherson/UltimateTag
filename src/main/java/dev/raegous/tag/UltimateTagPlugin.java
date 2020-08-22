package dev.raegous.tag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Optional;

public class UltimateTagPlugin extends JavaPlugin implements Listener {
    private TagInfo tagInfo;

    private void setupTagData() {
        tagInfo = new TagInfo();
        tagInfo = tagInfo.loadData();
        if (tagInfo == null) {
            tagInfo = new TagInfo();
            tagInfo.saveData();
        }
    }

    public void tagRandomPlayer() {
        Optional<? extends Player> randomPlayer = getRandom(Bukkit.getOnlinePlayers());
        randomPlayer.ifPresent(this::tag);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        setupTagData();
        tagRandomPlayer();
    }

    private void tag(Player player) {
        tag(player, null);
    }

    private void tag(Player player, Player by) {
        tagInfo.setActiveTaggerUsername(player.getName());
        getLogger().info("Setting current tagger to " + player.getName());

        player.getWorld().spawnParticle(Particle.BUBBLE_POP, player.getLocation(), 3);

        if (by == null) {
            say(player.getName() + " is now it!", ChatColor.AQUA);
        } else {
            say(by.getName() + " tagged " + player.getName(), ChatColor.AQUA);
        }
    }

    private void say(String message) {
        Bukkit.broadcastMessage(message);
    }

    private void say(String message, ChatColor color) {
        Bukkit.broadcastMessage(color.toString() + message);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Bukkit.getOnlinePlayers().size() > 1 && tagInfo.getActiveTaggerUsername().isEmpty()) {
            tagRandomPlayer();
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // pvp
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player)event.getDamager();
            Player attacked = (Player)event.getEntity();

            // if thorns is the cause of the damage, ignore
            if (event.getCause() == EntityDamageEvent.DamageCause.THORNS) {
                return;
            }

            if (attacker.getName().equals(tagInfo.getActiveTaggerUsername())) {
                tag(attacked, attacker);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getName().equals(tagInfo.getActiveTaggerUsername())) {
            tagRandomPlayer();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tag")) { // If the player typed /basic then do the following, note: If you only registered this executor for one command, you don't need this
            if (sender instanceof Player) {
                if (!sender.hasPermission("ultimatetag.tag")) {
                    sender.sendMessage("You can't change who is it");
                    return false;
                }
            }

            if (args.length < 1) {
                return false;
            }

            Player target = (Bukkit.getServer().getPlayer(args[0]));
            if (target == null) {
                sender.sendMessage(args[0] + " is not online!");
                return false;
            }

            getLogger().info(sender.getName() + " tagged " + target.getName());
            tag(target);

            return true;
        }

        return false;
    }

    public static <E> Optional<E> getRandom(Collection<E> e) {
        return e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst();
    }
}
