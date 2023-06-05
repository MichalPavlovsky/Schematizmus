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


public class StoreParish {
    ObjectMapper objectMapper;
    public static final String PATH = "src/main/resources/JsonView/out.json";

    public StoreParish() {
        this.objectMapper = new ObjectMapper();
    }

    public void setInformation(Parser parser) {
        JsonNode rootNode = readRootNode();
        for (String keyAllMap : parser.returnHashMap().keySet()) {
            HashMap<String, List<Parish>> partOfMap = parser.returnHashMap().get(keyAllMap);
            for (int i = 0; i <= parser.returnHashMap().size(); i++) {
                processingEparchyandDistricts(rootNode, i, partOfMap, keyAllMap);
            }
        }
        try {
            objectMapper.writeValue(new File(PATH), rootNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
}
public void processingEparchyandDistricts(JsonNode rootNode, int i, HashMap<String, List<Parish>> partOfMap, String keyAllMap){
    if (rootNode.get(i).get("eparchia").asText().equals(keyAllMap)) {
    for (String keyOfDekanat : partOfMap.keySet()) {
        for (JsonNode dekanat : rootNode.get(i).get("dekanaty")) {
            if (dekanat.get("dekanat").asText().equals(keyOfDekanat)) {
                JsonNode farnosti = dekanat.get("farnosti");
                List<Parish> listOfParish = partOfMap.get(keyOfDekanat);
                processingParishes(listOfParish,farnosti);
            }
        }
    }
}
    }
    public void processingParishes(List<Parish> listOfParish,JsonNode farnosti){
        for (Parish parish : listOfParish) {
            for (JsonNode farnost : farnosti) {
                if (farnost.get("farnost").asText().equals(parish.getNameOfVillage().trim())) {
                    if (parish.getNameofSpravca() != null) {
                        ((ObjectNode) farnost).put("Farar", parish.getNameofSpravca().trim());
                    }
                    ((ObjectNode) farnost).put("Kaplani", createArray(parish.getKaplani()));
                    ((ObjectNode) farnost).put("Výpomocný duchovný", createArray(parish.getVypomocnyDuchovny()));
    }}}
    }

    public JsonNode readRootNode(){
        try {
            return objectMapper.readTree(new File(PATH));
        } catch (IOException e) {
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

    public static void main(String[] args) {
        Parser parser = new Parser();
        StoreParish storeParish = new StoreParish();
        storeParish.setInformation(parser);
    }
}