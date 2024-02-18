package me.leminhbao.mitiergen.utils;

public class ChatColor {
    public static final ChatColor RESET = new ChatColor("\u001B[0m");
    public static final ChatColor BLACK = new ChatColor("\u001B[30m");
    public static final ChatColor RED = new ChatColor("\u001B[31m");
    public static final ChatColor GREEN = new ChatColor("\u001B[32m");
    public static final ChatColor YELLOW = new ChatColor("\u001B[33m");
    public static final ChatColor BLUE = new ChatColor("\u001B[34m");
    public static final ChatColor PURPLE = new ChatColor("\u001B[35m");
    public static final ChatColor AQUA = new ChatColor("\u001B[36m");
    public static final ChatColor WHITE = new ChatColor("\u001B[37m");

    private String color;

    public ChatColor(String color) {
        this.color = color;
    }

    public static String stripColor(String message) {
        if (message == null) {
            return null;
        }

        return message.replaceAll("\u001B\\[.*?m", "");
    }

    public String toString() {
        return this.color;
    }
}
