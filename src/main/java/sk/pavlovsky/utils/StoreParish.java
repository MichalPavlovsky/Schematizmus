package sk.pavlovsky.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.stream.Stream;

public class StoreParish {
    ObjectMapper objectMapper;
    public static final String PATH = "src/main/resources/JsonView/out.json";

    public StoreParish() {
        this.objectMapper = new ObjectMapper();
    }

    public ArrayList<JsonNode> storeEparchy() {
        ArrayList<JsonNode> jsonEparchy = new ArrayList<>();
        String jsonFilePaths = PATH;
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(new File(jsonFilePaths));
            for (int i=0; i<rootNode.size(); i++) {
                JsonNode nodeNames = rootNode.get(i);
                jsonEparchy.add(nodeNames);
                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonEparchy;
    }
    public ArrayList<JsonNode> parseDistrict(Parser parser){
        ArrayList<JsonNode> parseDistricts = new ArrayList<>();
        ArrayList<JsonNode> jsonEparchy = storeEparchy();
        for (String keyAllMap : parser.returnHashMap().keySet()) {
            for (int i = 0; i <= parser.returnHashMap().size(); i++) {
                JsonNode nodeNames = jsonEparchy.get(i);
                JsonNode eparchy = nodeNames.get("eparchia");
                if (eparchy.asText().equals(keyAllMap)) {
                    parseDistricts.add(nodeNames.get("dekanaty"));}
            }
        }
        return parseDistricts;
    }

    public void setInformation(Parser parser) throws IOException {
        for (int i = 0; i < parseDistrict(parser).size(); i++) {
            List<String> listOfKeys = Stream.of(parser.returnHashMap().keySet().toArray(new String[0])).toList();
            HashMap<String, List<Parish>> partOfMap = parser.returnHashMap().get(listOfKeys.get(i));
            JsonNode dekanaty = parseDistrict(parser).get(i);
            for (String keyOfDekanat : partOfMap.keySet()) {
                for (JsonNode dekanat : dekanaty) {
                    JsonNode dekanatNameNode = dekanat.get("dekanat");
                    if (dekanatNameNode.asText().equals(keyOfDekanat)) {
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

}       JsonNode rootNode = objectMapper.readTree(new File(PATH));
        System.out.println(rootNode);
        objectMapper.writeValue(new File(PATH), rootNode);
}


    public ArrayNode createArray(ArrayList<String> array) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (String arr : array) {
            arrayNode.add(arr);
        }
        return arrayNode;
    }
}
