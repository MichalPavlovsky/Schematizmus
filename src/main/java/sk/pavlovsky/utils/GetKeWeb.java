package sk.pavlovsky.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetKeWeb {
    public void downloadKeWebs(){
        String filePlace = "KE/KE.txt";
        URL url = getClass().getClassLoader().getResource(filePlace);
        if (url == null) {
            throw new RuntimeException("There is no such file: " + filePlace);}
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\*", 2);
                if (parts.length >= 2) {
                    String web = parts[1];
                    String fileName = parts[0] + ".html";
                    String saveDirectory = "src/main/resources/webs/KE";
                    try {
                        Document doc = Jsoup.connect(web).get();
                        Path directoryPath = Paths.get(saveDirectory);
                        if (!Files.exists(directoryPath)) {
                            Files.createDirectories(directoryPath);
                        }
                        Path filePath = Paths.get(saveDirectory, fileName);
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath.toString()), StandardCharsets.UTF_8));
                        writer.write(doc.html());
                        writer.close();

                        System.out.println("Stránka bola úspešne stiahnutá a uložená: " + web);
                    } catch (IOException e) {
                        System.out.println("Vyskytla sa chyba pri stahovaní a ukladaní stránky: " + url);
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Vyskytla sa chyba pri čítaní zo súboru: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        GetKeWeb getKeWeb = new GetKeWeb();
        getKeWeb.downloadKeWebs();
    }
    }

