package dev.thew.regions.menu;

import lombok.AccessLevel;
import lombok.Getter;
import dev.thew.regions.model.Region;
import dev.thew.regions.handler.visual.VisualizationService;
import dev.thew.regions.utils.InventoryUtils;
import dev.thew.regions.utils.TextUtils;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegionMenu extends StaticMenu {
    Region region;
    
    public RegionMenu(Region region) {
        super(27, "Регион игрока " + region.getOwner());
        this.region = region;

        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(" §x§c§4§c§4§c§4Владелец §7→ §x§c§9§f§c§6§0" + region.getOwner());
        lore.add(" §x§c§4§c§4§c§4Размер §7→ " + region.renderSize());
        lore.add(" §x§c§4§c§4§c§4Координаты §7→ §f" + TextUtils.renderLocation(region.getBaseLocation()));
        lore.add(" §x§c§4§c§4§c§4Кол-во участников §7→ §x§c§9§f§c§6§0" + region.getMembers().size());
        lore.add(" §x§c§4§c§4§c§4Голограмма §7→ " + (region.isHoloHidden() ? "§cСкрыта" : "§x§c§9§f§c§6§0Активна"));
        lore.add("");

        ItemStack item = InventoryUtils.createItem(Material.ARMOR_STAND, "§x§f§2§e§c§7§aИнформация", lore);
        setItem(11, item, null);

        item = InventoryUtils.createItem(Material.PAINTING, "§x§9§e§e§7§e§cУчастники", null);
        setItem(13, item, event -> {

            Player player = (Player) event.getWhoClicked();

            RegionMembersMenu regionMembersMenu = new RegionMembersMenu(region);
            regionMembersMenu.open(player);
        });

        item = InventoryUtils.createItem(Material.CAMPFIRE, "§x§e§7§f§a§6§5Просмотр границ", null);
        setItem(15, item, event -> {

            Player player = (Player) event.getWhoClicked();
            VisualizationService.renderCorners(player, region);

        });


        if (region.hideEnable()) {
            item = InventoryUtils.createItem(Material.BARRIER, "§x§e§7§f§a§6§5" + (region.isHoloHidden() ? "Показать голограмму" : "Скрыть голограмму"), null);
            setItem(16, item, event -> {
                Player player = (Player) event.getWhoClicked();

                boolean canUse = region.canHide(player);
                if (!canUse){
                    player.closeInventory();
                    player.sendMessage(" §cВы не можете этого делать!");
                    return;
                }

                if (region.isHoloHidden()) region.showHologram();
                else region.hideHologram();

                RegionMenu regionMenu = new RegionMenu(region);
                regionMenu.open(player);
            });
        }

    }
}
