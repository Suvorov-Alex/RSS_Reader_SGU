package com.rssreader.app.alex.rss_reader_sgu.unused;

import com.rssreader.app.alex.rss_reader_sgu.model.Article;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 09.08.2017.
 */

public class DisplayedList {
    public static Map<Long, Boolean> displayedList = new HashMap<>();

    public static void loadDataToList(List<Article> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!displayedList.containsKey(list.get(i).guid)) {
                displayedList.put(list.get(i).guid, false);
            }
        }
    }
}
