package com.example.graphicaltravelingtoolsystem;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;


public class ItinerarySimulator extends Application {


    private final String LOCATION_ICON_URL = Objects.requireNonNull(getClass().getResource("location.png")).toExternalForm();
    private final String PATH_ICON_URL = Objects.requireNonNull(getClass().getResource("path.png")).toExternalForm();
    private final String MONEY_ICON_URL = Objects.requireNonNull(getClass().getResource("money.png")).toExternalForm();
    private final String DISTANCE_ICON_URL = Objects.requireNonNull(getClass().getResource("distance.png")).toExternalForm();
    private final String PINK_STYLE = "-fx-background-color: rgb(245, 179, 232);";
    //MAX VALUE: 2, MIN VALUE:0. The path begins when there are two location that are chosen
    private static int locationCount = 0;
    //To know if we either create a path or a vertex
    private static boolean addVertex = true;
    //To know if we choose to calculate the money or calculate the distance
    private static boolean priceOption = true;
    //initial x and y position of a vbox when it is about to be dragged
    private double xOffset = 0;
    private double yOffset = 0;
    //Because for a mouse press evnt, the vertex could either be dragging or not, and the two events are different
    private boolean isVertexDragging = false;
    //Array of the two buttons that need to be linked together (values are set whenever different ones are chosen)
    private Button[] link = new Button[2];
    //css file for this class
    private final String itineraryFile = Objects.requireNonNull(getClass().getResource("itinerary.css")).toExternalForm();
    //Because I wanted to set the color of the left pane
    private Region leftRegion;

    private static Map<Button, List<Path>> linesMap = new HashMap<>();

    private static HashSet<Vertex> vertices;
    private Path path;
    private static BorderPane borderPane;
    private static Stage primaryStage;
    private static ComboBox<String> startBox = new ComboBox<>();
    private static ComboBox<String> endBox = new ComboBox<>();
    private static Label totalLabel = new Label("Total Price: ");
    private static Label totalAmountLabel = new Label("0.00");
    //Text changes when the toggle switch is set
    private static Label totalSign = new Label("$");

    private static Label weightLabel = new Label();

    public ItinerarySimulator() {
    }



    public void start(Stage stage) throws IOException {
        setPrimaryStage(stage);

        final double SCENE_HEIGHT = 800;
        final double SCENE_WIDTH = 1200;
        final double TOGGLE_HEIGHT = 30;

        borderPane = new BorderPane();
        Pane centerPane = new Pane();


        //Location or Path ToggleSwitch
        ToggleSwitch toggle1 = new ToggleSwitch(true);
        Image locationImage = new Image(LOCATION_ICON_URL);
        Image planeImage = new Image(PATH_ICON_URL);
        ImageView locationImageView = new ImageView(locationImage);
        ImageView planeImageView = new ImageView(planeImage);
        locationImageView.setFitHeight(TOGGLE_HEIGHT);
        locationImageView.setPreserveRatio(true);
        planeImageView.setFitHeight(TOGGLE_HEIGHT);
        planeImageView.setPreserveRatio(true);
        HBox locationOrPath = new HBox(2, locationImageView, toggle1, planeImageView);
        locationOrPath.setAlignment(Pos.CENTER);

        //Money or Distance ToggleSwitch
        ToggleSwitch toggle2 = new ToggleSwitch(false);
        Image moneyImage = new Image(MONEY_ICON_URL);
        Image distanceImage = new Image(DISTANCE_ICON_URL);
        ImageView moneyImageView = new ImageView(moneyImage);
        ImageView distanceImageView = new ImageView(distanceImage);
        moneyImageView.setFitHeight(TOGGLE_HEIGHT);
        moneyImageView.setPreserveRatio(true);
        distanceImageView.setFitHeight(TOGGLE_HEIGHT);
        distanceImageView.setPreserveRatio(true);
        HBox moneyOrDistance = new HBox(2, moneyImageView, toggle2, distanceImageView);
        moneyOrDistance.setAlignment(Pos.CENTER);


        Label startLabel = new Label("Starting Location: ");
        //To avoid inputting a vertex that does not exist
        startBox.setOnMouseClicked(event -> {
            addNamesToComboBox(startBox);

        });

        startBox.setOnAction(event -> {
            //to avoid that green paths overlap
            resetStyles(borderPane);
            //highlights the path and calculates it
            if (endBox.getValue() != null && startBox.getValue() != null) {
                doShortestActions();
            }
        });


        startBox.setPrefWidth(100);
        HBox startHbox = new HBox(2, startLabel, startBox);
        startHbox.setAlignment(Pos.CENTER);


        Label endLabel = new Label("Ending Location: ");
        endBox.setOnMouseClicked(event -> {
            addNamesToComboBox(endBox);

        });
        endBox.setOnAction(event -> {
            resetStyles(borderPane);
            if (endBox.getValue() != null && startBox.getValue() != null) {
                doShortestActions();
            }
        });
        endBox.setPrefWidth(100);
        HBox endHbox = new HBox(2, endLabel, endBox);
        endHbox.setAlignment(Pos.CENTER);

        totalLabel.setMaxHeight(50);
        totalAmountLabel.setMaxHeight(50);
        totalSign.setMaxHeight(50);
        HBox totalHbox = new HBox(2, totalLabel, totalAmountLabel, totalSign);
        totalHbox.setAlignment(Pos.CENTER);


        VBox toggles = new VBox(20, startHbox, endHbox, locationOrPath, moneyOrDistance, totalHbox);
        toggles.setAlignment(Pos.CENTER);


        borderPane.setLeft(toggles);
        //The only thing in the top part of the borderpane.
        borderPane.setTop(weightLabel);

        leftRegion = (Region) borderPane.getLeft();
        ((Region) borderPane.getLeft()).setPrefWidth(250);


        leftRegion.setStyle("-fx-background-color: rgb(197, 208, 240);");
        borderPane.setCenter(centerPane);


        //Event when a user clicks somewhere in the centerPane
        centerPane.setOnMouseClicked(event -> {
            //gets the position of where the event happened
            double x = event.getX();
            double y = event.getY();
            Button vertex;

            //If the button is clicked
            if (event.getSource() instanceof Button) {

                vertex = (Button) event.getSource();
            }
            //If we click somewhere in the pane
            else {
                vertex = new Button();
            }

            //The user data of the button is always its name
            Label locationLabel = new Label((String) vertex.getUserData());
            VBox location = new VBox(vertex, locationLabel);
            location.setAlignment(Pos.CENTER);
            //Because it wasn't really centered with the mouse event
            location.setLayoutX(x - 20);
            location.setLayoutY(y - 15);

            //the click event does not retrieve an instance of button (creating it)
            if (isAddVertex()) {
                centerPane.getChildren().add(location);
                //displays an input dialogue to get the name of the location
                Vertex.displayNameStage(vertex, locationLabel, false, x, y, centerPane, stage, location);


            }
            //if we're adding a path and we click somewhere inside the pane that is not a location, we cancel the action of trying to add a path
            else {
                setLocationCount(0);
                if (link[0] != null && link[0].getStyle().equals(PINK_STYLE))
                    link[0].setStyle(itineraryFile);
            }

            //changes the color of the button if we enter it and it is not chosen to add a path
            vertex.setOnMouseEntered(inEvent -> {
                if (!vertex.getStyle().equals(PINK_STYLE))
                    vertex.setStyle("-fx-background-color: rgb(101, 156, 201);");

            });
            //changes the color of the button if we exits it and it is not chosen to add a path
            vertex.setOnMouseExited(outEvent -> {
                if (!vertex.getStyle().equals(PINK_STYLE))
                    vertex.setStyle("-fx-background-color: rgb(14,103,135);");
            });
            //to drag the button
            vertex.setOnMousePressed(mousePressed -> {
               /* updates the position of the vBox based on the mouse's new position*/
                xOffset = mousePressed.getSceneX() - location.getLayoutX();
                yOffset = mousePressed.getSceneY() - location.getLayoutY();
            });

            vertex.setOnMouseDragged(mouseDragged -> {
                //sets the position of the vBox based on its initial position when the button was clicked was clicked
                location.setLayoutX(mouseDragged.getSceneX() - xOffset);
                location.setLayoutY(mouseDragged.getSceneY() - yOffset);
                //updates the lines
                updateLines(vertex);
                isVertexDragging = true;


            });

            vertex.setOnMouseReleased(mouseReleased -> {
                //because it could be just a click
                if (isVertexDragging)
                    System.out.println("Location \"" + vertex.getUserData() + "\" set to position (" + String.format("%.02f", mouseReleased.getSceneX()) + ", " + String.format("%.02f", mouseReleased.getSceneY()) + ")");
                isVertexDragging = false;
            });
            vertex.setOnAction(actionEvent -> {
                //if the button is clicked, modify it
                if (!isVertexDragging && isAddVertex()) {
                    Vertex.displayNameStage(vertex, locationLabel, true, x, y, centerPane, stage, location);
                }
                //if the user clicks on the same location again when it is meant to add a path, it cancels the actions
                else if (vertex.getStyle().equals(PINK_STYLE) && !isAddVertex() && !isVertexDragging) {
                    setLocationCount(0);
                    link[0].setStyle(itineraryFile);

                }
                //if we are initializing an "add path action"
                else if (!isVertexDragging && locationCount < 2 && !isAddVertex()) {

                    vertex.setStyle(PINK_STYLE);
                    locationCount++;

                    if (locationCount == 1) {
                        link[0] = vertex;
                    } else if (locationCount == 2) {
                        Line line;
                        link[1] = vertex;
                        line = new Line();
                        line.setStrokeWidth(1.5);
                        path = new Path(link[0], link[1], line);
                        addLine(line, link[0], link[1], borderPane);

                        link[0].setStyle(itineraryFile);
                        link[1].setStyle(itineraryFile);
                        setLocationCount(0);
                        linesMap.get(link[0]).add(path);
                        linesMap.get(link[1]).add(path);


                    }
                }
            });


        });


        Scene scene = new Scene(borderPane, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(itineraryFile);
        stage.setTitle("Itinerary Simulator");

        stage.setScene(scene);
        stage.show();
    }


    public void doShortestActions() {
        try {

            addDestinations(createSet());
            Vertex start = getLocationByName(vertices, startBox.getValue());
            //gets the set of the start value with all the other vertices
            Set<Vertex> setCalculated = calculateShortestPathFromSource(vertices, start);
            //gets the the vertex in the set from the start point
            Vertex end = getLocationByName(setCalculated, endBox.getValue());
            totalAmountLabel.setText(String.valueOf(end.getDistance()));
            //highlights the path in green
            tracePath(start, end);
        } catch (NullPointerException e) {
            //The end vertex is not inside of the the set connected to the start
            System.out.println("There is no connection to find!");
            totalAmountLabel.setText("0.0");
        }
    }

    //puts all the lines back to black
    public void resetStyles(Pane pane) {
        for (javafx.scene.Node node : pane.getChildren()) {
            if (node instanceof Line) {
                Line line = (Line) node;
                // Do something with the line
                line.setStroke(Color.BLACK);
            }
        }
    }

    public static Map<Button, List<Path>> getLinesMap() {
        return ItinerarySimulator.linesMap;
    }

    public static HashSet<Vertex> getVertices() {
        return ItinerarySimulator.vertices;
    }

    public static void setVertices(HashSet<Vertex> vertices) {
        ItinerarySimulator.vertices = vertices;
    }

    public static BorderPane getBorderPane() {
        return ItinerarySimulator.borderPane;
    }

    public static Label getWeightLabel() {
        return ItinerarySimulator.weightLabel;
    }

    public static void setWeightLabel(Label weightLabel) {
        ItinerarySimulator.weightLabel = weightLabel;
    }

    public static void setBorderPane(BorderPane borderPane) {
        ItinerarySimulator.borderPane = borderPane;
    }

    public static Stage getPrimaryStage() {
        return ItinerarySimulator.primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        ItinerarySimulator.primaryStage = primaryStage;
    }

    public static boolean isAddVertex() {
        return ItinerarySimulator.addVertex;
    }

    public static boolean isPriceOption() {
        return ItinerarySimulator.priceOption;
    }

    public static void setPriceOption(boolean priceOption) {
        ItinerarySimulator.priceOption = priceOption;
    }

    public static void setAddVertex(boolean addVertex) {
        ItinerarySimulator.addVertex = addVertex;
    }

    public static Label getTotalSign() {
        return ItinerarySimulator.totalSign;
    }

    public static Label getTotalLabel() {
        return ItinerarySimulator.totalLabel;
    }

    public static void setLocationCount(int locationCount) {
        ItinerarySimulator.locationCount = locationCount;
    }

    //Changes the position of the paths when a location is dragged
    public void updateLines(Button button) {

        for (int i = 0; i < linesMap.get(button).size(); i++) {
            linesMap.get(button).get(i).updateLine(button);
        }

    }

    //add the line where the end is  link[0] and start is link[1]
    public void addLine(Line line, Button button1, Button button2, Pane pane) {

        // First button pressed
        line.setStartX(button1.localToScene(button1.getBoundsInLocal()).getMinX() + button1.getWidth() / 2);
        line.setStartY(button1.localToScene(button1.getBoundsInLocal()).getMinY() + button1.getHeight() / 2);

        // Second button pressed
        line.setEndX(button2.localToScene(button2.getBoundsInLocal()).getMinX() + button2.getWidth() / 2);
        line.setEndY(button2.localToScene(button2.getBoundsInLocal()).getMinY() + button2.getHeight() / 2);
        // Add the line to the scene
        pane.getChildren().addAll(line);
    }


    //deletes the lines when the button is deleted
    public static void deleteLines(Button button) {
        for (int i = 0; i < linesMap.get(button).size(); i++) {
            getBorderPane().getChildren().remove(linesMap.get(button).get(i).getLine());
        }
        linesMap.get(button).clear();


    }


    public Vertex getLocationByName(Set<Vertex> set, String name) {

        for (Vertex ele : set) {
            if (ele.getName().equals(name)) {
                return ele;
            }
        }
        return null;
    }

    //get the other button based on the path
    public Button getOtherButton(Path path, Button button) {
        if (path.getstartLocation() == button) {
            return path.getendLocation();
        } else {
            return path.getstartLocation();
        }
    }

    /*Create a hashset of vertices. The button property will be set based on the button key of linesMap*/
    public HashSet<Vertex> createSet() {

        vertices = new HashSet<>();
        for (Map.Entry<Button, List<Path>> entry : linesMap.entrySet()) {
            vertices.add(new Vertex(entry.getKey()));
        }


        return vertices;
    }
    //We add properties to the existant set of vertices. (add destination:adjacentVertices.put(vertex, distance)
    public void addDestinations(HashSet<Vertex> set) {

        for (Vertex current : set) {
            //list of path related to the vertex in linesMap
            List<Path> list = linesMap.get(current.getButton());
            //loops through the paths.
            for (int i = 0; i < list.size(); i++) {
                for (Vertex ele : set) {
                    //if the vertices contains the adjacent vertex, we add the destination and destination weight rather than create it
                    if (ele.getButton().equals(getOtherButton(list.get(i), current.getButton()))) {

                        current.addDestination(ele, list.get(i).getWeight());

                    }

                }
            }
        }
    }

    //adds the button names to each of the two combo boxes
    public static void addNamesToComboBox(ComboBox box) {
        box.getItems().clear();
        for (Map.Entry<Button, List<Path>> entry : linesMap.entrySet()) {
            box.getItems().add(entry.getKey().getUserData());
        }
        if (box.equals(startBox))
            startBox.getItems().remove(endBox.getValue());
        else if (box.equals(endBox)) {
            endBox.getItems().remove(startBox.getValue());
        }
    }

    public List<Vertex> tracePath(Vertex start, Vertex end) {
        List<Vertex> path = new ArrayList<>();

        Vertex current = end;
        //loops until the current vertex is equal to the start vertex
        while (current != start) {
            path.add(current);
            current = current.previousVertex;

        }
        // Add the start vertex
        path.add(start);
        // Reverse the path to get it in correct order
        Collections.reverse(path);
        //To print the path
        System.out.print("Shortest path: ");
        for (int i = 0; i < path.size(); i++) {
            Button button = path.get(i).getButton();
            List<Path> paths = linesMap.get(button);
                if(i == 0){
                    System.out.print(button.getUserData() + " >>> ");
                }
            for (int j = 0; j < paths.size(); j++) {
                if (i != 0 && getOtherButton(paths.get(j), button).equals(path.get(i - 1).getButton())) {
                    System.out.print(button.getUserData() + " (" + paths.get(j).getWeight() + ") ");
                    if(i != path.size() -1 ){
                        System.out.print(" >>> ");
                    }
                    paths.get(j).getLine().setStroke(Color.rgb(44, 230, 90));
                }

            }

        }
        System.out.println(" displayed" + "\n");
        // To color the paths
        //for each vertex, examine all the paths
        for (int i = 0; i < path.size() - 1; i++) {
            Button button = path.get(i).getButton();
            List<Path> paths = linesMap.get(button);

            for (int j = 0; j < paths.size(); j++) {
                //if the start or end button of the path is the one after the current one in the set, then we know that we need to retrieve the path
                if ( getOtherButton(paths.get(j), button).equals(path.get(i + 1).getButton())) {

                    paths.get(j).getLine().setStroke(Color.rgb(44, 230, 90));
                }

            }

        }
        //returns the list of vertices
        return path;
    }


    public Set<Vertex> calculateShortestPathFromSource(Set<Vertex> set, Vertex startVertex) {

        //Because the distance from start to start is 0
        startVertex.setDistance(0.0);
        //Keeping a set of unvisited and visited Vertices.
        Set<Vertex> visited = new HashSet<>();
        Set<Vertex> unvisited = new HashSet<>();

        //Because we want to start by examining the first button
        unvisited.add(startVertex);
        //Stops when all the paths have been examined
        while (unvisited.size() != 0) {
            //Because the next vertex to examine is the one with the shortest distance from the previous one
            Vertex current = getLowestDistanceVertex(unvisited);
            //Because the current vertex has been visited
            unvisited.remove(current);

            for (Map.Entry<Vertex, Double> adjacent :
                    current.getAdjacentVertices().entrySet()) {
                Vertex adjacentVertex = adjacent.getKey();
                Double pathWeight = adjacent.getValue();
                if (!visited.contains(adjacentVertex)) {
                    calculateMinimumDistance(adjacentVertex, pathWeight, current);
                    unvisited.add(adjacentVertex);
                }
                visited.add(current);
            }

        }
        //if the end vertex is not in visited,then they are not linked together
        return visited;
    }

    //where the
    public Vertex getLowestDistanceVertex(Set<Vertex> unvisitedVertices) {
        Vertex lowestDistanceVertex = null;
        double lowestDistance = Double.MAX_VALUE;
        for (Vertex vertex : unvisitedVertices) {

            double vertexDistance = vertex.getDistance();
            //if the vertex has not been set to a lowest distance yet...
            if (vertexDistance < lowestDistance) {
                lowestDistance = vertexDistance;
                lowestDistanceVertex = vertex;
            }
        }
        return lowestDistanceVertex;
    }


    private static void calculateMinimumDistance(Vertex evaluationVertex,
                                                 Double pathWeight, Vertex sourceVertex) {
        Double sourceDistance = sourceVertex.getDistance();

        if (sourceDistance + pathWeight <= evaluationVertex.getDistance()) {

            evaluationVertex.setDistance(sourceDistance + pathWeight);
            LinkedList<Vertex> shortestPath = new LinkedList<>(sourceVertex.getShortestPath());
            shortestPath.add(sourceVertex);
            evaluationVertex.setShortestPath(shortestPath);
            evaluationVertex.setpreviousVertex(sourceVertex);

        }

    }

    public String getLOCATION_ICON_URL() {
        return this.LOCATION_ICON_URL;
    }

    public String getPATH_ICON_URL() {
        return this.PATH_ICON_URL;
    }

    public String getMONEY_ICON_URL() {
        return this.MONEY_ICON_URL;
    }

    public String getDISTANCE_ICON_URL() {
        return this.DISTANCE_ICON_URL;
    }

    public String getPINK_STYLE() {
        return this.PINK_STYLE;
    }

    public static int getLocationCount() {
        return ItinerarySimulator.locationCount;
    }

    public double getxOffset() {
        return this.xOffset;
    }

    public void setxOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    public double getyOffset() {
        return this.yOffset;
    }

    public void setyOffset(double yOffset) {
        this.yOffset = yOffset;
    }

    public boolean isVertexDragging() {
        return this.isVertexDragging;
    }

    public void setVertexDragging(boolean vertexDragging) {
        isVertexDragging = vertexDragging;
    }

    public Button[] getLink() {
        return this.link;
    }

    public void setLink(Button[] link) {
        this.link = link;
    }

    public String getitineraryFile() {
        return this.itineraryFile;
    }

    public Region getLeftRegion() {
        return leftRegion;
    }

    public void setLeftRegion(Region leftRegion) {
        this.leftRegion = leftRegion;
    }

    public static void setLinesMap(Map<Button, List<Path>> linesMap) {
        ItinerarySimulator.linesMap = linesMap;
    }

    public Path getPath() {
        return this.path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public static ComboBox<String> getStartBox() {
        return ItinerarySimulator.startBox;
    }

    public static void setStartBox(ComboBox<String> startBox) {
        ItinerarySimulator.startBox = startBox;
    }

    public static ComboBox<String> getEndBox() {
        return ItinerarySimulator.endBox;
    }

    public static void setEndBox(ComboBox<String> endBox) {
        ItinerarySimulator.endBox = endBox;
    }

    public static void setTotalLabel(Label totalLabel) {
        ItinerarySimulator.totalLabel = totalLabel;
    }

    public static Label getTotalAmountLabel() {
        return ItinerarySimulator.totalAmountLabel;
    }

    public static void setTotalAmountLabel(Label totalAmountLabel) {
        ItinerarySimulator.totalAmountLabel = totalAmountLabel;
    }

    public String getItineraryFile() {
        return this.itineraryFile;
    }

    public static void setTotalSign(Label totalSign) {
        ItinerarySimulator.totalSign = totalSign;
    }
}


