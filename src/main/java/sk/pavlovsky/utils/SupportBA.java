package sk.pavlovsky.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SupportBA {
    public List<String> townsOfDistrict(String district) {
        String filePath = "villages/obce.csv";
        URL url = getClass().getClassLoader().getResource(filePath);
        if (url == null) {
            throw new RuntimeException("There is no such file: " + filePath);
        }

        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(";");
                if (values.length > 0 && values[1].equals(district)) {
                    result.add(values[0]);
                }
            }
        } catch (IOException e) {
            System.out.println("Vyskytla sa chyba pri čítaní zo súboru: " + e.getMessage());
        }

        return result;
    }

    public static void main(String[] args) {
        SupportBA supportBA = new SupportBA();
        System.out.println(supportBA.townsOfDistrict("Martin"));
    }
}
