package ru.whbex.develop.clans.common.misc.text;


// Single text block with hover/click actions
public class Text {
    private final String text;
    private final boolean translate;
    private String hover = null;
    private boolean hoverTranslate = false;
    private String click = null;
    //private boolean clickTranslate = false;
    public Text(String text){
        this.text = text;
        this.translate = false;
    }
    public Text(String key, boolean translate){
        this.text = key;
        this.translate = translate;
    }
    public Text(Text other){
        this.text = other.text;
        this.translate = other.translate;
        this.hover = other.hover;
        this.click = other.click;
        this.hoverTranslate = other.hoverTranslate;
    }
    public Text hover(String message, boolean translate){
        this.hover = message;
        this.hoverTranslate = translate;
        return this;
    }
    public Text click(String message){
        this.click = message;
        //this.clickTranslate = translate;
        return this;
    }
    public String getText() {
        return text;
    }
    public boolean hasHoverText(){
        return hover != null;
    }
    public boolean hasClickableText(){
        return click != null;
    }
    public String getHoverText(){
        return hover;
    }
    public String getClickableText(){
        return click;
    }

    public boolean isHoverLocalized(){
        return hoverTranslate;
    }
    public boolean isLocalized(){
        return translate;
    }
}
