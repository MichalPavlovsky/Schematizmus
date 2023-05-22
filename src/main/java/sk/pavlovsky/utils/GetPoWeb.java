package main.java.sk.pavlovsky.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetPoWeb {
        public static void main(String[] args) {
            String url = "http://grkatpo.sk/?schematizmus&show=farnost&id=3";
            String saveDirectory = "src/main/resources/htmls";
            try {
                Document doc = Jsoup.connect(url).get();

                Path directoryPath = Paths.get(saveDirectory);
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }

                String fileName = url.replaceAll("[^a-zA-Z0-9-_\\.]", "_") + ".html";
                Path filePath = Paths.get(saveDirectory,fileName);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath.toString()), StandardCharsets.UTF_8));
                writer.write(doc.html());
                writer.close();

                System.out.println("Stránka bola úspešne stiahnutá a uložená.");
            } catch (IOException e) {
                System.out.println("Vyskytla sa chyba pri stahovaní a ukladaní stránky: " + e.getMessage());
            }
        }
    }

