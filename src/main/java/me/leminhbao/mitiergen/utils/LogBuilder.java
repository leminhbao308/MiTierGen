package me.leminhbao.mitiergen.utils;

import lombok.Getter;
import lombok.Setter;
import me.leminhbao.mitiergen.MiTierGen;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Getter
@Setter
public class LogBuilder {
    private final Logger logger;
    private final List<String> lines;

    public LogBuilder(MiTierGen plugin) {
        this.logger = plugin.getLogger();
        this.lines = new ArrayList<>();
    }

    public LogBuilder add(String message, ChatColor color) {
        lines.add(color + message + ChatColor.RESET);
        return this;
    }

    public LogBuilder add(String message) {
        return add(message, ChatColor.WHITE);
    }

    public LogBuilder addNewLine() {
        return add("");
    }


    public LogBuilder addSeparator() {
        return add("--------------------------------------------------");
    }

    public LogBuilder addSeparator(ChatColor color) {
        return add("--------------------------------------------------", color);
    }

    public LogBuilder addCentered(String message, ChatColor color) {
        int messageLength = ChatColor.stripColor(message).length();
        int prefixLength = ChatColor.stripColor(logger.getName()).length();

        int totalLength = Math.max(messageLength, prefixLength);

        int padding = Math.max(0, (totalLength - messageLength) / 2);

        String paddedMessage = String.format("%s%s%s",
                " ".repeat(Math.max(0, prefixLength - padding)),
                message,
                " ".repeat(padding));

        return add(paddedMessage, color);
    }

    public LogBuilder addCentered(String message) {
        return addCentered(message, ChatColor.WHITE);
    }

    public LogBuilder addList(List<String> list) {
        lines.addAll(list);
        return this;
    }

    public LogBuilder clear() {
        lines.clear();
        return this;
    }

    public LogBuilder replace(String placeholder, String replacement) {
        lines.replaceAll(s -> s.replace(placeholder, replacement));
        return this;
    }

    public LogBuilder replace(String placeholder, String replacement, ChatColor color) {
        String coloredString = color + replacement;
        lines.replaceAll(s -> s.replace(placeholder, coloredString));
        return this;
    }

    public void build() {
        for (String line : lines) {
            logger.info(line);
        }
    }

    public void finalAdd(String message) {
        add(message);
        build();
    }

    public void finalAdd(String message, ChatColor color) {
        add(message, color);
        build();
    }
}
