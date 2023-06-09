package sk.pavlovsky.database;


import sk.pavlovsky.Parish;

import javax.swing.*;
import java.awt.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UI extends JFrame {
    private JComboBox<String> cityDropdown;
    private JButton searchButton;
    private JLabel labelEparchy,labelDistrict, labelFilialka, labelVypomocny, labelKaplan, labelSpravca, labelNameOfVillage;

    public UI() {

        setTitle("Vyhladavanie udajov");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        cityDropdown = new JComboBox<>();

        labelEparchy = new JLabel();
        labelSpravca = new JLabel();
        labelDistrict = new JLabel();
        labelVypomocny = new JLabel();
        labelNameOfVillage = new JLabel();
        labelFilialka = new JLabel();
        labelKaplan = new JLabel();
        labelKaplan = new JLabel();
        cityDropdown.setPreferredSize(new Dimension(150, 25));
        add(cityDropdown);
        searchButton = new JButton("Find Parish");
        add(searchButton);
        searchButton.addActionListener(e -> {
            String selectedCity = (String) cityDropdown.getSelectedItem();
            searchRecords(selectedCity);});
        loadParishNames();
        add(new JLabel("Select parish: "));
        cityDropdown.setLocation(200,200);
        add(cityDropdown);
        add(searchButton);
        JLabel []jLabel= new JLabel[] {labelNameOfVillage, labelEparchy, labelDistrict,labelSpravca, labelKaplan, labelVypomocny, labelFilialka};
        int about = 400;
        for (JLabel value : jLabel) {
            about = about + 1;
            value.setPreferredSize(new Dimension( about, 20));
            add(value);
        }

        pack();
        setSize(500,300);
        setVisible(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private void searchRecords(String selectedCity) {
        DatabaseBridge bridge = new ConcreteDatabaseBridge(new MySqlImplementor());
        bridge.implementor.connect();
        Parish parish = bridge.implementor.getParishData(selectedCity);
        labelNameOfVillage.setText("Parish: "+parish.getNameOfVillage());
        labelEparchy.setText("Eparchia: "+parish.getNameOfEparchy());
        labelDistrict.setText("District: "+parish.getNameOfDistrict());
        labelSpravca.setText("Admin: "+parish.getNameofSpravca());
        setLabelWithArray("Kaplan: ",parish.getKaplani(), labelKaplan);
        setLabelWithArray("Filialky: ",parish.getFilialky(), labelFilialka);
        setLabelWithArray("Vypomocny: ",parish.getVypomocnyDuchovny(), labelVypomocny);
        bridge.implementor.close();
    }
    public void loadParishNames(){
        DatabaseBridge bridge = new ConcreteDatabaseBridge(new MySqlImplementor());
        bridge.implementor.connect();
        String query = "SELECT FARNOST.NAZOV " +
                "FROM FARNOST";
        try {
            PreparedStatement statement = bridge.implementor.getConnection().prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                String parishName = resultSet.getString("NAZOV");
                cityDropdown.addItem(parishName);
            }
            bridge.implementor.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void setLabelWithArray(String nazov, ArrayList<String> arraylist, JLabel label){
        if (arraylist.isEmpty()){
            label.setText(nazov+"-");
        }else for (String name: arraylist) {
            label.setText(nazov+name);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UI::new);
    }
}