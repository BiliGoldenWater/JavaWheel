//<dependency>
//    <groupId>com.alibaba</groupId>
//    <artifactId>fastjson</artifactId>
//    <version>1.2.47</version>
//</dependency>
package io.github.biligoldenwater.midiplayer.modules;

import com.alibaba.fastjson.JSONObject;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonMessage {
    private final HashMap<String,JsonMessageSingle> message = new HashMap<>();
    private final List<String> texts = new ArrayList<>();

    public JsonMessage(String text){
        message.put(text, new JsonMessageSingle(text));
        texts.add(text);
    }

    public JsonMessage(){
    }

    public JsonMessageSingle addText(String text){
        message.put(text, new JsonMessageSingle(text));
        texts.add(text);
        return message.get(text);
    }

    public JsonMessageSingle getText(String text){
        return message.get(text);
    }

    public List<JSONObject> getJsonArray(){
        List<JSONObject> output = new ArrayList<>();

        for (String textName : texts){
            output.add(message.get(textName).getJSONObject());
        }

        return output;
    }

    public String getJsonText(){
        List<String> output = new ArrayList<>();

        for (String textName : texts){
            output.add(message.get(textName).getStringOutput());
        }

        return output.toString();
    }

    public String getCommand(CommandSender sender){
        return "tellraw "+sender.getName()+" "+this.getJsonText();
    }

    public void sendTo(CommandSender sender){
        if(sender instanceof Player){
            ((CraftPlayer) sender).getHandle().sendMessage(IChatBaseComponent.ChatSerializer.a(this.getJsonText()));
        } else {
            sender.getServer().dispatchCommand(sender.getServer().getConsoleSender(),this.getCommand(sender));
        }
    }

    public static class JsonMessageSingle {
        private final JSONObject text = new JSONObject();

        public JsonMessageSingle(String text){
            if(text == null)text = "";
            this.text.put("text",text);
        }

        public JsonMessageSingle(){

        }

        public String getStringOutput(){
            return text.toJSONString();
        }

        public JSONObject getJSONObject(){
            return text;
        }

        public JsonMessageSingle setColor(String color){
            this.text.put("color",color);
            return this;
        }

        public JsonMessageSingle setBold(boolean bold){
            this.text.put("bold",bold);
            return this;
        }

        public JsonMessageSingle setItalic(boolean italic){
            this.text.put("italic",italic);
            return this;
        }

        public JsonMessageSingle setUnderlined(boolean underlined){
            this.text.put("underlined",underlined);
            return this;
        }

        public JsonMessageSingle setStrikethrough(boolean strikethrough){
            this.text.put("strikethrough",strikethrough);
            return this;
        }

        public JsonMessageSingle setObfuscated(boolean obfuscated){
            this.text.put("obfuscated",obfuscated);
            return this;
        }

        public JsonMessageSingle setHoverEvent(String action_type, Object value){
            JsonMessageSingle json = new JsonMessageSingle();
            json.text.put("action",action_type);
            json.text.put("value",value);
            this.text.put("hoverEvent",json.text);
            return this;
        }

        public JsonMessageSingle setClickEvent(String action_type, Object value){
            JsonMessageSingle json = new JsonMessageSingle();
            json.text.put("action",action_type);
            json.text.put("value",value);
            this.text.put("clickEvent",json.text);
            return this;
        }
    }

    public static class clickEvent {
        public static class action {
            public static final String open_url = "open_url";
            public static final String run_command = "run_command";
            public static final String suggest_command = "suggest_command";
            public static final String change_page = "change_page";
        }
    }

    public static class hoverEvent {
        public static class action {
            public static final String show_text = "show_text";
            public static final String show_item = "show_item";
        }
    }
}

