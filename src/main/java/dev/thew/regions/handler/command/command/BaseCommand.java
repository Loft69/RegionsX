package dev.thew.regions.handler.command.command;

import dev.thew.regions.handler.region.RegionHandler;
import dev.thew.regions.handler.regionType.RegionTypeHandler;
import dev.thew.regions.model.BreakCause;
import dev.thew.regions.model.Region;
import dev.thew.regions.model.RegionType;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public abstract class BaseCommand {

    private final String sub;
    private final RegionHandler regionHandler;
    private final RegionTypeHandler regionTypeHandler;

    protected BaseCommand(final String sub, final RegionHandler regionHandler, final RegionTypeHandler regionTypeHandler) {
        this.sub = sub;
        this.regionHandler = regionHandler;
        this.regionTypeHandler = regionTypeHandler;
    }

    protected BaseCommand(final String sub, final RegionHandler regionHandler) {
        this(sub, regionHandler, null);
    }

    public abstract String description();

    public abstract void execute(Player player, Region region, String[] args);

    public String getDescription(){
        return description();
    }

    public final void deleteRegion(@NonNull Region region,BreakCause breakCause) {
        regionHandler.deleteRegion(region, breakCause);
    }

    public final List<Region> getPlayerRegions(@NonNull String targetName) {
        return regionHandler.getPlayerRegions(targetName);
    }

    public final RegionType getType(String regionTypeId) {
        return regionTypeHandler.getType(regionTypeId);
    }

    public final NamespacedKey getCustomKey() {
        return regionTypeHandler.getCustomKey();
    }

}
