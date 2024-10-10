package ru.whbex.develop.clans.common.misc;

import java.util.Collections;
import java.util.List;

// TODO: move to a common lib
public class PagedListView<E> {
    private final static short PAGE_SIZE = 10;
    private final List<E> list;
    public PagedListView(List<E> list){
        this.list = list;
    }
    public int size(){
        return list.size();
    }
    public int pageAmount(){
        if(list.isEmpty())
            return 0;
        return list.size() % PAGE_SIZE != 0 ? list.size() / PAGE_SIZE + 1 : list.size() / PAGE_SIZE;
    }
    public List<E> page(int pageIndex){
        if(list.isEmpty())
            return Collections.emptyList();
        int pageAmount = pageAmount();
        if(pageIndex < 1 || pageIndex > pageAmount)
            throw new ArrayIndexOutOfBoundsException("Page index " + pageIndex + "out of bounds");
        // start index
       // int si = (pageIndex - 1 == 0 ? 1 : (pageIndex - 1) * PAGE_SIZE) - 1; wtf lol
        int si = (pageIndex - 1) * PAGE_SIZE;
        // end index
       // int ei = (si + PAGE_SIZE) - pageIndex == pageAmount ? (PAGE_SIZE * pageAmount() - size()) : 0;
        int ei = si + PAGE_SIZE;
        // check for last page fullness
        if(pageIndex == pageAmount && size() != pageAmount * PAGE_SIZE)
            ei = ei - (pageAmount * PAGE_SIZE - size());
        return list.subList(si, ei);
    }
}
