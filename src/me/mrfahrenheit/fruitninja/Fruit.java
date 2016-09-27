package me.mrfahrenheit.fruitninja;

import net.minecraft.server.v1_8_R1.EntityArmorStand;
import net.minecraft.server.v1_8_R1.Vector3f;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class Fruit {

    static final double gravity = 0.01;

    Vector velocity;
    Random random = new Random();
    Game fruitninja;
    EntityArmorStand armorStand;
    Material fruit;

    public Fruit(Game fruitninja) {
        int randomint = random.nextInt(3);
        switch (randomint) {
            case 0:
                fruit = Material.TNT;
                break;
            case 1:
                fruit = Material.APPLE;
                break;
            case 2:
                fruit = Material.MELON;
                break;
        }
        this.fruitninja = fruitninja;
        Location spawnLocation = locationAndVelocity();
        armorStand = new EntityArmorStand(((CraftWorld) fruitninja.gameLocation.getWorld()).getHandle(), spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
        armorStand.setGravity(true);
        armorStand.setInvisible(true);
        armorStand.setBasePlate(true);
        armorStand.setArms(true);
        armorStand.setRightArmPose(new Vector3f(90, 0, 180));
        armorStand.setEquipment(0, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(fruit)));
        armorStand.getWorld().addEntity(armorStand);
    }

    public Location locationAndVelocity() {
        double beginPoint = randomDouble(-1.5, 1.5);
        double endPoint = randomDouble(-1.5, 1.5);
        double velocityY = randomDouble(0.20, 0.27);
        int ticksAlive = (int) (velocityY / gravity * 2);
        double velocityX = (endPoint - beginPoint) / ticksAlive;
        velocity = new Vector(velocityX, velocityY, 0);
        Location gameLocation = fruitninja.gameLocation;
        return ItemToArmor(new Location(gameLocation.getWorld(), fruitninja.gameLocation.getX() + beginPoint, fruitninja.gameLocation.getY(), fruitninja.gameLocation.getZ() + Game.distance));
    }

    public double randomDouble(double rangeMin, double rangeMax) {
        return rangeMin + (rangeMax - rangeMin) * random.nextDouble();
    }


    public boolean tick(Location location) {
        armorStand.setLocation(velocity.getX() + armorStand.locX, velocity.getY() + armorStand.locY, armorStand.locZ, 0, 0);
        velocity.setY(velocity.getY() - gravity);

        if (location.distance(getFruitLocation()) < 0.3 && fruitninja.clicking) {
            despawn();
            if (fruit == Material.TNT) {
                fruitninja.removeLife();
            } else {
                fruitninja.addPoint();
            }
            return true;
        }
        if (armorStand.locY + 1.9 < fruitninja.gameLocation.getY()) {
            despawn();
            if (fruit != Material.TNT) fruitninja.removeLife();
            return true;
        }
        return false;
    }


    public void despawn() {
        armorStand.die();
    }

    public Location getFruitLocation() {
        return new Location(fruitninja.gameLocation.getWorld(), armorStand.locX - 0.2, armorStand.locY + 1.4, armorStand.locZ - 0.4);
    }

    public static Location ArmorToItem(Location l) {
        l.setX(l.getX() - 0.4);
        l.setY(l.getY() + 1.4);
        l.setZ(l.getZ() - 0.4);
        return l;
    }

    public static Location ItemToArmor(Location l) {
        l.setX(l.getX() + 0.4);
        l.setY(l.getY() - 1.4);
        l.setZ(l.getZ() + 0.4);
        return l;
    }

}
