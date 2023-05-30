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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {
    public HashMap<String, HashMap<String,List<Parish>>> returnHashMap(){
        HashMap<String,List<Parish>> mapOfDistrictPo = new HashMap<>();
        HashMap<String,List<Parish>> mapOfDistrictKe = new HashMap<>();
        List<String> listOfEparchy = new ArrayList<>();
        listOfEparchy.add("PO");listOfEparchy.add("KE");
        List<String> listOfDistrictsPo = new ArrayList<>();
        List<String> listOfDistrictsKe = new ArrayList<>();
        listOfDistrictsPo.add("BJ");listOfDistrictsPo.add("CM");listOfDistrictsPo.add("GI");listOfDistrictsPo.add("HA");listOfDistrictsPo.add("HR");
        listOfDistrictsPo.add("HU");listOfDistrictsPo.add("ML");listOfDistrictsPo.add("OR");listOfDistrictsPo.add("PO");listOfDistrictsPo.add("PP");
        listOfDistrictsPo.add("SB");listOfDistrictsPo.add("SK");listOfDistrictsPo.add("SL");listOfDistrictsPo.add("SN");listOfDistrictsPo.add("SP");
        listOfDistrictsPo.add("VT");listOfDistrictsKe.add("Košice");listOfDistrictsKe.add("Maďarský");listOfDistrictsKe.add("Michalovce");
        listOfDistrictsKe.add("Sečovce");listOfDistrictsKe.add("Sobrance");listOfDistrictsKe.add("Spišská Nová Ves");listOfDistrictsKe.add("Trebišov");
        for (String eparchia : listOfEparchy) {
            List<String> listOfDistrict = null;
            HashMap<String, List<Parish>> listHashMap = null;
            if (eparchia.equals("PO")) {
                listOfDistrict = listOfDistrictsPo;
                listHashMap = mapOfDistrictPo;
            } else if (eparchia.equals("KE")) {
                listOfDistrict = listOfDistrictsKe;
                listHashMap = mapOfDistrictKe;
            }
            for (String district : listOfDistrict) {
                listHashMap.put(district, returnListOfParishes(eparchia, district));
            }
        }
        HashMap<String, HashMap<String,List<Parish>>> finishMap = new HashMap<>();
        finishMap.put("PO",mapOfDistrictPo);
        finishMap.put("KE",mapOfDistrictKe);
        return finishMap;
    }



    public List<String> nameOfHtmlToList(String eparchia, String dekanat){
        List<String> listOfParishesHtml = new ArrayList<>();
        String filePlace = "PO/" + dekanat + ".lst";
        if (eparchia.equals("PO")) {
            filePlace = "PO/" + dekanat + ".lst";
        } else if (eparchia.equals("KE")) {
            filePlace = "KE/" + dekanat + ".txt";}
        URL url = getClass().getClassLoader().getResource(filePlace);
        if (url == null) {
            throw new RuntimeException("There is no such file: " + filePlace);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String regex=null;
                if(eparchia.equals("PO")){
                     regex = " ";
                }else if (eparchia.equals("KE")) {
                     regex = "\\*";
                }
                String[] parts = line.split(regex, 2);
                String fileName = "";
                if (parts.length >= 2) {
                    if (eparchia.equals("PO")) {
                        fileName = parts[1];
                    } else if (eparchia.equals("KE")) {
                    fileName = parts[0];
                }
                    listOfParishesHtml.add(fileName);
                }
            }
        } catch (IOException e) {
            System.out.println("Vyskytla sa chyba pri čítaní zo súboru: " + e.getMessage());
        }
            return listOfParishesHtml;
}

    public List<Parish> returnListOfParishes(String eparchia, String dekanat) {
        List<String> listOfParishesHtml = nameOfHtmlToList(eparchia,dekanat);
        List<Parish> listOfParishes = new ArrayList<>();
        if (eparchia.equals("PO")) {
            for (int i = 0; i < listOfParishesHtml.size(); i++) {
                Parish parish = parseInformationPo(eparchia,dekanat,listOfParishesHtml.get(i));
                listOfParishes.add(parish);
            }
        }else if (eparchia.equals("KE")){
            for (int i = 0; i < listOfParishesHtml.size(); i++) {
                Parish parish = parseInfoKe(eparchia,dekanat,listOfParishesHtml.get(i));
                listOfParishes.add(parish);
        }
    }
        return listOfParishes;
    }

    public Parish parseInformationPo(String eparchia, String dekanat, String nameOfVillage) {
        Parish parish = new Parish();
        String filePath = "webs/"+eparchia+"/"+dekanat+"/"+nameOfVillage+".html";
        URL url = getClass().getClassLoader().getResource(filePath);
        try {BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
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
            }
        } catch (IOException e) {
            System.out.println("Vyskytla sa chyba pri čítaní zo súboru: " + e.getMessage());
        }return parish;
    }

    public Parish parseInfoKe(String eparchia,String dekanat, String nameOfVillage){
        Parish parish = new Parish();
        String filePath = "webs/"+eparchia+"/"+dekanat+"/"+nameOfVillage+".html";
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
            parish.setFilialky((ArrayList<String>) collect);
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
        }return parish;
    }
    public void setNewInformation() {
        // Cesta k JSON súboru
        String jsonFilePath = "src/main/resources/JSONview/out.json";

        try {
            // Vytvorenie ObjectMapperu
            ObjectMapper objectMapper = new ObjectMapper();

            // Načítanie JSON súboru
            JsonNode rootNode = null;
            try {
                rootNode = objectMapper.readTree(new File(jsonFilePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Prístup k prvemu objektu vo vnútri poľa
            JsonNode json = rootNode.get(0);
            System.out.println("json: "+json);

            // Doplnenie informácií o farárovi do farnosti "Andrejová"
            JsonNode dekanaty = json.get("dekanaty");
            System.out.println("deka: "+dekanaty);
            for (JsonNode dekanat : dekanaty) {
                JsonNode farnosti = dekanat.get("farnosti");
                System.out.println("far: "+farnosti);
                for (JsonNode farnost : farnosti) {
                    JsonNode farnostName = farnost.get("farnost");
                    System.out.println("farnostName: "+farnostName);
                    String farnostNames = farnost.get("farnost").asText();
                    System.out.println("farnostNames: "+farnostNames);
                    if (farnostName.equals("Bačkov")) {
                        String farar = "Anton Vesely"; // Tu zadajte meno farára

                        // Doplnenie informácie o farárovi
                        ((ObjectNode) farnost).put("farar", farar);
                    }
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        Parser parser = new Parser();
        System.out.println(parser.nameOfHtmlToList("KE","Košice"));
        System.out.println(parser.returnListOfParishes("PO","BJ"));
        System.out.println(parser.returnHashMap());
//        System.out.println(parser.parseInformationPo("PO","SK","Svidník").getNameOfVillage());
//        parish.setNewInformation();
//        List<Parish> Parishes = parser.returnListOfParishes("KE","Košice");
//        String parish = Parishes.get(1).getNameOfVillage();
//        System.out.println(parish);
//        parser.setNewInformation();
    }
}
