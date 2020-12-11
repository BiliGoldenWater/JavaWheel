package io.github.biligoldenwater.midiplayer.modules;

import org.bukkit.entity.Player;

public class PlayNote {
    public static final int realPiano = 0;
    public static final int MSGSPiano = 1;
    public static final int vanilla = -1;

    public static void playNote(Player player, int note, int strength, int resourcePack){

        switch (resourcePack){
            case vanilla:
                double perStep = 0.03409;
                player.playSound(player.getLocation(),"minecraft:block.note.harp",255, (float) ((note-20) * perStep));
//                double perStep = 0.0227;
//                double[] position = {player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ()};
//                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
//                        "playsound minecraft:block.note.chime record "+ player.getName() +
//                                " "+position[0]+" "+position[1]+" "+position[2]+
//                                " 255 "+ ((note-20) * perStep));
                break;
            case realPiano:
                player.playSound(player.getLocation(),"minecraft:lkrb.piano.p"+note+getStrength(strength),255,1);
//                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
//                        "playsound minecraft:lkrb.piano.p"+note+getStrength(strength)+" record "+ player.getName() +
//                                " "+player.getLocation().getX()+" "+player.getLocation().getY()+" "+player.getLocation().getZ()+
//                                " 255");//+String.valueOf((strength/0x7F)*255));
                break;
            case MSGSPiano:
                player.playSound(player.getLocation(),"minecraft:piano."+note,255,1);
//                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
//                        "playsound minecraft:piano."+note+" record "+ player.getName() +
//                                " "+player.getLocation().getX()+" "+player.getLocation().getY()+" "+player.getLocation().getZ()+
//                                " 255");//+String.valueOf((strength/0x7F)*255));
                break;
        }

    }

    public static String getStrength(int strength){
        switch (strength){
            case 20:
                return "ppp";
            case 40:
                return "pp";
            case 50:
                return "p";
            case 64:
                return "mp";
            case 70:
                return "mf";
            case 80:
                return "f";
            case 90:
                return "ff";
            default:
                return "fff";
        }
    }

    public static void stopNote(Player player, int note, int strength, int resourcePack){
        switch (resourcePack){
            case realPiano:
                player.stopSound("minecraft:lkrb.piano.p"+note+getStrength(strength));
//                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
//                        "stopsound "+player.getName()+" record minecraft:lkrb.piano.p"+note+getStrength(strength));
                break;
            case MSGSPiano:
                player.stopSound("minecraft:piano."+note);
//                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
//                        "stopsound "+player.getName()+" record minecraft:piano."+note);
                break;
        }

    }
}
