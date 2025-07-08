package dev.thew.regions.handler.command.command;

import dev.thew.regions.handler.regionType.RegionTypeHandler;
import dev.thew.regions.model.Region;
import dev.thew.regions.utils.Message;
import lombok.NonNull;
import dev.thew.regions.Regions;
import dev.thew.regions.handler.command.command.subcommands.AddSubCommand;
import dev.thew.regions.handler.command.command.subcommands.AdminSubCommand;
import dev.thew.regions.handler.command.command.subcommands.ListCommand;
import dev.thew.regions.handler.command.command.subcommands.RemoveSubCommand;
import dev.thew.regions.handler.region.RegionHandler;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class BaseCommandExecutor implements TabExecutor {

    private final Map<String, BaseCommand> subCommands = new HashMap<>();
    private final RegionHandler regionHandler;

    public BaseCommandExecutor(RegionHandler regionHandler, RegionTypeHandler regionTypeHandler) {
        this.regionHandler = regionHandler;

        addSub(
                new AddSubCommand(regionHandler),
                new RemoveSubCommand(regionHandler),
                new ListCommand(regionHandler),
                new AdminSubCommand(regionHandler, regionTypeHandler)
        );
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender,@NonNull Command command,@NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            sendHelpMessage(player);
            return true;
        }

        String subCommandLabel = args[0].toLowerCase();
        BaseCommand baseCommand = this.subCommands.getOrDefault(subCommandLabel, null);
        if (baseCommand == null) {
            Regions.sendError(player, Message.COMMAND_NOT_FOUND);
            return true;
        }

        Region region = regionHandler.getRegion(player.getLocation());

        String[] arguments = new String[args.length - 1];
        System.arraycopy(args, 1, arguments, 0, args.length - 1);

        baseCommand.execute(player, region, arguments);
        return true;
    }

    private void sendHelpMessage(Player player) {

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(" ").append("\n");
        messageBuilder.append(" §fДоступные команды §x§c§9§f§c§6§0↓§f").append("\n");
        messageBuilder.append(" ").append("\n");

        for (Map.Entry<String, BaseCommand> entry : subCommands.entrySet()) {
            if (entry.getValue().getDescription() == null) continue;
            messageBuilder.append("§x§c§9§f§c§6§0").append(entry.getValue().getDescription()).append("\n");
        }

        messageBuilder.append(" ").append("\n");
        player.sendMessage(messageBuilder.toString());
    }

    private void addSub(BaseCommand... baseCommand) {
        for (BaseCommand command : baseCommand) subCommands.put(command.getSub(), command);
    }

    private List<String> getSubCommands() {
        List<String> arguments = new ArrayList<>(subCommands.keySet().stream().toList());
        arguments.add("help");

        return arguments;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender commandSender,@NonNull Command command,@NonNull String s, String[] args) {
        if (args.length == 1) return getSubCommands();
        return null;
    }
}
