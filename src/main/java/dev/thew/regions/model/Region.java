package dev.thew.regions.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Region {
    final String id;

    final Location baseLocation;
    final RegionType regionType;
    @Setter
    String owner;
    @Setter
    Hologram hologram;
    int endurance;
    final List<String> members;
    boolean holoHidden;

    public JsonObject toJson() {
        JsonObject regionJSON = new JsonObject();

        regionJSON.addProperty("regionTypeId", regionType.id());
        regionJSON.addProperty("owner", owner);
        regionJSON.addProperty("endurance", endurance);
        regionJSON.addProperty("holoHidden", holoHidden);
        regionJSON.addProperty("id", id);

        JsonArray membersArray = new JsonArray();
        for (String member : members) membersArray.add(member);
        regionJSON.add("members", membersArray);

        return regionJSON;
    }

    public boolean isInside(Location location) {

        if (baseLocation.getWorld() != location.getWorld()) return false;

        int cx = baseLocation.getBlockX();
        int cy = baseLocation.getBlockY();
        int cz = baseLocation.getBlockZ();

        int radius = regionType.radius();

        int startX = cx - radius;
        int startY = cy - radius;
        int startZ = cz - radius;

        int endX = cx + radius;
        int endY = cy + radius;
        int endZ = cz + radius;

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return startX <= x && x <= endX && startY <= y && y <= endY && startZ <= z && z <= endZ;
    }

    public boolean canHide(@NonNull Player player) {
        WhoHide whoHide = regionType.whoHide();
        if (whoHide == null) return false;

        String nickname = player.getName();
        if (whoHide == WhoHide.OWNER && isOwner(nickname)) return true;
        else return whoHide == WhoHide.MEMBERS && (isMember(nickname) || isOwner(nickname));
    }

    public boolean hideEnable(){
        return regionType.canHide();
    }

    public Location getMinLocation() {
        int radius = regionType.radius();
        return baseLocation.clone().add(-radius, -radius, -radius);
    }

    public Location getMaxLocation() {
        int radius = regionType.radius();
        return baseLocation.clone().add(radius, radius, radius);
    }

    public boolean haveEndurance() {
        return endurance > 0;
    }

    public boolean isOwner(String playerName) {
        return owner.equals(playerName);
    }

    public boolean isMemberOrOwner(String playerName) {
        return isOwner(playerName) || members.contains(playerName);
    }

    public boolean isMember(String playerName) {
        return members.contains(playerName);
    }

    public void addMember(String playerName) {
        members.add(playerName);
    }

    public void removeMember(String playerName) {
        members.remove(playerName);
    }

    public String renderSize() {
        return regionType.render();
    }

    public String getX(){
        return baseLocation.getBlockX() + "";
    }

    public String getY(){
        return baseLocation.getBlockY() + "";
    }

    public String getZ(){
        return baseLocation.getBlockZ() + "";
    }

    public void decreaseEndurance(int damage) {
        endurance -= damage;
    }

    public boolean isCrossing(@NonNull Selection selection) {

        Location min = getMinLocation();
        Location max = getMaxLocation();

        Location min2 = selection.getMin();
        Location max2 = selection.getMax();

        boolean xOverlap = min.getX() <= max2.getX() && max.getX() >= min2.getX();
        boolean yOverlap = min.getY() <= max2.getY() && max.getY() >= min2.getY();
        boolean zOverlap = min.getZ() <= max2.getZ() && max.getZ() >= min2.getZ();

        return xOverlap && yOverlap && zOverlap;
    }

    public HologramModel getHologramModel(){
        return regionType.hologramModel();
    }

    public void showHologram() {
        HologramModel hologramModel = getHologramModel();
        hologramModel.reload(this);
    }

    public void hideHologram() {
        HologramModel hologramModel = getHologramModel();
        hologramModel.hide(this);
    }

    public void reloadHologram(){
        HologramModel hologramModel = getHologramModel();
        hologramModel.reload(this);
    }

    public HashMap<EntityType, List<Entity>> getEntityGroupedByType() {
        List<Entity> entities = getEntityInRegion();
        HashMap<EntityType, List<Entity>> mobsGroupedByType = new HashMap<>();

        entities.forEach(entity -> mobsGroupedByType.computeIfAbsent(entity.getType(), k -> new ArrayList<>()).add(entity));

        return mobsGroupedByType;
    }

    public List<Entity> getEntityInRegion() {
        World world = baseLocation.getWorld();
        assert world != null;

        Location minLocation = getMinLocation();
        Location maxLocation = getMaxLocation();
        BoundingBox regionBox = new BoundingBox(minLocation.getX(), minLocation.getY(), minLocation.getZ(), maxLocation.getX(), maxLocation.getY(), maxLocation.getZ());

        return world.getNearbyEntities(regionBox).stream()
                .filter(entity -> isInside(entity.getLocation()))
                .toList();
    }

}
