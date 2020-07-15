package org.bukkit.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.command.defaults.HelpCommand;
import org.bukkit.command.defaults.PluginsCommand;
import org.bukkit.command.defaults.ReloadCommand;
import org.bukkit.command.defaults.TimingsCommand;
import org.bukkit.command.defaults.VersionCommand;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleCommandMap implements CommandMap {
    protected final Map<String, Command> knownCommands = new HashMap<String, Command>();
    private final Server server;

    public SimpleCommandMap(@NotNull final Server server) {
        this.server = server;
        setDefaultCommands();
    }

    private void setDefaultCommands() {
        register("bukkit", new VersionCommand("version"));
        register("bukkit", new ReloadCommand("reload"));
        register("bukkit", new PluginsCommand("plugins"));
        register("bukkit", new TimingsCommand("timings"));
    }

    public void setFallbackCommands() {
        register("bukkit", new HelpCommand());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerAll(@NotNull String fallbackPrefix, @NotNull List<Command> commands) {
        if (commands != null) {
            for (Command c : commands) {
                register(fallbackPrefix, c);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean register(@NotNull String fallbackPrefix, @NotNull Command command) {
        return register(command.getName(), fallbackPrefix, command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean register(@NotNull String label, @NotNull String fallbackPrefix, @NotNull Command command) {
        label = label.toLowerCase(java.util.Locale.ENGLISH).trim();
        fallbackPrefix = fallbackPrefix.toLowerCase(java.util.Locale.ENGLISH).trim();
        boolean registered = register(label, command, false, fallbackPrefix);

        Iterator<String> iterator = command.getAliases().iterator();
        while (iterator.hasNext()) {
            if (!register(iterator.next(), command, true, fallbackPrefix)) {
                iterator.remove();
            }
        }

        // If we failed to register under the real name, we need to set the command label to the direct address
        if (!registered) {
            command.setLabel(fallbackPrefix + ":" + label);
        }

        // Register to us so further updates of the commands label and aliases are postponed until its reregistered
        command.register(this);

        return registered;
    }

    /**
     * 用给定的可能的名称注册命令.另外使用fallbackPrefix来创建唯一名称.
     * <p>
     * 原文:Registers a command with the given name is possible. Also uses
     * fallbackPrefix to create a unique name.
     *
     * @param label 这个命令的名称，没有“/”前缀
     * @param command 要注册的命令
     * @param isAlias 这个命令是不是以别名执行的
     * @param fallbackPrefix 添加到命令的前缀作为唯一地址
     * @return 如果命令注册成功则为true，false反之
     */
    private synchronized boolean register(@NotNull String label, @NotNull Command command, boolean isAlias, @NotNull String fallbackPrefix) {
        knownCommands.put(fallbackPrefix + ":" + label, command);
        if ((command instanceof VanillaCommand || isAlias) && knownCommands.containsKey(label)) {
            // Request is for an alias/fallback command and it conflicts with
            // a existing command or previous alias ignore it
            // Note: This will mean it gets removed from the commands list of active aliases
            return false;
        }

        boolean registered = true;

        // If the command exists but is an alias we overwrite it, otherwise we return
        Command conflict = knownCommands.get(label);
        if (conflict != null && conflict.getLabel().equals(label)) {
            return false;
        }

        if (!isAlias) {
            command.setLabel(label);
        }
        knownCommands.put(label, command);

        return registered;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean dispatch(@NotNull CommandSender sender, @NotNull String commandLine) throws CommandException {
        String[] args = commandLine.split(" ");

        if (args.length == 0) {
            return false;
        }

        String sentCommandLabel = args[0].toLowerCase(java.util.Locale.ENGLISH);
        Command target = getCommand(sentCommandLabel);

        if (target == null) {
            return false;
        }

        try {
            target.timings.startTiming(); // Spigot
            // Note: we don't return the result of target.execute as thats success / failure, we return handled (true) or not handled (false)
            target.execute(sender, sentCommandLabel, Arrays.copyOfRange(args, 1, args.length));
            target.timings.stopTiming(); // Spigot
        } catch (CommandException ex) {
            target.timings.stopTiming(); // Spigot
            throw ex;
        } catch (Throwable ex) {
            target.timings.stopTiming(); // Spigot
            throw new CommandException("Unhandled exception executing '" + commandLine + "' in " + target, ex);
        }

        // return true as command was handled
        return true;
    }

    @Override
    public synchronized void clearCommands() {
        for (Map.Entry<String, Command> entry : knownCommands.entrySet()) {
            entry.getValue().unregister(this);
        }
        knownCommands.clear();
        setDefaultCommands();
    }

    @Override
    @Nullable
    public Command getCommand(@NotNull String name) {
        Command target = knownCommands.get(name.toLowerCase(java.util.Locale.ENGLISH));
        return target;
    }

    @Override
    @Nullable
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String cmdLine) {
        return tabComplete(sender, cmdLine, null);
    }

    @Override
    @Nullable
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String cmdLine, @Nullable Location location) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(cmdLine, "Command line cannot null");

        int spaceIndex = cmdLine.indexOf(' ');

        if (spaceIndex == -1) {
            ArrayList<String> completions = new ArrayList<String>();
            Map<String, Command> knownCommands = this.knownCommands;

            final String prefix = (sender instanceof Player ? "/" : "");

            for (Map.Entry<String, Command> commandEntry : knownCommands.entrySet()) {
                Command command = commandEntry.getValue();

                if (!command.testPermissionSilent(sender)) {
                    continue;
                }

                String name = commandEntry.getKey(); // Use the alias, not command name

                if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {
                    completions.add(prefix + name);
                }
            }

            Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
            return completions;
        }

        String commandName = cmdLine.substring(0, spaceIndex);
        Command target = getCommand(commandName);

        if (target == null) {
            return null;
        }

        if (!target.testPermissionSilent(sender)) {
            return null;
        }

        String[] args = cmdLine.substring(spaceIndex + 1, cmdLine.length()).split(" ", -1);

        try {
            return target.tabComplete(sender, commandName, args, location);
        } catch (CommandException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, ex);
        }
    }

    @NotNull
    public Collection<Command> getCommands() {
        return Collections.unmodifiableCollection(knownCommands.values());
    }

    public void registerServerAliases() {
        Map<String, String[]> values = server.getCommandAliases();

        for (Map.Entry<String, String[]> entry : values.entrySet()) {
            String alias = entry.getKey();
            if (alias.contains(" ")) {
                server.getLogger().warning("Could not register alias " + alias + " because it contains illegal characters");
                continue;
            }

            String[] commandStrings = entry.getValue();
            List<String> targets = new ArrayList<String>();
            StringBuilder bad = new StringBuilder();

            for (String commandString : commandStrings) {
                String[] commandArgs = commandString.split(" ");
                Command command = getCommand(commandArgs[0]);

                if (command == null) {
                    if (bad.length() > 0) {
                        bad.append(", ");
                    }
                    bad.append(commandString);
                } else {
                    targets.add(commandString);
                }
            }

            if (bad.length() > 0) {
                server.getLogger().warning("Could not register alias " + alias + " because it contains commands that do not exist: " + bad);
                continue;
            }

            // We register these as commands so they have absolute priority.
            if (targets.size() > 0) {
                knownCommands.put(alias.toLowerCase(java.util.Locale.ENGLISH), new FormattedCommandAlias(alias.toLowerCase(java.util.Locale.ENGLISH), targets.toArray(new String[targets.size()])));
            } else {
                knownCommands.remove(alias.toLowerCase(java.util.Locale.ENGLISH));
            }
        }
    }
}