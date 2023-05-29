package sk.pavlovsky.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {
    public void parseInformationPo(String dekanat, String nameOfVillage) {
        Parish parish = new Parish();
        String filePath = "src/main/resources/webs/"+dekanat+".lst/"+nameOfVillage+".html";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            String htmls = content.toString();
            String[] split = StringUtils.splitByWholeSeparator(htmls, "<strong>");
            Optional<String> filiálkyOptional = Stream.of(split).filter(it -> it.contains("Filiálk")).findFirst();
//            Meno Farnosti
            Optional<String> nameOfFarnost = Stream.of(split).filter(it -> it.contains("h1")).findFirst();
//            Meno Spravcu
            Optional<String> nameOfFarar = Stream.of(split).filter(it -> it.contains("Farár")).findFirst();
            Optional<String> nameOfSpravca = Stream.of(split).filter(it -> it.contains("Správca farnosti")).findFirst();
            Optional<String> nameOfAdministrator = Stream.of(split).filter(it -> it.contains("Administrator")).findFirst();
            if (nameOfFarar.isPresent()) {
                String nameOfFararr = nameOfFarar.get();
                String nameOfFarar1 = nameOfFararr.replaceAll(".*</strong>","");
                String nameOfFarar2 = nameOfFarar1.replaceAll("<[^>]+>","");
                parish.setNameofSpravca(nameOfFarar2);
                parish.setFunctionOfAdministrator(2);
            } else if (nameOfSpravca.isPresent()) {
                String nameOfSpravcaa = nameOfSpravca.get();
                String nameOfSpravca1 = nameOfSpravcaa.replaceAll(".*</strong>","");
                String nameOfSpravca2 = nameOfSpravca1.replaceAll("<[^>]+>","");
                parish.setNameofSpravca(nameOfSpravca2);
                parish.setFunctionOfAdministrator(1);
            }
            String nameOfParish = nameOfFarnost.get();
            String nameOfParish1 = nameOfParish.replaceAll(".*<span[^>]*>","");
            String nameOfParish2 = nameOfParish1.replaceAll("<[^>]+>","");
            parish.setNameOfVillage(nameOfParish2);
//            Filialky
            if (filiálkyOptional.isPresent()) {
                String filiaky = filiálkyOptional.get();
                String s = filiaky.replaceAll(".*</strong>", "");
                String s1 = s.replaceAll("<span[^>]*>[^>]*>","");
                String s2 = s1.replaceAll("<[^>]+>", "\n");
                String s3 = s2.replaceAll(" - .*", "");
                String s4 = s3.replaceAll("\\d+\\. ", "");
                String s5 = s4.replaceAll("[^\n]*&nbsp;", "");
                List<String> collect = Arrays.stream(s5.split("\n")).map(StringUtils::strip).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                parish.setFilialky((ArrayList<String>) collect);

            }
//            Jurisdikcne uzemie
            Optional<String> jurisdictionPlacesOptional = Stream.of(split).filter(it -> it.contains("Jurisdikčné územie")).findFirst();
            if (jurisdictionPlacesOptional.isPresent()) {
                String jurisdictionPlaces = jurisdictionPlacesOptional.get();
                String jurisdictionPlaces1= jurisdictionPlaces.replaceAll(".*</strong>","");
                String jurisdictionPlaces2= jurisdictionPlaces1.replaceAll("<[^>]+>","");
                String jurisdictionPlaces3= jurisdictionPlaces2.replaceAll("\\d+","");
                String jurisdictionPlaces4= jurisdictionPlaces3.replaceAll("[^\n]*&nbsp;","");
                List<String> collect = Arrays.stream(jurisdictionPlaces4.split("\\,")).map(StringUtils::strip).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                parish.setJurisdikcneUzemie((ArrayList<String>) collect);
                System.out.println(collect);
            }
//            kaplani
            Optional<String> kaplanOptional = Stream.of(split).filter(it -> it.contains("Kaplán")).findFirst();
            if (kaplanOptional.isPresent()) {
                String kaplan = kaplanOptional.get();
                String kaplan1 = kaplan.replaceAll(".*</strong>", "");
                String kaplan2 = kaplan1.replaceAll(" <[^>]+>","");
                String kaplan3 = kaplan2.replaceAll("<[^>]+>","");
                List<String> collect = Arrays.stream(kaplan3.split("\\,")).map(StringUtils::strip).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                parish.setKaplani((ArrayList<String>) collect);
                System.out.println(collect);
            }
//            vypomocny duchovny
            Optional<String> vypomocnyDuchovnyOptional = Stream.of(split).filter(it -> it.contains("Výpomocní duchovní")).findFirst();
            if (vypomocnyDuchovnyOptional.isPresent()) {
                String vypomocnyDuchovny = vypomocnyDuchovnyOptional.get();
                String vypomocnyDuchovny1 = vypomocnyDuchovny.replaceAll(".*</strong>","");
                String vypomocnyDuchovny2 = vypomocnyDuchovny1.replaceAll(" <[^>]+>","");
                String vypomocnyDuchovny3 = vypomocnyDuchovny2.replaceAll("<[^>]+>","");
                List<String> collect = Arrays.stream(vypomocnyDuchovny3.split("\\,")).map(StringUtils::strip).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                parish.setVypomocnyDuchovny((ArrayList<String>) collect);
                System.out.println(collect);
            }
        } catch (IOException e) {
            System.out.println("Vyskytla sa chyba pri čítaní zo súboru: " + e.getMessage());
        }
    }

    public void parseInfoKe(String nameOfVillage){
        Parish parish = new Parish();
        String filePath = "webs/KE/"+nameOfVillage+".html";
        URL url = getClass().getClassLoader().getResource(filePath);
        try {BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
            String html = content.toString();
            Document doc = Jsoup.parse(html);
//          Meno farnosti
            Elements nameOfParishElements = doc.select("div.container.clearfix h1");
            String nameOfParish = nameOfParishElements.text();
            String nameOfParish1 = nameOfParish.replaceAll("Farnosť ", "");
            parish.setNameOfVillage(nameOfParish1);
//          Meno knaza
            Elements nameofAdministratorElements = doc.select("div.entry-title h4 a");
            String nameofAdministrator = nameofAdministratorElements.get(0).text();
            parish.setNameofSpravca(nameofAdministrator);
//          Filialky
            Elements nameofFilialkyElements = doc.select("div#post-list-footer h4");
            String nameOfFilialky = nameofFilialkyElements.toString();
            String nameOfFilialky1 = nameOfFilialky.replaceAll("<[^>]*>","");
            List<String> collect = Arrays.stream(nameOfFilialky1.split("\n")).collect(Collectors.toList());
//          Funkcia knaza
            Elements nameOfFunctionElements = doc.select("div.widget.clearfix");
            Element needPartoOfHtml = nameOfFunctionElements.get(1);
            String htmls = needPartoOfHtml.toString();
            Document docu = Jsoup.parse(htmls);
            Elements nameOfPriests = docu.select("div.entry-title h4 a");
            Elements function = docu.select("div.entry-c li.color");
            for (int i=0;  i< function.size(); i++){
                String nameOfFunction = function.get(i).text();
                if (nameOfFunction.contains("farár")) {
                    parish.setNameofSpravca(nameOfPriests.get(i).text());
                }else if (nameOfFunction.contains("výpomocný duchovný")) {
                    parish.getVypomocnyDuchovny().add(nameOfPriests.get(i).text());
                }else if (nameOfFunction.contains("duchovný správca")) {
                    parish.getDuchovnySpravca().add(nameOfPriests.get(i).text());
                }else if (nameOfFunction.contains("kaplán")) {
                    parish.getKaplani().add(nameOfPriests.get(i).text());
                }else if (nameOfFunction.contains("na odpočinku")) {
                    parish.getNaOdpocinku().add(nameOfPriests.get(i).text());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public void setNewInformation() {
        String jsonFilePath = "src/main/resources/JSONview/out.json";
        try {ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));
            JsonNode json = rootNode.get(0);
            JsonNode dekanaty = json.get("dekanaty");
            for (JsonNode dekanat : dekanaty) {
                JsonNode farnosti = dekanat.get("farnosti");
                for (JsonNode farnost : farnosti) {
                    String farnostName = farnost.get("farnost").asText();
                    if (farnostName.equals("Bačkov")) {
                        String farar = "Anton Vesely"; // Tu zadajte meno farára
                        ((ObjectNode) farnost).put("farar", farar);
                    }
                }
            }
            objectMapper.writeValue(new File(jsonFilePath), rootNode);

            System.out.println("Informácie o farárovi v farnosti boli úspešne doplnené.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Parish parish = new Parish();
        parish.parseInfoKe("Košice-Západ");
//        parish.parseInformationPo("SK","Hrabovčík");
//        parish.setNewInformation();


    }
}
