package com.paxkun.download;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Objects;
import java.util.Scanner;

public class MangaScraper {

    private static final WebDriver driver = new ChromeDriver();

    public static void main(String[] args) {
        //searchManga();
    }

    @NotNull
    public static String searchManga() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter manga: ");
        String userManga = scanner.nextLine();
        String encodedManga = java.net.URLEncoder.encode(userManga, java.nio.charset.StandardCharsets.UTF_8);
        String searchUrl = "https://weebcentral.com/search/?text=" + encodedManga + "&sort=Best+Match&order=Ascending&official=Any&anime=Any&adult=Any&display_mode=Full+Display";

        driver.get(searchUrl);

        try {
            Thread.sleep(2000); // Wait for the page to load
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Document doc = Jsoup.parse(Objects.requireNonNull(driver.getPageSource()));
        Elements mangaResults = doc.select("a.link.link-hover, a.line-clamp-1.link.link-hover");

        if (mangaResults.isEmpty()) {
            System.out.println("Manga not found, please try a different search.");
            return "";
        }

        // Display manga results
        for (int i = 0; i < mangaResults.size(); i++) {
            Element manga = mangaResults.get(i);
            System.out.println((i + 1) + ". " + manga.text());
        }

        // Ask user to select manga
        System.out.print("Select a manga from the given list: ");
        int selectedMangaIndex = scanner.nextInt() - 1;

        if (selectedMangaIndex >= 0 && selectedMangaIndex < mangaResults.size()) {
            Element selectedManga = mangaResults.get(selectedMangaIndex);
            System.out.println("You have selected: " + selectedManga.text());

            System.out.print("Would you like to add this manga to your library? Y/N: ");
            scanner.nextLine(); // Consume newline
            String answer = scanner.nextLine();

            if ("Y".equalsIgnoreCase(answer)) {
                return selectedManga.absUrl("href");
            }
        } else {
            System.out.println("Invalid index, please choose a correct one.");
        }
        return "";
    }
}

