package sk.pavlovsky.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.stream.Stream;

public class GetPoWeb {

    public void downloadPoWebs() {
        String fileNames = "PO";
        URL urls = getClass().getClassLoader().getResource(fileNames);
        if (urls == null) {
            throw new RuntimeException("There is no such: " + fileNames);
        }
        try (Stream<Path> pathStream = Files.list(Path.of(urls.toURI()))) {
            List<String> districts = pathStream.map(Path::getFileName).map(Path::toString).toList();
            for (String district : districts) {
                String filePlace = "PO/" + district;
                URL url = getClass().getClassLoader().getResource(filePlace);
                if (url == null) {
                    throw new RuntimeException("There is no such file: " + filePlace);
                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(" ", 2);
                        if (parts.length >= 2) {
                            String web = parts[0];
                            String fileName = parts[1] + ".html";
                            String updateName = district.replaceAll(".lst", "");
                            String saveDirectory = "src/main/resources/webs/PO/" + updateName;
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
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        GetPoWeb getPoWeb = new GetPoWeb();
        getPoWeb.downloadPoWebs();
    }
}
