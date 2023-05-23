package sk.pavlovsky.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
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

    public static void main(String[] args) {
        ArrayList<String> dekanat = new ArrayList<>();
        dekanat.add("CM.lst");
        dekanat.add("BJ.lst");
        dekanat.add("GI.lst");
        dekanat.add("HA.lst");
        dekanat.add("HR.lst");
        dekanat.add("HU.lst");
        dekanat.add("ML.lst");
        dekanat.add("OR.lst");
        dekanat.add("PO.lst");
        dekanat.add("PP.lst");
        dekanat.add("SB.lst");
        dekanat.add("SK.lst");
        dekanat.add("SL.lst");
        dekanat.add("SN.lst");
        dekanat.add("SP.lst");
        dekanat.add("VT.lst");
        String nameofDekanat;
        for( int i = 0; i < dekanat.size(); i++) {
            nameofDekanat = dekanat.get(getNum());
            setNum(1);
            String filePlace = "src/main/resources/PO/" + nameofDekanat;
            try (BufferedReader reader = new BufferedReader(new FileReader(filePlace))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ", 2);
                    if (parts.length >= 2) {
                        String url = parts[0];
                        String fileName = parts[1] + ".html";
                        String saveDirectory = "src/main/resources/webs/"+nameofDekanat;
                        try {
                            Document doc = Jsoup.connect(url).get();
                            Path directoryPath = Paths.get(saveDirectory);
                            if (!Files.exists(directoryPath)) {
                                Files.createDirectories(directoryPath);
                            }

                            String sanitizedFileName = fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
                            Path filePath = Paths.get(saveDirectory, sanitizedFileName);
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath.toString()), StandardCharsets.UTF_8));
                            writer.write(doc.html());
                            writer.close();

                            System.out.println("Stránka bola úspešne stiahnutá a uložená: " + url);
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
}
