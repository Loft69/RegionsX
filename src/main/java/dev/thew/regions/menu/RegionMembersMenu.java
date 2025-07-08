package dev.thew.regions.menu;

import lombok.AccessLevel;
import lombok.Getter;
import dev.thew.regions.Regions;
import dev.thew.regions.model.Region;
import dev.thew.regions.utils.InventoryUtils;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegionMembersMenu extends StaticMenu {
    Region region;

    public RegionMembersMenu(Region region) {
        super(27, "Участники региона " + region.getOwner());
        this.region = region;

        render();
    }

    private void render() {

        getInventory().clear();

        ItemStack itemStack = InventoryUtils.createItem(Material.WITHER_SKELETON_SKULL, "§fВладелец §x§c§9§f§c§6§0" + region.getOwner(), null);
        setItem(0, itemStack, null);

        List<String> lore = new ArrayList<>();

        lore.add(" §fскм §f→ §7Передать владение");
        lore.add(" §fпкм §f→ §7Исключить из привата");

        int index = 1;
        for (String member : region.getMembers()) {
            ItemStack item = InventoryUtils.createItem(Material.PLAYER_HEAD, "§fУчастник §x§c§9§f§c§6§0" + member, lore);
            setItem(index++, item, event -> {

                Player player = (Player) event.getWhoClicked();

                if (!region.isOwner(player.getName())) {
                    Regions.sendError(player, "Вы не являетесь владельцем привата");
                    return;
                }

                if (event.isRightClick()) {

                    region.removeMember(member);

                    player.sendMessage(" Игрок §x§c§9§f§c§6§0" + member + "§f исключен из привата");
                    render();
                } else if (event.getClick() == ClickType.MIDDLE) {

                    region.removeMember(member);
                    region.setOwner(member);
                    region.addMember(player.getName());

                    region.reloadHologram();

                    player.sendMessage(" Приват передан игроку §x§c§9§f§c§6§0" + member);
                    render();
                }
            });
        }

        itemStack = InventoryUtils.createItem(Material.GOLD_NUGGET, "§fДобавить участника §e+", null);
        setItem(index, itemStack, event -> {

            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            player.sendMessage(" §fЧтобы добавить участника в регион, используйте команду §x§c§9§f§c§6§0/base add §7ник");

        });

        ItemStack item = InventoryUtils.createItem(Material.FLOWER_BANNER_PATTERN, "§fНазад §e→", null);
        setItem(26, item, event -> {

            Player player = (Player) event.getWhoClicked();

            RegionMenu regionMenu = new RegionMenu(region);
            regionMenu.open(player);
        });
    }
}
