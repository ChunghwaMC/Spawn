package me.rockquiet.spawn;

import me.rockquiet.spawn.commands.CommandDelay;
import me.rockquiet.spawn.commands.SpawnCommand;
import me.rockquiet.spawn.commands.TabComplete;
import me.rockquiet.spawn.events.TeleportOnJoinEvents;
import me.rockquiet.spawn.events.TeleportOnRespawnEvent;
import me.rockquiet.spawn.events.TeleportOutOfVoidEvent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Spawn extends JavaPlugin {

    private static Spawn plugin;

    @Override
    public void onEnable() {
        plugin = this;

        loadConfig();

        TabCompleter tc = new TabComplete();
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("spawn").setTabCompleter(tc);

        Bukkit.getPluginManager().registerEvents(new TeleportOnJoinEvents(), this);
        Bukkit.getPluginManager().registerEvents(new TeleportOutOfVoidEvent(), this);
        Bukkit.getPluginManager().registerEvents(new TeleportOnRespawnEvent(), this);
        Bukkit.getPluginManager().registerEvents(new CommandDelay(), this);
    }

    public static Spawn getPlugin() {
        return plugin;
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        if (getConfig().options().getHeader().isEmpty()) {
            final List<String> header = new ArrayList<>();

            header.add(0, "---------------------------------------------------- #");
            header.add(1, "                 Spawn by rockquiet                  #");
            header.add(2, "---------------------------------------------------- #");
            header.add(3, "   Wiki - https://github.com/rockquiet/Spawn/wiki    #");
            header.add(4, "---------------------------------------------------- #");

            getConfig().options().setHeader(header);
        }
        saveConfig();
    }

    public Location getSpawn() {
        reloadConfig();
        World world = Bukkit.getWorld(getConfig().getString("spawn.world"));
        double x = getConfig().getDouble("spawn.x");
        double y = getConfig().getDouble("spawn.y");
        double z = getConfig().getDouble("spawn.z");
        float yaw = (float) getConfig().getDouble("spawn.yaw");
        float pitch = (float) getConfig().getDouble("spawn.pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void teleportPlayer(Player player) {
        if (getConfig().getString("spawn.world") != null && getConfig().getString("spawn.x") != null && getConfig().getString("spawn.y") != null && getConfig().getString("spawn.z") != null && getConfig().getString("spawn.yaw") != null && getConfig().getString("spawn.pitch") != null) {
            if (!getConfig().getBoolean("options.fall-damage")) {
                player.setFallDistance(0F);
            }
            player.teleport(getSpawn());

            spawnEffects(player);

            sendMessageToPlayer(player, "messages.teleport");
        } else {
            sendMessageToPlayer(player, "messages.no-spawn");
        }
    }

    public void spawnEffects(Player player) {
        // Particles
        if (!Bukkit.getVersion().contains("1.8")) {
            player.spawnParticle(Particle.valueOf(getConfig().getString("options.particle")), getSpawn(), getConfig().getInt("options.particle-amount"));
        } else {
            // workaround for 1.8
            for (int p = 0; p < getConfig().getInt("options.particle-amount"); p++) {
                Bukkit.getWorld(getSpawn().getWorld().getName()).playEffect(getSpawn(), Effect.valueOf(getConfig().getString("options.particle")), 0);
            }
        }
        // Sounds
        player.playSound(getSpawn(), Sound.valueOf(getConfig().getString("options.sound")), (float) getConfig().getDouble("options.sound-volume"), (float) getConfig().getDouble("options.sound-pitch"));
    }

    public void sendMessageToPlayer(Player player, String message) {
        if (getConfig().getString(message).isEmpty() || getConfig().getString(message) == null) {
            // do not send a message to the player
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString(message)));
        }
    }
    public void sendPlaceholderMessageToPlayer(Player player, String message, String placeholder, String replacePlaceholder) {
        if (getConfig().getString(message).isEmpty() || getConfig().getString(message) == null) {
            // do not send a message to the player
        } else if (getConfig().getString(message).contains(placeholder)){
            String convertedMessage = getConfig().getString(message).replace(placeholder, replacePlaceholder);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', convertedMessage));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString(message)));
        }
    }
    public void sendMessageToSender(CommandSender sender, String message) {
        if (getConfig().getString(message).isEmpty() || getConfig().getString(message) == null) {
            // do not send a message to the sender
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString(message)));
        }
    }
}
