package ru.whbex.develop.clans.common.misc.text;

import java.util.HashMap;
import java.util.Map;

public class FormattedText extends Text {
    private final int size;
    private Map<Integer, Text> positions;
    public FormattedText(Text base, int size) {
        super(base);
        this.size = size;
        this.positions = new HashMap<>(size);
    }

    public FormattedText addPosition(int pos, Text text){
        if(pos < 0) throw new IllegalArgumentException("Invalid arg position!");
        positions.put(pos, text);
        return this;
    }
    public Text at(int pos){
        if(pos < 0 || pos > positions.size()) return null;
        return positions.get(pos);
    }
    public int getSize(){
        return size;
    }
}
