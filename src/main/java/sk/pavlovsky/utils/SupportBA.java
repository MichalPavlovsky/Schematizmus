package sk.pavlovsky.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SupportBA {
    public static List<String> townsOfDistrict(String district) {
        String filePlace = "src/main/resources/villages/obce.csv";
        List<String> result = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePlace))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(";");
                for (String value : values) {
                    if (value.equals(district)) {
                        result.add(values[0]);
                    }}}
        } catch (IOException e) {
            System.out.println("Vyskytla sa chyba pri čítaní zo súboru: " + e.getMessage());
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(townsOfDistrict("Martin"));
    }
}