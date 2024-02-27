package com.example.graphicaltravelingtoolsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

public class Vertex extends ItinerarySimulator{
    private static String locationInitialName = "";
    private static final String PINK_STYLE = "-fx-background-color: rgb(245, 179, 232);";

    private static final String CIRCLE_LOCATION_ICON_URL = Objects.requireNonNull(Vertex.class.getResource("location.png")).toExternalForm();




    Vertex previousVertex;
    private Button button;
    private String name;
    public Vertex(Button button) {
        this.button = button;
        this.name = (String) button.getUserData();
    }

    private List<Vertex> shortestPath = new LinkedList<>();
    private List<Path> pathList = new ArrayList<>();
    //By default, I want to set the minimum distance to infinity (I can't do that, so I will put Double.MAX_VALUE instead)
    private Double distance = Double.MAX_VALUE;

    //because each vertex has a map of vertices that are directly related with the vertex object
    Map<Vertex, Double> adjacentVertices = new HashMap<>();

    public void addDestination(Vertex vertex, double distance) {
        adjacentVertices.put(vertex, distance);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Button getButton() {
        return this.button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public List<Vertex> getShortestPath() {
        return this.shortestPath;
    }

    public void setShortestPath(List<Vertex> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public Double getDistance() {
        return this.distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Map<Vertex, Double> getAdjacentVertices() {
        return this.adjacentVertices;
    }

    public void setAdjacentVertices(Map<Vertex, Double> adjacentVertices) {
        this.adjacentVertices = adjacentVertices;
    }

    public Vertex getpreviousVertex() {
        return previousVertex;
    }

    public void setpreviousVertex(Vertex previousVertex) {
        this.previousVertex = previousVertex;
    }
    public static boolean containsLocation(String s){

        for (Map.Entry<Button, List<Path>> entry : ItinerarySimulator.getLinesMap().entrySet()) {
            String toCompare =(String) entry.getKey().getUserData();
            if(toCompare.equals(s)){
                return true;
            }
        }
        return false;
    }
    public static void displayNameStage(Button button, Label locationLabel, Boolean isLocationClicked, double positionX, double positionY, Pane mainPane, Stage primaryStage, VBox location) {
        Stage locationNameStage = new Stage();
        //preventing the user to click anywhere outside the stage before choosing to do an action
        locationNameStage.initModality(Modality.WINDOW_MODAL);
        locationNameStage.initOwner(primaryStage);

        Label label = new Label("Enter your location's name: ");
        TextField nameText;
        Button okButton = new Button();

            /*If it is a pop-up window to modify the name of the button, one of the
            button's name will be set to "modify" and the textField of the stage will contain the name of the initial
            location's name*/
        if (isLocationClicked) {
            locationInitialName = (String) button.getUserData();
            nameText = new TextField(locationInitialName);
            okButton.setText("Modify");


        }
        //If it is a pop-up window to add a button, one of the button's name will be set to "create"
        else {
            nameText = new TextField();
            okButton.setText("Create");
            // Handling the 'X' button close event if the user creates a button (it deletes it)
            locationNameStage.setOnCloseRequest(event -> {
                ItinerarySimulator.getLinesMap().remove(button, ItinerarySimulator.getLinesMap().get(button));
                mainPane.getChildren().remove(location);
                System.out.println("Action Canceled");
            });
        }


        okButton.setOnAction(e -> {
            //Case1: The user left the text field blank
            if (nameText.getText().isEmpty()) {
                Alert blankAlert = new Alert(Alert.AlertType.ERROR);
                blankAlert.setHeaderText("You must input a location's name");
                blankAlert.setTitle("Blank Text Error");
                System.out.println(blankAlert.getTitle() + " displayed");
                blankAlert.showAndWait();
            }
            //Case2: The user is inputting a locations Name that is already used
            else if(containsLocation(nameText.getText())){
                Alert blankAlert = new Alert(Alert.AlertType.ERROR);
                blankAlert.setHeaderText("This location already exists");
                blankAlert.setTitle("Existing Location Error");
                System.out.println(blankAlert.getTitle() + " displayed");
                blankAlert.showAndWait();

            }
            //Case 3: the user is modifying or creating a location
            else {
                if (isLocationClicked) {
                    System.out.println("Location \"" + button.getUserData() + " \" modified to \"" + nameText.getText() + "\" in (" + String.format("%.02f", positionX) + ", " + String.format("%.02f", positionY) + ")");
                } else {
                    System.out.println("Location \"" + nameText.getText() + "\" added in (" + String.format("%.02f", positionX) + ", " + String.format("%.02f", positionY) + ")");
                }
                button.setUserData(nameText.getText());
                locationLabel.setText(nameText.getText());

                ItinerarySimulator.getLinesMap().put(button, new ArrayList<>());
                locationNameStage.close();
            }

        });

        Button deleteButton = new Button("Delete");
        //Handling the "delete" button event
        deleteButton.setOnAction(event -> {
            System.out.println("Location \"" + locationInitialName + "\" deleted in (" + String.format("%.02f", positionX) + ", " + String.format("%.02f", positionY) + ")");
            if (button.getStyle().equals(PINK_STYLE)) {
                ItinerarySimulator.setLocationCount(0);
            }
            mainPane.getChildren().remove(location);

            if(isLocationClicked)
                deleteLines(button);


            ItinerarySimulator.getLinesMap().remove(button);
            locationNameStage.close();

        });
        HBox locationNameHbox = new HBox(10, label, nameText);
        locationNameHbox.setAlignment(Pos.CENTER);
        HBox buttonsHbox = new HBox(10, okButton, deleteButton);
        buttonsHbox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(10, locationNameHbox, buttonsHbox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));

        Image locationImage = new Image(CIRCLE_LOCATION_ICON_URL);
        ImageView locationImageView = new ImageView(locationImage);
        locationImageView.setFitWidth(50);

        locationImageView.setFitHeight(50);

        HBox hBox = new HBox(10, vBox, locationImageView);
        hBox.setPadding(new Insets(20));

        Scene scene = new Scene(hBox);
        locationNameStage.setScene(scene);
        locationNameStage.setTitle("Location's name");
        locationNameStage.show();

    }

    public static String getLocationInitialName() {
        return Vertex.locationInitialName;
    }

    public static void setLocationInitialName(String locationInitialName) {
        Vertex.locationInitialName = locationInitialName;
    }

    public Vertex getPreviousVertex() {
        return this.previousVertex;
    }

    public void setPreviousVertex(Vertex previousVertex) {
        this.previousVertex = previousVertex;
    }

    public List<Path> getPathList() {
        return this.pathList;
    }

    public void setPathList(List<Path> pathList) {
        this.pathList = pathList;
    }
}
