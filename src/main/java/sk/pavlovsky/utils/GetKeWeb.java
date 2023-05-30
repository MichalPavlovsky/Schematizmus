package sk.pavlovsky.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GetKeWeb {
    private static int num = 0;

    public static int getNum() {
        return num;
    }

    public static void setNum(int num) {
        GetKeWeb.num = getNum() + num;
    }
    public void downloadKeWebs(){
        ArrayList<String> district = new ArrayList<>();
        district.add("Košice");district.add("Maďarský");district.add("Michalovce");
        district.add("Sečovce");district.add("Sobrance");district.add("Spišská Nová Ves");
        district.add("Trebišov");
        String nameofDekanat;
        for( int i = 0; i < district.size(); i++) {
            nameofDekanat = district.get(getNum());
            setNum(1);
        String filePlace = "KE/"+nameofDekanat+".txt";
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
                    String savePlace = "src/main/resources/webs/KE/"+nameofDekanat;
                    try {
                        Document doc = Jsoup.connect(web).get();
                        Path directoryPath = Paths.get(String.valueOf(savePlace));
                        if (!Files.exists(directoryPath)) {
                            Files.createDirectories(directoryPath);
                        }
                        Path filePath = Paths.get(String.valueOf(savePlace), fileName);
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
    }}

    public static void main(String[] args) {
        GetKeWeb getKeWeb = new GetKeWeb();
        getKeWeb.downloadKeWebs();
    }
}


