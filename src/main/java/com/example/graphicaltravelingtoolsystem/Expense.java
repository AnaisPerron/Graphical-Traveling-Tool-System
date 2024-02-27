package com.example.graphicaltravelingtoolsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.Serializable;

public class Expense implements Serializable {
    private double price;
    private String name;

    private Destination destination;

    public Expense() {
    }


    public void displayStage(Stage stage, Journey journey, boolean create){
        Stage expenseNameStage = new Stage();
        //preventing the user to click anywhere outside the stage before choosing to do an action
        expenseNameStage.initModality(Modality.WINDOW_MODAL);
        expenseNameStage.initOwner(stage);
        Button okButton = new Button();

        Label namelabel = new Label("Enter your expense's name: ");
        TextField nameText = new TextField();
        HBox nameHbox = new HBox(5,namelabel,nameText);
        nameHbox.setAlignment(Pos.CENTER);

        Label destinationLabel = new Label("Destination: ");
        ComboBox<Destination> destinations = new ComboBox<>();
        for (int i = 0; i<journey.getDestinations().size(); i++){
            destinations.getItems().add(journey.getDestinations().get(i));
        }
        HBox destHbox = new HBox(5, destinationLabel, destinations);
        destHbox.setAlignment(Pos.CENTER);


        Label priceLabel = new Label("Enter a price: ");
        TextField priceText = new TextField();
        HBox priceHbox = new HBox(5, priceLabel, priceText);
        priceHbox.setAlignment(Pos.CENTER);

        if(create) {
            okButton.setText("Create");

        }
        else {
            okButton.setText("Modify");
            priceText.setText(String.valueOf(this.price));
            nameText.setText(this.name);
        }

        // Handling the 'X' button close event if the user creates a button (it deletes it)
        expenseNameStage.setOnCloseRequest(event -> {

            System.out.println("Action Canceled");
        });
        okButton.setOnAction(e -> {
            //Case1: The user left the text field blank
            if (nameText.getText().isEmpty()) {
                Alert blankAlert = new Alert(Alert.AlertType.ERROR);
                blankAlert.setHeaderText("You must input a expense's name");
                blankAlert.setTitle("Blank Text Error");
                System.out.println(blankAlert.getTitle() + " displayed");
                blankAlert.showAndWait();
            }

            else if(destinations.getValue() == null){
                Alert blankAlert = new Alert(Alert.AlertType.ERROR);
                blankAlert.setHeaderText("You must input a destination's name");
                blankAlert.setTitle("Blank ComboBox Error");
                System.out.println(blankAlert.getTitle() + " displayed");
                blankAlert.showAndWait();
            }


            else{
                System.out.println("expense \"" + nameText.getText() + "\" added");

                this.setName(nameText.getText());


                if(priceText.getText().isEmpty()){
                    this.setPrice(0.0);
                }
                else{
                    try {
                        double number = Double.parseDouble(priceText.getText());
                        // If parsing succeeds, the inputString is a valid double
                        this.setPrice(number);

                        journey.getDestinations().get(journey.getDestinations().indexOf(destinations.getValue())).getExpenses().remove(this);
                        this.setDestination(destinations.getValue());
                        journey.getDestinations().get(journey.getDestinations().indexOf(destinations.getValue())).getExpenses().add(this);

                        AccountPage.addToListView();
                        expenseNameStage.close();


                    } catch (NumberFormatException exception) {
                        // If parsing fails, inputString is not a valid double
                        Alert blankAlert = new Alert(Alert.AlertType.ERROR);
                        blankAlert.setHeaderText(priceText.getText() + " is not a valid double");
                        blankAlert.setTitle("Parsing Error");
                        System.out.println(blankAlert.getTitle() + " displayed");

                        blankAlert.showAndWait();
                    }

                }



            }


        });

        Button delete = new Button("Delete");
        delete.setOnAction(event ->{
            if(!create)
                this.destination.getExpenses().remove(this);

            AccountPage.addToListView();
            expenseNameStage.close();
        });


        HBox buttonsHbox;
        if(create){
            buttonsHbox = new HBox(10, okButton);
        }
        else {
            buttonsHbox = new HBox(10, okButton, delete);
        }

        buttonsHbox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(10, nameHbox, priceHbox, destHbox, buttonsHbox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));

        Scene scene = new Scene(vBox);
        expenseNameStage.setScene(scene);
        expenseNameStage.setTitle("expense Info");
        expenseNameStage.show();
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return this.name + " ("+ this.price + "$) ";
    }
}
