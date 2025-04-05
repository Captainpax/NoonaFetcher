package com.paxkun;

import com.paxkun.download.Download;
import com.paxkun.download.MangaScraper;
import com.paxkun.download.SourceFinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    private static final int PAGE_LIMIT = 9999;

    public static void main(String[] args) throws IOException {
        Map<String, String> data = MangaScraper.searchManga();
        if (data.isEmpty()) return;
        String href = data.get("href");
        String url = href.substring(0, href.lastIndexOf("/"));
        String manga = href.substring(href.lastIndexOf("/") + 1);
        Map<Integer, String> chapters = MangaScraper.chapterLinks(url + "/rss");
        //System.out.println(chapters);
        Download download = new Download();
        chapters.forEach((k, v) -> {
            List<String> imageUrls = new ArrayList<>();
            String SOURCE_URL = SourceFinder.findSource(v);
            if (SOURCE_URL.isEmpty()) return;
            for (int i = 1; i < PAGE_LIMIT; i++) {
                String s = SOURCE_URL + manga + "/" + String.format("%04d", k) + "-" + String.format("%03d", i) + ".png";
                if (Download.urlExists(s)) {
                    imageUrls.add(s);
                } else {
                    break;
                }
            }
            download.downloadChapter(imageUrls, manga + "-" + String.format("%04d", k) + ".cbz", "downloads/" + data.get("title") + "/");
        });
    }
}
