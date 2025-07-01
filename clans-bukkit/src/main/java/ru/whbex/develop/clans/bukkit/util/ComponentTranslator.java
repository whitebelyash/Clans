package ru.whbex.develop.clans.bukkit.util;

import net.md_5.bungee.api.chat.*;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.misc.text.FormattedText;
import ru.whbex.develop.clans.common.misc.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComponentTranslator {

    public static BaseComponent translate(Text text){
        TextComponent result = new TextComponent(text.isLocalized() ? ClansPlugin.mainLanguage().getPhrase(text.getText()) : text.getText());
        if(text.hasHoverText()){
            result.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(
                    text.isHoverLocalized() ? ClansPlugin.mainLanguage().getPhrase(text.getHoverText()) : text.getHoverText()
            )));
        }
        // TODO: Implement more action types in Text
        if(text.hasClickableText())
            result.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, text.getClickableText()));
        if(text.isRetain())
            result.retain(ComponentBuilder.FormatRetention.FORMATTING);
        return result;
    }
    public static BaseComponent[] translate(Text... objs){
        return Arrays.stream(objs).map(ComponentTranslator::translate).toArray(BaseComponent[]::new);

    }
    public static BaseComponent[] translate(FormattedText text){
        List<BaseComponent> list = new ArrayList<>();
        StringBuilder base = new StringBuilder(text.isLocalized() ? ClansPlugin.mainLanguage().getPhrase(text.getText()) : text.getText());

        for(int i = 0; i < text.getSize(); i++){
            int index = base.indexOf("{" + i + "}");
            if(index > -1){
                // part before the formatted object
                TextComponent part = new TextComponent(base.replace(index, index + 3, "").substring(0, index));
                part.retain(ComponentBuilder.FormatRetention.FORMATTING);
                list.add(part);
                if(text.at(i) == null)
                    continue;
                list.add(translate(text.at(i))); // formatted object itself
                base.replace(0, index, "");
            }
        }
        // Trailing
        if(!base.isEmpty()){
            TextComponent trail = new TextComponent(base.toString());
            trail.retain(ComponentBuilder.FormatRetention.FORMATTING);
            list.add(trail);
        }
        return list.toArray(BaseComponent[]::new);
    }
}
