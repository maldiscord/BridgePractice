package lol.maltest.buildpractice.utils;

import org.bukkit.ChatColor;

import java.lang.reflect.Member;

public class ChatUtil {
    public static String clr(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
