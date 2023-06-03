package sk.pavlovsky.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {
    //    upravit catch
    public HashMap<String, HashMap<String, List<Parish>>> returnHashMap() {
        HashMap<String, HashMap<String, List<Parish>>> finishMap = new HashMap<>();
        List<String> listEparchy;
        String filePlace = "webs";
        URL url = getClass().getClassLoader().getResource(filePlace);
        if (url == null) {
            throw new RuntimeException("There is no such file" + filePlace);
        }
        try (Stream<Path> pathStream = Files.list(Path.of(url.toURI()))) {
            listEparchy = pathStream.map(Path::getFileName).map(Path::toString).toList();
            for (String eparchia : listEparchy) {
                String filePlace1 = "webs/" + eparchia;
                URL url1 = getClass().getClassLoader().getResource(filePlace1);
                if (url1 == null) {
                    throw new RuntimeException("There is no such file " + filePlace1);
                }
                try (Stream<Path> pathStream1 = Files.list(Path.of(url1.toURI()))) {
                    List<String> listOfDistricts = pathStream1.map(Path::getFileName).map(Path::toString).toList();
                    HashMap<String, List<Parish>> listHashMap = new HashMap<>();
                    for (String district : listOfDistricts) {
                        listHashMap.put(district, returnListOfParishes(eparchia, district));
                    }
                    finishMap.put(eparchia, listHashMap);
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
            return finishMap;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Parish> returnListOfParishes(String eparchia, String dekanat) throws IOException, URISyntaxException {
        String filePlace = "webs/" + eparchia + "/" + dekanat;
        URL url = getClass().getClassLoader().getResource(filePlace);
        List<String> listOfParishesHtmls = Files.list(Path.of(url.toURI())).map(Path::getFileName).map(Path::toString).toList();
        List<Parish> listOfParishes = new ArrayList<>();
        for (String listOfParishesHtml : listOfParishesHtmls) {
            Parish parish = null;
            String html = listOfParishesHtml.replaceAll(".html", "");
            if (eparchia.equals("PO")) {
                parish = parseInformationPo(eparchia, dekanat, html);
            } else if (eparchia.equals("KE")) {
                parish = parseInfoKe(eparchia, dekanat, html);
            }
            listOfParishes.add(parish);
        }
        return listOfParishes;
    }

    public Parish parseInformationPo(String eparchia, String dekanat, String nameOfVillage) {
        Parish parish = new Parish();
        String filePath = "webs/" + eparchia + "/" + dekanat + "/" + nameOfVillage + ".html";
        URL url = getClass().getClassLoader().getResource(filePath);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            String htmls = content.toString();
            String[] split = StringUtils.splitByWholeSeparator(htmls, "<strong>");
            Optional<String> filialkyOptional = Stream.of(split).filter(it -> it.contains("Filiálk")).findFirst();
//            Meno Farnosti
            Optional<String> nameOfFarnost = Stream.of(split).filter(it -> it.contains("h1")).findFirst();
//            Meno Spravcu
            Optional<String> nameOfFarar = Stream.of(split).filter(it -> it.contains("Farár")).findFirst();
            Optional<String> nameOfSpravca = Stream.of(split).filter(it -> it.contains("Správca farnosti")).findFirst();
            Optional<String> nameOfAdministrator = Stream.of(split).filter(it -> it.contains("Administrator")).findFirst();
            if (nameOfFarar.isPresent()) {
                String nameOfFarar2 = getName(nameOfFarar, ".*</strong>");
                parish.setNameofSpravca(nameOfFarar2);
                parish.setFunctionOfAdministrator(2);
            } else if (nameOfSpravca.isPresent()) {
                String nameOfSpravca2 = getName(nameOfSpravca, ".*</strong>");
                parish.setNameofSpravca(nameOfSpravca2);
                parish.setFunctionOfAdministrator(1);
            }
            String nameOfParish2 = getName(nameOfFarnost, ".*<span[^>]*>");
            parish.setNameOfVillage(nameOfParish2);
//            Filialky
            if (filialkyOptional.isPresent()) {
                String s2 = getString(filialkyOptional, "<span[^>]*>[^>]*>", "<[^>]+>", "\n");
                String s3 = s2.replaceAll(" - .*", "");
                String s4 = s3.replaceAll("\\d+\\. ", "");
                String s5 = s4.replaceAll("[^\n]*&nbsp;", "");
                List<String> collect = Arrays.stream(s5.split("\n")).map(StringUtils::strip).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                parish.setFilialky((ArrayList<String>) collect);
            }
//            Jurisdikcne uzemie
            Optional<String> jurisdictionPlacesOptional = Stream.of(split).filter(it -> it.contains("Jurisdikčné územie")).findFirst();
            if (jurisdictionPlacesOptional.isPresent()) {
                String jurisdictionPlaces3 = getString(jurisdictionPlacesOptional, "<[^>]+>", "\\d+", "");
                String jurisdictionPlaces4 = jurisdictionPlaces3.replaceAll("[^\n]*&nbsp;", "");
                List<String> collect = getCollect(jurisdictionPlaces4);
                parish.setJurisdikcneUzemie((ArrayList<String>) collect);
            }
//            kaplani
            Optional<String> kaplanOptional = Stream.of(split).filter(it -> it.contains("Kaplán")).findFirst();
            if (kaplanOptional.isPresent()) {
                String kaplan3 = getString(kaplanOptional, " <[^>]+>", "<[^>]+>", "");
                List<String> collect = getCollect(kaplan3);
                parish.setKaplani((ArrayList<String>) collect);
            }
//            vypomocny duchovny
            Optional<String> vypomocnyDuchovnyOptional = Stream.of(split).filter(it -> it.contains("Výpomocní duchovní")).findFirst();
            if (vypomocnyDuchovnyOptional.isPresent()) {
                String vypomocnyDuchovny3 = getString(vypomocnyDuchovnyOptional, " <[^>]+>", "<[^>]+>", "");
                List<String> collect = getCollect(vypomocnyDuchovny3);
                parish.setVypomocnyDuchovny((ArrayList<String>) collect);
            }
        } catch (IOException e) {
            System.out.println("Vyskytla sa chyba pri čítaní zo súboru: " + e.getMessage());
        }
        return parish;
    }

    private static String getName(Optional<String> nameOfSpravca, String regex) {
        String nameOfSpravcaa = nameOfSpravca.get();
        String nameOfSpravca1 = nameOfSpravcaa.replaceAll(regex, "");
        return nameOfSpravca1.replaceAll("<[^>]+>", "").trim();
    }

    private static List<String> getCollect(String jurisdictionPlaces4) {
        return Arrays.stream(jurisdictionPlaces4.split("\\,")).map(StringUtils::strip).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
    }

    private static String getString(Optional<String> kaplanOptional, String regex, String regex1, String replacement) {
        String kaplan = kaplanOptional.get();
        String kaplan1 = kaplan.replaceAll(".*</strong>", "");
        String kaplan2 = kaplan1.replaceAll(regex, "");
        return kaplan2.replaceAll(regex1, replacement);
    }

    public Parish parseInfoKe(String eparchia, String dekanat, String nameOfVillage) {
        Parish parish = new Parish();
        String filePath = "webs/" + eparchia + "/" + dekanat + "/" + nameOfVillage + ".html";
        URL url = getClass().getClassLoader().getResource(filePath);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
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
            String nameOfFilialky1 = nameOfFilialky.replaceAll("<[^>]*>", "");
            List<String> collect = Arrays.stream(nameOfFilialky1.split("\n")).collect(Collectors.toList());
            parish.setFilialky((ArrayList<String>) collect);
//          Funkcia knaza
            Elements nameOfFunctionElements = doc.select("div.widget.clearfix");
            Element needPartoOfHtml = nameOfFunctionElements.get(1);
            String htmls = needPartoOfHtml.toString();
            Document docu = Jsoup.parse(htmls);
            Elements nameOfPriests = docu.select("div.entry-title h4 a");
            Elements function = docu.select("div.entry-c li.color");
            for (int i = 0; i < function.size(); i++) {
                String nameOfFunction = function.get(i).text();
                if (nameOfFunction.contains("farár")) {
                    parish.setNameofSpravca(nameOfPriests.get(i).text());
                } else if (nameOfFunction.contains("výpomocný duchovný")) {
                    parish.getVypomocnyDuchovny().add(nameOfPriests.get(i).text());
                } else if (nameOfFunction.contains("duchovný správca")) {
                    parish.getDuchovnySpravca().add(nameOfPriests.get(i).text());
                } else if (nameOfFunction.contains("kaplán")) {
                    parish.getKaplani().add(nameOfPriests.get(i).text());
                } else if (nameOfFunction.contains("na odpočinku")) {
                    parish.getNaOdpocinku().add(nameOfPriests.get(i).text());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return parish;
    }

    public void setInformation() {
//        String jsonFilePath = "JsonView/out.json";
        String jsonFilePaths = "src/main/resources/JsonView/out.json";
//        URL url = getClass().getClassLoader().getResource(jsonFilePath);
//        if (url==null) {
//            throw new RuntimeException("There is no such file "+jsonFilePath);
//        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode;
            try {
                rootNode = objectMapper.readTree(new File(jsonFilePaths));

                HashMap<String, HashMap<String, List<Parish>>> mapOfInformation = returnHashMap();
                for (String keyAllMap : mapOfInformation.keySet()) {
                    HashMap<String, List<Parish>> partOfMap = mapOfInformation.get(keyAllMap);
                    for (int i = 0; i <= mapOfInformation.size(); i++) {
                        JsonNode nodeNames = rootNode.get(i);
                        JsonNode eparchy = nodeNames.get("eparchia");
                        String eparchie = eparchy.asText();
                        if (eparchie.equals(keyAllMap)) {
                            JsonNode json = rootNode.get(i);
                            JsonNode dekanaty = json.get("dekanaty");
                            for (String keyOfDekanat : partOfMap.keySet()) {
                                for (JsonNode dekanat : dekanaty) {
                                    JsonNode dekanatNameNode = dekanat.get("dekanat");
                                    String dekanatName = dekanatNameNode.asText();
                                    if (dekanatName.equals(keyOfDekanat)) {
                                        JsonNode farnosti = dekanat.get("farnosti");
                                        List<Parish> listOfParish = partOfMap.get(keyOfDekanat);
                                        for (Parish parish : listOfParish) {
                                            for (JsonNode farnost : farnosti) {
                                                String farnostName = farnost.get("farnost").asText();
                                                String meno = parish.getNameOfVillage().trim();
                                                if (farnostName.equals(meno)) {
                                                    String nameofSpravca = parish.getNameofSpravca();
                                                    if (nameofSpravca != null) {
                                                        ((ObjectNode) farnost).put("Farar", nameofSpravca.trim());
                                                    }
                                                    ((ObjectNode) farnost).put("Kaplani", createArray(parish.getKaplani()));
                                                    ((ObjectNode) farnost).put("Výpomocný duchovný", createArray(parish.getVypomocnyDuchovny()));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                objectMapper.writeValue(new File(jsonFilePaths), rootNode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayNode createArray(ArrayList<String> array) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (String arr : array) {
            arrayNode.add(arr);
        }
        return arrayNode;
    }

    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        parser.setInformation();
    }
}
