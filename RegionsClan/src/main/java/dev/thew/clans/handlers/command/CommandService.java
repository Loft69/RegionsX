package dev.thew.clans.handlers.command;

import dev.thew.clans.handlers.HandlerService;
import dev.thew.clans.handlers.clan.ClanHandler;
import dev.thew.clans.handlers.clan.ClanService;
import dev.thew.clans.handlers.message.Message;
import dev.thew.clans.handlers.message.Reason;
import dev.thew.clans.model.Access;
import dev.thew.clans.model.Clan;
import dev.thew.clans.model.Result;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandService implements CommandHandler, TabExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    @Override
    public void load() {

    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            sendHelpMessage(player);
            return true;
        }

        String subCommandLabel = args[0].toLowerCase();
        SubCommand subCommand = this.subCommands.getOrDefault(subCommandLabel, null);
        if (subCommand == null) {
            Message notFound = new Message(Reason.COMMAND_NOT_FOUND);
            notFound.execute(player, null);
            return true;
        }

        String[] arguments = new String[args.length - 1];
        System.arraycopy(args, 1, arguments, 0, args.length - 1);

        ClanHandler clanHandler = HandlerService.getHandler(ClanService.class);
        Clan clan = clanHandler.getClan(player);

        if (clan == null && subCommand.isClanRequired()) {
            // TODO message u are not in the clan
            return true;
        }

        if (clan != null) {
            String playerName = player.getName();

            Access access = clan.getAccess(playerName);

            boolean isAccess = subCommand.accessContains(access);
            if (!isAccess){

                // TODO message
                return true;
            }
        }

        Message message = subCommand.execute(player, clan, arguments);
        message.execute(player, clan, arguments);
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

        List<String> arguments = new ArrayList<>(subCommands.keySet().stream().toList());
        arguments.add("help");

        return arguments;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender commandSender,@NonNull Command command,@NonNull String s, String[] args) {
        if (args.length == 1) return getSubCommands();
        return null;
    }

    @Override
    public void shutdown() {

    }
}
