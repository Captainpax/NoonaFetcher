package com.paxkun;

import com.paxkun.download.Download;
import com.paxkun.old.Server;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final List<String> SOURCES = List.of(
            "https://hot.planeptune.us/manga/",
            "https://scans-hot.planeptune.us/manga/"
            //CLOUDFLARE PROTECTION
            /*"https://scans.lastation.us/manga/"*/
    );

    private static final String MANGA = "One-Piece";
    private static final int CHAPTER_LIMIT = 9999;
    private static final int PAGE_LIMIT = 9999;

    public static void main(String[] args) {
        Download download = new Download();
        for (int j = 1; j < CHAPTER_LIMIT; j++) {
            List<String> imageUrls = new ArrayList<>();
            outer:
            for (int i = 1; i < PAGE_LIMIT; i++) {
                for (String source : SOURCES) {
                    String s = source + MANGA + "/" + String.format("%04d", j) + "-" + String.format("%03d", i) + ".png";
                    System.out.println(s);
                    if (Download.urlExists(s)) {
                        imageUrls.add(s);
                        break;
                    } else if (SOURCES.getLast().equalsIgnoreCase(source)) {
                        break outer;
                    }
                }
            }
            download.downloadChapter(imageUrls, MANGA + "-" + String.format("%04d", j) + ".cbz", MANGA + "/");
        }
    }
}
