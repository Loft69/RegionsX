package dev.thew.regions.handler.command.command.subcommands;

import dev.thew.regions.handler.command.command.BaseCommand;
import dev.thew.regions.handler.regionType.RegionTypeHandler;
import dev.thew.regions.model.BreakCause;
import dev.thew.regions.model.Region;
import dev.thew.regions.handler.region.RegionHandler;
import dev.thew.regions.model.RegionType;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class AdminSubCommand extends BaseCommand {
    public AdminSubCommand(RegionHandler regionHandler, RegionTypeHandler regionTypeHandler) {
        super("admin", regionHandler, regionTypeHandler);
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public void execute(@NonNull Player player, Region region, String[] args) {
        if (!player.hasPermission("regions.admin")) return;

        if (args.length == 1 && args[0].equalsIgnoreCase("remove")){
            if (region == null){
                player.sendMessage(" §cВы должны стоять в регионе!");
                return;
            }

            World world = player.getWorld();
            world.getBlockAt(region.getBaseLocation().clone()).setType(Material.AIR);

            deleteRegion(region, BreakCause.ADMIN_COMMAND);
            player.sendMessage(" Приват игрока " + region.getOwner() + " успешно удален!");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("list")){

            String regionTypeId = args[1];

            RegionType regionType = getType(regionTypeId);
            if (regionType == null) {
                player.sendMessage(" RegionType не найден!");
                return;
            }

            player.getInventory().addItem(regionType.getItemStack(getCustomKey()));
        }
    }
}
