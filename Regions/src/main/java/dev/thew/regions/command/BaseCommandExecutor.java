package dev.thew.regions.command;

import dev.thew.regions.handler.HandlerService;
import dev.thew.regions.handler.service.RegionService;
import lombok.NonNull;
import dev.thew.regions.Regions;
import dev.thew.regions.command.subCommands.AddSubCommand;
import dev.thew.regions.command.subCommands.AdminSubCommand;
import dev.thew.regions.command.subCommands.ListCommand;
import dev.thew.regions.command.subCommands.RemoveSubCommand;
import dev.thew.regions.handler.RegionHandler;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseCommandExecutor implements TabExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public BaseCommandExecutor() {
        subCommands.put("add", new AddSubCommand());
        subCommands.put("remove", new RemoveSubCommand());
        subCommands.put("list", new ListCommand());
        subCommands.put("admin", new AdminSubCommand());
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender,@NonNull Command command,@NonNull String label, String @NonNull [] args) {

        if (sender instanceof ConsoleCommandSender && args[0].equalsIgnoreCase("removeall")){
            RegionHandler regionsService = HandlerService.getHandler(RegionService.class);
            regionsService.clearRegions();
            HolographicDisplaysAPI.get(Regions.getInstance()).deleteHolograms();
            return true;
        }

        if (!(sender instanceof Player player)) return true;

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            sendHelpMessage(player);
            return true;
        }

        String subCommandLabel = args[0].toLowerCase();
        SubCommand subCommand = this.subCommands.getOrDefault(subCommandLabel, null);
        if (subCommand == null) {
            Regions.sendError(player, "Команда не найдена. Используйте §7/base help");
            return true;
        }

        String[] arguments = new String[args.length - 1];
        System.arraycopy(args, 1, arguments, 0, args.length - 1);

        subCommand.execute(player, arguments);
        return true;
    }

    private void sendHelpMessage(Player player) {

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(" ").append("\n");
        messageBuilder.append(" §fДоступные команды §x§c§9§f§c§6§0↓§f").append("\n");
        messageBuilder.append(" ").append("\n");

        for (Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
            if (entry.getValue().getDescription() == null) continue;
            messageBuilder.append("§x§c§9§f§c§6§0").append(entry.getValue().getDescription()).append("\n");
        }

        messageBuilder.append(" ").append("\n");
        player.sendMessage(messageBuilder.toString());
    }

    private List<String> getSubCommands() {

        List<String> arguments = new ArrayList<>();

        for (Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
            arguments.add(entry.getKey());
        }

        arguments.add("help");

        return arguments;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender commandSender,@NonNull Command command,@NonNull String s, String[] args) {
        if (args.length == 1) return getSubCommands();
        return null;
    }
}
