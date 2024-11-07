package dev.thew.regions.command.subCommands;

import dev.thew.regions.command.SubCommand;
import dev.thew.regions.handler.HandlerService;
import dev.thew.regions.handler.RegionTypeHandler;
import dev.thew.regions.handler.service.RegionService;
import dev.thew.regions.handler.service.RegionTypeService;
import dev.thew.regions.model.BreakCause;
import dev.thew.regions.model.Region;
import dev.thew.regions.handler.RegionHandler;
import dev.thew.regions.model.RegionType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class AdminSubCommand extends SubCommand {
    @Override
    public String description() {
        return null;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("regions.admin")) return;

        if (args.length == 1 && args[0].equalsIgnoreCase("remove")){
            Location playerLocation = player.getLocation();

            RegionHandler regionsService = HandlerService.getHandler(RegionService.class);

            Region region = regionsService.getRegion(playerLocation);

            if (region == null){
                player.sendMessage(" §cВы должны стоять в регионе!");
                return;
            }

            World world = player.getWorld();
            world.getBlockAt(region.getBaseLocation().clone()).setType(Material.AIR);

            regionsService.deleteRegion(region, BreakCause.ADMIN_COMMAND);
            player.sendMessage(" Приват игрока " + region.getOwner() + " успешно удален!");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("list")){

            String regionTypeId = args[1];
            RegionTypeHandler regionTypeHandler = HandlerService.getHandler(RegionTypeService.class);

            RegionType regionType = regionTypeHandler.getType(regionTypeId);
            if (regionType == null) {
                player.sendMessage(" RegionType не найден!");
                return;
            }

            player.getInventory().addItem(regionType.getItemStack(regionTypeHandler.getCustomKey()));
        }
    }
}
