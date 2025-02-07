package com.paxkun;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles searching URLs for downloadable files of a specified type.
 */
public class SearchAPI {

    private static final Set<String> downloadList = new HashSet<>();

    /**
     * Searches for files of the specified type at the given URL.
     *
     * @param url      The target website URL.
     * @param fileType The type of files to search for (e.g., ".pdf").
     * @return A set containing the found file links.
     */
    public static Set<String> searchFiles(String url, String fileType) {
        downloadList.clear(); // Clear previous results
        try {
            StatusAPI.updateLog("🔍 Searching for files in: " + url);

            // Connect to the URL and parse its HTML
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href$=" + fileType + "]");

            // Extract and store valid file links
            for (Element link : links) {
                String href = link.absUrl("href");
                if (!href.isEmpty() && downloadList.add(href)) {
                    StatusAPI.updateLog("📄 Found: " + href);
                }
            }

            if (downloadList.isEmpty()) {
                StatusAPI.updateLog("⚠️ No matching files found.");
            } else {
                StatusAPI.updateLog("✅ Search complete. " + downloadList.size() + " files found.");
            }
        } catch (IOException e) {
            StatusAPI.updateLog("❌ Error during search: " + e.getMessage());
        }
        return new HashSet<>(downloadList);
    }
}
