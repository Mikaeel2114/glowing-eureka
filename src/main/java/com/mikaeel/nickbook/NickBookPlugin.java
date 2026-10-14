package com.mikaeel.nickbook;

import com.mikaeel.nickbook.command.NickCommand;
import com.mikaeel.nickbook.listener.NickListener;
import com.mikaeel.nickbook.service.NickService;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class NickBookPlugin extends JavaPlugin {
    private NickService nickService;
    @Override public void onEnable() {
        saveDefaultConfig();
        nickService = new NickService(this);
        NickCommand command = new NickCommand(this, nickService);
        getCommand("nick").setExecutor(command);
        getCommand("nick").setTabCompleter(command);
        getServer().getPluginManager().registerEvents(new NickListener(this, nickService), this);
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (getConfig().getBoolean("players." + player.getUniqueId() + ".active", false)) {
                    sendLegacyActionBar(player, nickService.color("&fYou are currently &cNICKED (In games only)"));
                }
            }
        }, 20L, 20L);
    }
    private void sendLegacyActionBar(Player player, String message) {
        try {
            Class<?> baseComponent = Class.forName("net.md_5.bungee.api.chat.BaseComponent");
            Class<?> textComponent = Class.forName("net.md_5.bungee.api.chat.TextComponent");
            Object components = textComponent.getMethod("fromLegacyText", String.class).invoke(null, message);
            Object spigot = player.getClass().getMethod("spigot").invoke(player);
            spigot.getClass().getMethod("sendMessage", java.lang.reflect.Array.newInstance(baseComponent, 0).getClass())
                    .invoke(spigot, components);
        } catch (Exception ignored) {
            player.sendMessage(message);
        }
    }
    public NickService getNickService() { return nickService; }
}
