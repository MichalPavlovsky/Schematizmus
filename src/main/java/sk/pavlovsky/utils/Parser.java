package sk.pavlovsky.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Parser{
    private String nameOfVillage;
    private ArrayList<String> filialky;
    private ArrayList<String> jurisdikcneUzemie;
    private String nameofSpravca;
    private ArrayList<String> kaplani;
    private ArrayList<String> vypomocnyDuchovny;

    public Parser() {
        this.filialky=new ArrayList<>();
        this.jurisdikcneUzemie = new ArrayList<>();
    }

    public String getNameOfVillage() {
        return nameOfVillage;
    }

    public void setNameOfVillage(String nameOfVillage) {
        this.nameOfVillage = nameOfVillage;
    }

    public ArrayList<String> getFilialky() {
        return filialky;
    }

    public void setFilialky(ArrayList<String> filialky) {
        this.filialky = filialky;
    }

    public ArrayList<String> getJurisdikcneUzemie() {
        return jurisdikcneUzemie;
    }

    public void setJurisdikcneUzemie(ArrayList<String> jurisdikcneUzemie) {
        this.jurisdikcneUzemie = jurisdikcneUzemie;
    }

    public String getNameofSpravca() {
        return nameofSpravca;
    }

    public void setNameofSpravca(String nameofSpravca) {
        this.nameofSpravca = nameofSpravca;
    }

    public ArrayList<String> getKaplani() {
        return kaplani;
    }

    public void setKaplani(ArrayList<String> kaplani) {
        this.kaplani = kaplani;
    }

    public ArrayList<String> getVypomocnyDuchovny() {
        return vypomocnyDuchovny;
    }

    public void setVypomocnyDuchovny(ArrayList<String> vypomocnyDuchovny) {
        this.vypomocnyDuchovny = vypomocnyDuchovny;
    }
    public void parseInformation(String nameOfVillage) {
        String filePath = "src/main/resources/webs/SB.lst/"+nameOfVillage+".html";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            String htmlContent = content.toString();
            Document doc = Jsoup.parse(htmlContent);

            String regexNameOfSpravca = "Správca farnosti:</strong><br><a[^>]*>(.*?)</a>";
            String regexnameOfVillage = "Farnosť: <span [^>]*>(.*?)</span>";
            String regexFilialky = "\\d+\\.\\s+(.*?)\\s+-";
            String regexJurisdikcneUzemie = "Jurisdikčné územie:</strong><br>[^>]*>\\s*(.*?)\\n\\s*<[^>]*>(.*?)<";

            Pattern patternNameOfSpravca = Pattern.compile(regexNameOfSpravca);
            Pattern patternFilialky = Pattern.compile(regexFilialky);
            Pattern patternNameOfVillage = Pattern.compile(regexnameOfVillage);
            Pattern patternJurisdikcneUzemie = Pattern.compile(regexJurisdikcneUzemie);

            Matcher matcherNameOfSpravca = patternNameOfSpravca.matcher(htmlContent);
            Matcher matcherNameOfVillage = patternNameOfVillage.matcher(htmlContent);
            Matcher matcherJurisdikcneUzemie = patternJurisdikcneUzemie.matcher(htmlContent);

            Elements filialkyDivs = doc.select("strong:contains(Filiálka)+div");
            for (Element div : filialkyDivs) {
                String text = div.text();
                Matcher matcher = patternFilialky.matcher(text);
                if (matcher.find()) {
                    this.filialky.add(matcher.group(1));
                }

            }
            if (matcherJurisdikcneUzemie.find()) {
                this.jurisdikcneUzemie.add(matcherJurisdikcneUzemie.group(1));
            }
            if (matcherNameOfSpravca.find()) {
                setNameofSpravca(matcherNameOfSpravca.group(1));
                }
            if (matcherNameOfVillage.find()) {
                setNameOfVillage(matcherNameOfVillage.group(1));
            }
            else {
                System.out.println("Nepodarilo sa nájsť meno správcu farnosti.");
            }
        } catch (IOException e) {
            System.out.println("Vyskytla sa chyba pri čítaní zo súboru: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Parser parser= new Parser();
        parser.parseInformation("Lipany");
        System.out.println(parser.getNameofSpravca());
        System.out.println(parser.getNameOfVillage());
        System.out.println(parser.getFilialky());
        for (String i: parser.getFilialky()) {
            System.out.println(i);
        }
        System.out.println(parser.getJurisdikcneUzemie());

        }
    }


