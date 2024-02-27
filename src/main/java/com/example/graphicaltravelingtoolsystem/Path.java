package com.example.graphicaltravelingtoolsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

public class Path extends ItinerarySimulator {

    private Button startLocation;
    private Button endLocation;
    private double weight;
    private String additionalInfo = "";

    private Pane pane;



    private Line line;
    private final String PATH_ICON_URL = Objects.requireNonNull(getClass().getResource("path.png")).toExternalForm();
    //private Line line = ItinerarySimulator.getLinesMap().get(endLocation).
    public static void main(String[] args) {
        launch(args);

    }
    
    
    public Path(Button button1, Button button2, Line line){
        this.setstartLocation(button1);
        this.setendLocation(button2);
        this.line = line;
        this.line.setStrokeWidth(3);

        this.line.setOnMouseClicked(lineEvent -> {
            this.displayPathInfo(true);
        });
        this.line.setOnMouseEntered(lineEvent -> {
            ItinerarySimulator.getWeightLabel().setText(this.weight + ItinerarySimulator.getTotalSign().getText());
            if(!this.line.getStroke().equals(Color.rgb(44, 230, 90 ))){
                this.line.setStroke(Color.DARKGRAY);
            }

        });
        this.line.setOnMouseExited(lineEvent -> {
            ItinerarySimulator.getWeightLabel().setText("");
            if(!this.line.getStroke().equals(Color.rgb(44, 230, 90))){
                this.line.setStroke(Color.BLACK);
            }
        });


        displayPathInfo( false);
    }

    public void displayPathInfo(boolean isLineClicked){
        Stage pathStage = new Stage();
        //preventing the user to click anywhere outside the stage before choosing to do an action
        pathStage.initModality(Modality.WINDOW_MODAL);
        pathStage.initOwner(ItinerarySimulator.getPrimaryStage());


        Label doubleLabel = new Label();
        Label sign = new Label();
        Label addInfoLabel = new Label("Additional information: ");
        TextArea addInfoArea;
        if(ItinerarySimulator.isPriceOption()){
            doubleLabel.setText("Enter the Price of the Journey: ");
            sign.setText("$");
        }
        else{
            doubleLabel.setText("Enter the Distance of the Journey: ");
            sign.setText("km");
        }
        TextField amountText;
        Button okButton = new Button();

            /*If it is a pop-up window to modify the line, one of the
            button's name will be set to "modify" and the textField of the stage will
            contain the previous amount*/
        if (isLineClicked) {
        amountText = new TextField(String.valueOf(this.weight));
        addInfoArea =  new TextArea(this.additionalInfo);
        okButton.setText("Modify");
        }

        //If it is a pop-up window to add a path, one of the button's name will be set to "create"
        else {
            amountText = new TextField();
            addInfoArea =  new TextArea(this.additionalInfo);
            okButton.setText("Create");

            pathStage.setOnCloseRequest(event ->{
                if(!isLineClicked){
                    deleteLine();
                }
                System.out.println("Action Canceled");
            });
        }

        okButton.setOnAction(e -> {
            //Case1: The user left the text field blank
            if (amountText.getText().isEmpty()) {
                Alert blankAlert = new Alert(Alert.AlertType.ERROR);
                blankAlert.setHeaderText("You must input the path's weight");
                blankAlert.setTitle("Blank Text Error");
                System.out.println(blankAlert.getTitle() + " displayed");
                blankAlert.showAndWait();
            }
            else {
                try {
                    double number = Double.parseDouble(amountText.getText());
                    // If parsing succeeds, the inputString is a valid double
                    this.setWeight(number);
                    this.setAdditionalInfo(addInfoArea.getText());

                    if (isLineClicked) {
                        System.out.println("Path from \"" + startLocation.getUserData() + " \" to \"" + endLocation.getUserData() +  "\" modified");
                        pathStage.close();
                    } else {

                        System.out.println("Path of weight (" + this.weight + ") between " + this.startLocation.getUserData() + "\" and \"" + this.endLocation.getUserData() + "\" added");
                        pathStage.close();
                    }

                } catch (NumberFormatException exception) {
                    // If parsing fails, inputString is not a valid double
                    Alert blankAlert = new Alert(Alert.AlertType.ERROR);
                    blankAlert.setHeaderText(amountText.getText() + " is not a valid double");
                    blankAlert.setTitle("Parsing Error");
                    System.out.println(blankAlert.getTitle() + " displayed");

                    blankAlert.showAndWait();
                }




            }

        });

        Button deleteButton = new Button("Delete");
        //Handling the "delete" button event
        deleteButton.setOnAction(event -> {
            System.out.println("Path from \"" + startLocation.getUserData() + " \" to \"" + endLocation.getUserData() +  "\" deleted");
            deleteLine();

            pathStage.close();

        });

        HBox pathWeightHbox = new HBox(10, doubleLabel, amountText, sign);
        pathWeightHbox.setAlignment(Pos.CENTER);

        addInfoArea.setPrefRowCount(2);
        addInfoArea.setPrefColumnCount(15);
        HBox addInfoHBox = new HBox(10, addInfoLabel, addInfoArea);
        addInfoHBox.setAlignment(Pos.CENTER);

        HBox buttonsHbox = new HBox(10, okButton, deleteButton);
        buttonsHbox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(10, pathWeightHbox,addInfoHBox, buttonsHbox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));

        Image pathImage = new Image(PATH_ICON_URL);
        ImageView pathImageView = new ImageView(pathImage);
        pathImageView.setFitWidth(50);

        pathImageView.setFitHeight(50);

        HBox hBox = new HBox(10, vBox, pathImageView);
        hBox.setPadding(new Insets(20));

        Scene scene = new Scene(hBox);
        pathStage.setScene(scene);
        pathStage.setTitle("Path Info");
        pathStage.show();








        }

    public Button getstartLocation() {
        return this.startLocation;
    }

    public void setstartLocation(Button startLocation) {
        this.startLocation = startLocation;
    }

    public Button getendLocation() {
        return this.endLocation;
    }

    public void setendLocation(Button endLocation) {
        this.endLocation = endLocation;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Line getLine() {
        return this.line;
    }



    public void setLine(Line line) {
        this.line = line;
    }

    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void deleteLine(){

            ItinerarySimulator.getBorderPane().getChildren().remove(this.line);

        ItinerarySimulator.getLinesMap().get(startLocation).remove(this);
        ItinerarySimulator.getLinesMap().get(endLocation).remove(this);

    }

    public void updateLine(Button button) {
            //if the button is the end of the path, the endX and endY changes
            if (this.startLocation == button) {
                this.line.setStartX(button.localToScene(button.getBoundsInLocal()).getMinX() + button.getWidth() / 2);
                this.line.setStartY(button.localToScene(button.getBoundsInLocal()).getMinY() + button.getHeight() / 2);

            } else {
                this.line.setEndX(button.localToScene(button.getBoundsInLocal()).getMinX() + button.getWidth() / 2);
                this.line.setEndY(button.localToScene(button.getBoundsInLocal()).getMinY() + button.getHeight() / 2);
            }



    }

    public Button getStartLocation() {
        return this.startLocation;
    }

    public void setStartLocation(Button startLocation) {
        this.startLocation = startLocation;
    }

    public Button getEndLocation() {
        return this.endLocation;
    }

    public void setEndLocation(Button endLocation) {
        this.endLocation = endLocation;
    }

    public Pane getPane() {
        return this.pane;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    @Override
    public String getPATH_ICON_URL() {
        return PATH_ICON_URL;
    }
}
