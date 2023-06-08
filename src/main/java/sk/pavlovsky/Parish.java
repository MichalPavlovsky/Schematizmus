package sk.pavlovsky;

import java.util.ArrayList;


public class Parish {
    private String nameOfVillage;


    private int functionOfAdministrator;

    public int getFunctionOfAdministrator() {
        return functionOfAdministrator;
    }

    public void setFunctionOfAdministrator(int functionOfAdministrator) {
        this.functionOfAdministrator = functionOfAdministrator;
    }

    private ArrayList<String> filialky;

    private String nameOfDistrict;

    public String getNameOfDistrict() {
        return nameOfDistrict;
    }

    public void setNameOfDistrict(String nameOfDistrict) {
        this.nameOfDistrict = nameOfDistrict;
    }

    private String nameOfEparchy;

    public String getNameOfEparchy() {
        return nameOfEparchy;
    }

    public void setNameOfEparchy(String nameOfEparchy) {
        this.nameOfEparchy = nameOfEparchy;
    }

    private ArrayList<String> naOdpocinku;
    private ArrayList<String> jurisdikcneUzemie;
    private String nameofSpravca;
    private ArrayList<String> kaplani;
    private ArrayList<String> vypomocnyDuchovny;
    private ArrayList<String> duchovnySpravca;

    public Parish() {
        this.filialky = new ArrayList<>();
        this.jurisdikcneUzemie = new ArrayList<>();
        this.vypomocnyDuchovny = new ArrayList<>();
        this.duchovnySpravca = new ArrayList<>();
        this.naOdpocinku = new ArrayList<>();
        this.kaplani = new ArrayList<>();
    }

    public ArrayList<String> getDuchovnySpravca() {
        return duchovnySpravca;
    }

    public void setDuchovnySpravca(ArrayList<String> duchovnySpravca) {
        this.duchovnySpravca = duchovnySpravca;
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

    public ArrayList<String> getNaOdpocinku() {
        return naOdpocinku;
    }

    public void setNaOdpocinku(ArrayList<String> naOdpocinku) {
        this.naOdpocinku = naOdpocinku;
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

    @Override
    public String toString() {
        return "Parish{" +
                "nameOfVillage='" + nameOfVillage + '\'' +
                ", functionOfAdministrator=" + functionOfAdministrator +
                ", filialky=" + filialky +
                ", nameOfDistrict='" + nameOfDistrict + '\'' +
                ", nameOfEparchy='" + nameOfEparchy + '\'' +
                ", naOdpocinku=" + naOdpocinku +
                ", jurisdikcneUzemie=" + jurisdikcneUzemie +
                ", nameofSpravca='" + nameofSpravca + '\'' +
                ", kaplani=" + kaplani +
                ", vypomocnyDuchovny=" + vypomocnyDuchovny +
                ", duchovnySpravca=" + duchovnySpravca +
                '}';
    }
}


