package com.paxkun;

import com.paxkun.download.Download;
import com.paxkun.download.MangaScraper;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final List<String> SOURCES = List.of(
            "https://hot.planeptune.us/manga/",
            "https://scans-hot.planeptune.us/manga/",
            "https://official.lowee.us/manga/"
            //CLOUDFLARE PROTECTION
            /*"https://scans.lastation.us/manga/"*/
    );

    //private static final String MANGA = "Absolute-Duo";
    private static final int CHAPTER_LIMIT = 9999;
    private static final int PAGE_LIMIT = 9999;

    public static void main(String[] args) {
        String url = MangaScraper.searchManga();
        String MANGA = url.substring(url.lastIndexOf("/", url.length() - 2) + 1);
        Download download = new Download();
        outer:
        for (int j = 20; j < CHAPTER_LIMIT; j++) {
            List<String> imageUrls = new ArrayList<>();
            inner:
            for (int i = 1; i < PAGE_LIMIT; i++) {
                for (String source : SOURCES) {
                    String s = source + MANGA + "/" + String.format("%04d", j) + "-" + String.format("%03d", i) + ".png";
                    System.out.println(s);
                    if (Download.urlExists(s)) {
                        imageUrls.add(s);
                        break;
                    } else if (SOURCES.getLast().equalsIgnoreCase(source) && i == 1) {
                        break outer;
                    } else if (SOURCES.getLast().equalsIgnoreCase(source)) {
                        break inner;
                    }
                }
            }
            download.downloadChapter(imageUrls, MANGA + "-" + String.format("%04d", j) + ".cbz", MANGA + "/");
        }
    }
}
