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

public class GetPoWeb {
    private static int num = 0;

    public static int getNum() {
        return num;
    }

    public static void setNum(int num) {
        GetPoWeb.num = getNum() + num;
    }
    public void downloadPoWebs(){
        ArrayList<String> district = new ArrayList<>();
        district.add("CM");district.add("BJ");district.add("GI");district.add("HA");district.add("HR");district.add("HU");
        district.add("ML");district.add("OR");district.add("PO");district.add("PP");district.add("SB");district.add("SK");
        district.add("SL");district.add("SN");district.add("SP");district.add("VT");
        String nameofDekanat;
        for( int i = 0; i < district.size(); i++) {
            nameofDekanat = district.get(getNum());
            setNum(1);
            String filePlace = "PO/" + nameofDekanat+".lst";
            URL url = getClass().getClassLoader().getResource(filePlace);
            if (url == null) {
                throw new RuntimeException("There is no such file: " + filePlace);}
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ", 2);
                    if (parts.length >= 2) {
                        String web = parts[0];
                        String fileName = parts[1] + ".html";
                        String saveDirectory = "src/main/resources/webs/PO/"+nameofDekanat;
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
    }

    public static void main(String[] args) {
        GetPoWeb getPoWeb = new GetPoWeb();
        getPoWeb.downloadPoWebs();
    }
}
