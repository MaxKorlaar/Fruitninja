package me.mrfahrenheit.fruitninja;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Created by Gijs on 26-8-2016.
 */
public class Fruitninja extends JavaPlugin implements Listener {

    static Fruitninja fruitninja;

    double location[] = {-230.5, 70D, 360.5}; // TODO: Read from file

    @Override
    public void onEnable() {
        fruitninja = this;
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onChat(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            new Game(event.getPlayer(), new Location(event.getPlayer().getWorld(), location[0], location[1], location[2]));
        }
    }

    public static Fruitninja i() {
        return fruitninja;
    }
}
