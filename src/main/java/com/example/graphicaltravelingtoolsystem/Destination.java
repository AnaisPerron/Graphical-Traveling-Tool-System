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
import java.util.ArrayList;

public class Destination implements Serializable {
   private ArrayList<Expense> expenses = new ArrayList<>();
   private double budget;
   private String name;

   public void displayStage(Stage stage, boolean create, Journey jour){
      Stage destinationNameStage = new Stage();
      //preventing the user to click anywhere outside the stage before choosing to do an action
      destinationNameStage.initModality(Modality.WINDOW_MODAL);
      destinationNameStage.initOwner(stage);

      Label namelabel = new Label("Enter your destination's name: ");
      TextField nameText = new TextField();
      HBox nameHbox = new HBox(5,namelabel,nameText);
      nameHbox.setAlignment(Pos.CENTER);




      Label budgetLabel = new Label("Enter a budget: ");
      TextField budgetText = new TextField();

      HBox budgetHbox = new HBox(5, budgetLabel, budgetText);
      budgetHbox.setAlignment(Pos.CENTER);


      // Handling the 'X' button close event if the user creates a button (it deletes it)
      destinationNameStage.setOnCloseRequest(event -> {

         System.out.println("Action Canceled");
      });

      Button okButton = new Button("Create");
      Button delete = new Button("Delete");

      if(!create){
         okButton.setText("Modify");
         budgetText.setText(String.valueOf(this.budget));
         nameText.setText(this.name);

         delete.setOnAction(event ->{
            if(!create)
               jour.getDestinations().remove(this);

            AccountPage.addToListView();
            destinationNameStage.close();

         });

      }


      okButton.setOnAction(e -> {
         //Case1: The user left the text field blank
         if (nameText.getText().isEmpty()) {
            Alert blankAlert = new Alert(Alert.AlertType.ERROR);
            blankAlert.setHeaderText("You must input a destination's name");
            blankAlert.setTitle("Blank Text Error");
            System.out.println(blankAlert.getTitle() + " displayed");
            blankAlert.showAndWait();
         }
         else if(journeyContainsDest(jour, nameText.getText())){
            Alert blankAlert = new Alert(Alert.AlertType.ERROR);
            blankAlert.setHeaderText("The destination already exists");
            blankAlert.setTitle("Existant destination Error");
            System.out.println(blankAlert.getTitle() + " displayed");
            blankAlert.showAndWait();
         }

         else{
            System.out.println("destination \"" + nameText.getText() + "\" added");

            this.setName(nameText.getText());

            try {
            if(budgetText.getText().isEmpty()){
               this.setBudget(0.0);

            }
            else {

               double number = Double.parseDouble(budgetText.getText());
               // If parsing succeeds, the inputString is a valid double
               this.setBudget(number);

            }
               if(create)
               jour.getDestinations().add(this);

               destinationNameStage.close();
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




      HBox buttonsHbox;
      if (create){
         buttonsHbox = new HBox(10, okButton);
      }
      else{
         buttonsHbox = new HBox(10, okButton, delete);
      }

      buttonsHbox.setAlignment(Pos.CENTER);

      VBox vBox = new VBox(10, nameHbox, budgetHbox, buttonsHbox);
      vBox.setAlignment(Pos.CENTER);
      vBox.setPadding(new Insets(10));

      Scene scene = new Scene(vBox);
      destinationNameStage.setScene(scene);
      destinationNameStage.setTitle("destination Info");
      destinationNameStage.show();
   }

   public boolean journeyContainsDest(Journey jour, String name){
      for (Destination dest : jour.getDestinations()){
         if(dest.getName().equals(name)){
            return true;
         }
      }
      return false;
   }
   public ArrayList<Expense> getExpenses() {
      return expenses;
   }


   public void setExpenses(ArrayList<Expense> expenses) {
      this.expenses = expenses;
   }

   public double getBudget() {
      return budget;
   }

   public void setBudget(double budget) {
      this.budget = budget;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }



   @Override
   public String toString() {

      return this.name + " (budget: " + this.budget + "$)";
   }
}
