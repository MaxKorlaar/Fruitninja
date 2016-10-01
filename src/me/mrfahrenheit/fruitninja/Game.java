package me.mrfahrenheit.fruitninja;

import net.minecraft.server.v1_8_R1.EnumParticle;
import net.minecraft.server.v1_8_R1.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Game implements Listener {

    static final double distance = 3.4;


    private int tickTask;

    final Location gameLocation;

    private final Player player;


    private char rainbowTick = 0;

    enum RainBowColor {
        RED, BLUE, GREEN
    }

    RainBowColor currentColor = RainBowColor.RED;


    private int score = 0;

    boolean clicking = false;
    private int clickingDelay = 0;

    public Game(Player player, Location location) {
        this.player = player;
        gameLocation = location;
        start();
    }

    public void addPoint() {
        score++;
        player.setLevel(score);
    }

    private int lives = 5;

    public void removeLife() {
        lives--;
        if (lives == 0) {
            stop();
        }
    }

    private int maxDelay = 80;
    private int delay = 20;

    private void tick() {
        if (clickingDelay++ > 5) clicking = false;
        if (delay-- < 0) {
            delay = new Random().nextInt(maxDelay);
            if (maxDelay > 20) maxDelay--;
            fruitList.add(new Fruit(this));
        }
        Location location = lookingLocation();
        if (rainbowTick++ == 255) {
            rainbowTick = 0;
        }
        if (clicking) {
            displayParticle(location, Color.getHSBColor(rainbowTick / 255F, 0.5F, 0.5F));
        }
        for (int i = 0; i < fruitList.size(); i++)
            if (fruitList.get(i).tick(location)) {
                fruitList.remove(i);
            }
    }

    private Location lookingLocation() {
        double z = distance + gameLocation.getZ();
        double playerDistance = player.getLocation().getZ() - z;
        double y = playerDistance * Math.tan(Math.toRadians(player.getLocation().getPitch())) + 1.7 + player.getLocation().getY();
        double x = playerDistance * Math.tan(Math.toRadians(player.getLocation().getYaw())) + player.getLocation().getX();
        return new Location(player.getWorld(), x, y, z);
    }

    private void displayParticle(Location location, Color color) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
                new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, false, (float) location.getX(),
                        (float) location.getY(), (float) location.getZ(), (float) (color.getRed() + 1) / 255 - 1,
                        (float) (color.getGreen() + 1) / 255, (float) (color.getBlue() + 1) / 255, 1F, 0));
    }


    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getPlayer() == player) {
            clicking = true;
            clickingDelay = 0;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickArEntity(PlayerInteractAtEntityEvent event) {
        if (event.getPlayer() == player) {
            clicking = true;
            clickingDelay = 0;
            event.setCancelled(true);

        }
    }

    @EventHandler
    public void onClickEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer() == player) {
            clicking = true;
            clickingDelay = 0;
            event.setCancelled(true);

        }
    }


    private void start() {
        tickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Fruitninja.i(), this::tick, 1, 1);
        player.teleport(gameLocation);
        player.setGameMode(GameMode.ADVENTURE);
        player.setItemInHand(new ItemStack(Material.BONE));
        register();
    }

    private final List<Fruit> fruitList = new ArrayList<>();

    private void register() {
        Bukkit.getPluginManager().registerEvents(this, Fruitninja.i());
    }

    private void deRegister() {
        HandlerList.unregisterAll(this);
    }

    private void stop() {
        deRegister();
        Bukkit.getScheduler().cancelTask(tickTask);
    }

}
