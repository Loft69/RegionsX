package dev.thew.clans.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.*;

@RequiredArgsConstructor
@Data
public class Clan {

    private Location regionLocation;
    private Location homeLocation;
    private String owner;
    private String name;
    private final Set<String> managers = new HashSet<>();
    private final Set<String> members = new HashSet<>();
    private long timeStampSetHome = 0L;

    public Location getLocation() {
        return regionLocation.clone();
    }

    public Location getHomeLocation() {
        return homeLocation.clone();
    }

    public Result contains(String playerName){
        return (isManager(playerName) == Result.SUCCESS
                || isMember(playerName) == Result.SUCCESS
                || isOwner(playerName) == Result.SUCCESS)

                ? Result.SUCCESS : Result.FAILED;
    }

    public Result isOwner(String playerName) {
        return playerName.equalsIgnoreCase(owner) ? Result.SUCCESS : Result.FAILED;
    }

    public Result isMember(String playerName) {
        return members.contains(playerName) ? Result.SUCCESS : Result.FAILED;
    }

    public Result isManager(String playerName) {
        return managers.contains(playerName) ? Result.SUCCESS : Result.FAILED;
    }

    public Result addManager(String playerName) {
        if (managers.contains(playerName)) return Result.FAILED;
        managers.add(playerName);
        return Result.SUCCESS;
    }

    public Access getAccess(String playerName) {
        if (isOwner(playerName) == Result.SUCCESS) return Access.OWNER;
        if (isManager(playerName) == Result.SUCCESS) return Access.MANAGER;

        return Access.MEMBER;
    }

    public Result kick(String playerName) {
        if (playerName.equalsIgnoreCase(owner)) return Result.FAILED;

        managers.remove(playerName);
        members.remove(playerName);

        return Result.SUCCESS;
    }

    public Result removeManager(String playerName) {
        if (!managers.contains(playerName)) return Result.FAILED;
        managers.remove(playerName);
        return Result.SUCCESS;
    }

    public Result addMember(String playerName) {
        if (members.contains(playerName)) return Result.FAILED;
        members.add(playerName);
        return Result.SUCCESS;
    }

    public Result removeMember(String playerName) {
        if (!members.contains(playerName)) return Result.FAILED;
        members.remove(playerName);
        return Result.SUCCESS;
    }

    public Result setHomeLocation(@NonNull Location location) {
        long current = System.currentTimeMillis();
        long cooldown = 60 * 30 * 1000L;

        if (current + cooldown > timeStampSetHome){
            homeLocation = location.clone();
            timeStampSetHome = current + cooldown;

            return Result.SUCCESS;
        }

        return Result.FAILED;
    }

    public List<String> getMembers(){
        return members.stream().toList();
    }

    public List<String> getManagers(){
        return managers.stream().toList();
    }

}
