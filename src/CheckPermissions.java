package indi.goldenwater.binditem.module;

import org.bukkit.command.CommandSender;

public final class CheckPermissions {
    public static boolean hasPermission_Tips(CommandSender sender, String perm,String messageOnNoPerm){
        boolean hasPerm;
        hasPerm = sender.hasPermission(perm);
        if(!hasPerm){
            sender.sendMessage(messageOnNoPerm.replaceAll("%perm",perm));
        }
        return hasPerm;
    }
    public static boolean hasPermission_Tips(CommandSender sender, String perm){
        boolean hasPerm;
        hasPerm = sender.hasPermission(perm);
        if(!hasPerm){
            sender.sendMessage("§cYou don't have permission "+perm+".");
        }
        return hasPerm;
    }
}
