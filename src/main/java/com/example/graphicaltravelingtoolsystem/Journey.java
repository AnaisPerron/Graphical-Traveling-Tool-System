package com.example.graphicaltravelingtoolsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Journey implements Serializable {
    private String name;

    private Account account;
    private double budget;
    private ArrayList<Destination> destinations;

    private final String ACCOUNTSTYLE = Objects.requireNonNull(getClass().getResource("accountpage.css")).toExternalForm();



    public Journey(String name, Account account, double budget) {
        this.name = name;
        this.account = account;
        this.budget = budget;
        this.destinations = new ArrayList<>();

    }

    public Journey() {
        this.destinations = new ArrayList<>();
    }

    public void displayStage(Stage stage, VBox journeysVBox, Button journeybutton, boolean create, Account account){
        Stage journeyNameStage = new Stage();
        //preventing the user to click anywhere outside the stage before choosing to do an action
        journeyNameStage.initModality(Modality.WINDOW_MODAL);
        journeyNameStage.initOwner(stage);
        Button okButton = new Button();
        if (create){
            okButton.setText("Create");
        }
        else{
            okButton.setText("Modify");
        }

        Label namelabel = new Label("Enter your journey's name: ");
        TextField nameText = new TextField();
        HBox nameHbox = new HBox(5,namelabel,nameText);
        nameHbox.setAlignment(Pos.CENTER);
        
        
        Label budgetLabel = new Label("Enter a budget: ");
        TextField budgetText = new TextField();
        HBox budgetHbox = new HBox(5, budgetLabel, budgetText);
        budgetHbox.setAlignment(Pos.CENTER);
        if(!create){
            nameText.setText(this.name);
            budgetText.setText(String.valueOf(this.budget));
        }

        // Handling the 'X' button close event if the user creates a button (it deletes it)
        journeyNameStage.setOnCloseRequest(event -> {
            if(create) {
                journeysVBox.getChildren().remove(journeybutton);
                account.getJourneys().remove(this);
            }
            System.out.println("Action Canceled");
        });
        okButton.setOnAction(e -> {
            //Case1: The user left the text field blank
            if (nameText.getText().isEmpty()) {
                Alert blankAlert = new Alert(Alert.AlertType.ERROR);
                blankAlert.setHeaderText("You must input a journey's name");
                blankAlert.setTitle("Blank Text Error");
                System.out.println(blankAlert.getTitle() + " displayed");
                blankAlert.showAndWait();
            }
            //Case2: The user is inputting a journey Name that is already used
            else if(((existantName(nameText.getText()) && create)) ||(existantName(nameText.getText()) && !this.name.equals(nameText.getText()) && !create)){
                Alert blankAlert = new Alert(Alert.AlertType.ERROR);
                blankAlert.setHeaderText("This journey name already exists");
                blankAlert.setTitle("Existing Journey Error");
                System.out.println(blankAlert.getTitle() + " displayed");
                blankAlert.showAndWait();

            }
            else{
                System.out.println("Journey \"" + nameText.getText() + "\" added");
                journeybutton.setText(nameText.getText());


                try {



                        if(budgetText.getText().isEmpty()){
                            this.setBudget(0.0);
                        }
                        else {
                            double number = Double.parseDouble(budgetText.getText());
                            // If parsing succeeds, the inputString is a valid double
                            this.setBudget(number);

                        }
                    if (create)
                        journeysVBox.getChildren().add(journeybutton);
                    this.setName(nameText.getText());
                    journeyNameStage.close();
                    AccountPage.addToListView();
                    } catch (NumberFormatException exception) {
                        // If parsing fails, inputString is not a valid double
                        Alert blankAlert = new Alert(Alert.AlertType.ERROR);
                        blankAlert.setHeaderText(budgetText.getText() + " is not a valid double");
                        blankAlert.setTitle("Parsing Error");
                        System.out.println(blankAlert.getTitle() + " displayed");

                        blankAlert.showAndWait();
                    }





            }

        });

        Button deleteButton = new Button("Delete");
        //Handling the "delete" button event
        deleteButton.setOnAction(event -> {
            System.out.println("Journey deleted");
            journeysVBox.getChildren().remove(journeybutton);

                account.getJourneys().remove(this);
            if(journeybutton.equals(AccountPage.getClickedButton())){
                AccountPage.setClickedButton(null);
            }
            journeyNameStage.close();
            AccountPage.addToListView();

        });

        

        HBox buttonsHbox = new HBox(10, okButton, deleteButton);
        buttonsHbox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(10, nameHbox, budgetHbox, buttonsHbox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));

        Scene scene = new Scene(vBox);
        journeyNameStage.setScene(scene);
        journeyNameStage.setTitle("Journey Info");
        journeyNameStage.show();
    }

    public boolean existantName(String s){
        for(Journey jour : this.account.getJourneys()){
            if (jour.getName() != null && jour.getName().equals(s))
            return true;
        }
        return false;
    }
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Destination> getDestinations() {
        return this.destinations;
    }

    public void setDestinations(ArrayList<Destination> destinations) {
        this.destinations = destinations;
    }

    public Account getAccount() {
        return this.account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public double getBudget() {
        return this.budget;
    }

    public String getACCOUNTSTYLE() {
        return this.ACCOUNTSTYLE;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }



}
