package com.example.graphicaltravelingtoolsystem;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;


public class AccountPage extends Application {
    final double SCENE_HEIGHT = 800;
    final double SCENE_WIDTH = 1200;
    private Account account;
    //boolean to know if the user either creates objects or modifies them
    private boolean create;

    private Stage stage;
    private static Button clickedButton;
    private BorderPane borderPane = new BorderPane();
    private Label usernameLabel = new Label();

    private VBox journeysVbox;

    private static ListView listView = new ListView();

    private Region leftRegion = new Region();
    private Button createJourney;
    private final String PINK_STYLE = "-fx-background-color: rgb(245, 179, 232);";

    private Button itinerarySimulator;

    private Button createButton;

    private static Label totalLabel = new Label();

    private VBox createAndGetJourneys;

    private List<Button> journeyButtons;

    private final String ACCOUNTSTYLE = Objects.requireNonNull(getClass().getResource("accountpage.css")).toExternalForm();
    private final String ACCOUNT_ICON_URL = Objects.requireNonNull(getClass().getResource("profile.png")).toExternalForm();

    private HBox destExpHbox;

    public AccountPage(Account account) {

        this.account = account;
        this.usernameLabel.setText(this.account.getUsername());

    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        clickedButton = null;
        create = true;
        stage = primaryStage;

        Image accountImage = new Image(ACCOUNT_ICON_URL);
        ImageView accountImageView = new ImageView(accountImage);
        accountImageView.setFitHeight(50);
        accountImageView.setPreserveRatio(true);


        //Button to create the journey
        createJourney = new Button("+ Create Journey");
        createJourney.getStyleClass().add("button-create");
        journeyButtons = new ArrayList<>();
        //Vbox of journeys, in the leftPane

        addJourneyButtons();
        journeysVbox.setAlignment(Pos.CENTER);
        journeysVbox.setSpacing(10);
        journeysVbox.setPadding(new Insets(10));
        addActionsToJourneyButtons();
        createAndGetJourneys = new VBox(createJourney, journeysVbox);
        createAndGetJourneys.setAlignment(Pos.TOP_CENTER);
        createAndGetJourneys.setPadding(new Insets(10));

        createJourney.setOnAction(actionEvent -> {
            if (create) {
                Button journeyButton = new Button();
                Journey journey = new Journey();
                journeyButtons.add(journeyButton);
                journey.setAccount(this.account);
                journeyButton.setUserData(journey);
                addActionsToJourneyButtons();
                this.account.getJourneys().add(journey);
                journeyButton.getStyleClass().add("button-journeys");

                journey.displayStage(primaryStage, journeysVbox, journeyButton, create, this.account);
            }


        });
        journeysVbox.setAlignment(Pos.CENTER);

        primaryStage.setOnCloseRequest(event -> {
            LoginPage.serializeData();
        });
        borderPane.setLeft(createAndGetJourneys);
        leftRegion = (Region) borderPane.getLeft();
        ((Region) borderPane.getLeft()).setPrefWidth(250);
        leftRegion.setStyle("-fx-background-color: rgb(197, 208, 240);");


        createButton = new Button("Modify");
        createButton.getStyleClass().add("button-modify");
        createButton.setOnAction(event -> {
            setCreate(!isCreate());
            if (create) {
                createButton.setText("Modify");
                createJourney.setVisible(true);

            } else {
                createButton.setText("Create");
                createJourney.setVisible(false);

            }
        });

        //Top right being the account
        itinerarySimulator = new Button("Itinerary Simulator");
        itinerarySimulator.getStyleClass().add("button-itinerary");
        itinerarySimulator.setOnMouseEntered(action -> {
            buttonTransition(this.itinerarySimulator).play();
        });
        itinerarySimulator.setOnMouseEntered(action -> {
            reversebuttonTransition(this.itinerarySimulator).play();
        });
        itinerarySimulator.setOnAction(event -> {
            Stage stage = new Stage();
            ItinerarySimulator is = new ItinerarySimulator();
            try {
                is.start(stage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.usernameLabel.setMinHeight(40);
        HBox account = new HBox(10, itinerarySimulator, accountImageView, this.usernameLabel);
        account.setAlignment(Pos.CENTER_RIGHT);


        createButton.setAlignment(Pos.CENTER_LEFT);
        createButton.setPadding(new Insets(10));
        HBox topHBox = new HBox(2 * SCENE_WIDTH / 3, createButton, account);
        topHBox.setPadding(new Insets(10));


        borderPane.setTop(topHBox);


        Button plusButtond = new Button("+");
        plusButtond.getStyleClass().add("button-plusd");
        plusButtond.setOnAction(event -> {
                    Destination dest = new Destination();
                    if (clickedButton != null && create) {
                        Journey jour = (Journey) clickedButton.getUserData();
                        dest.displayStage(primaryStage, true, jour);

                    }
                }
        );

        Button plusButtone = new Button("+");
        plusButtone.getStyleClass().add("button-pluse");
        plusButtone.setOnAction(event -> {

                    if (clickedButton != null && create) {
                        Expense expense = new Expense();
                        Journey jour = (Journey) clickedButton.getUserData();
                        expense.displayStage(primaryStage, jour, true);

                    }
                }
        );


        destExpHbox = new HBox(10, totalLabel, plusButtond, plusButtone);
        destExpHbox.setAlignment(Pos.BOTTOM_RIGHT);
        destExpHbox.setPadding(new Insets(10));


        listView.setOnMouseClicked(mouseEvent -> {
            if(!create && listView.getSelectionModel().getSelectedItem() != null){
                if(listView.getSelectionModel().getSelectedItem() instanceof Destination){
                    Destination dest = (Destination) listView.getSelectionModel().getSelectedItem();
                    dest.displayStage(primaryStage, false, (Journey) clickedButton.getUserData());
                }
                else if(listView.getSelectionModel().getSelectedItem() instanceof Expense){
                    Expense exp = (Expense) listView.getSelectionModel().getSelectedItem();
                    exp.displayStage(primaryStage, (Journey) clickedButton.getUserData(),false);
                }
            }
        });
        listView.setPrefSize(80, 500);
        VBox listViewVbox = new VBox(listView);
        listViewVbox.setAlignment(Pos.CENTER);

        VBox centerVbox = new VBox(10, listViewVbox, destExpHbox);


        borderPane.setCenter(centerVbox);


        Scene scene = new Scene(borderPane, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(ACCOUNTSTYLE);
        primaryStage.setTitle("Account Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void addJourneyButtons() {
        this.journeysVbox = new VBox();
        Set<Journey> journeys = this.account.getJourneys();
        for (Journey journey : journeys) {
            System.out.println(journey.getName());
            Button button = new Button(journey.getName());
            button.setUserData(journey);
            button.getStyleClass().add("button-journeys");
            this.journeysVbox.getChildren().add(button);
            this.journeyButtons.add(button);
        }
    }

    /*This is in order to set action events to all journey buttons (so it gets bigger when
    the user enters the mouse on it and it gets pink when the user clicks on it*/
    public void addActionsToJourneyButtons() {
        for (int i = 0; i < journeyButtons.size(); i++) {
            Button b = journeyButtons.get(i);
            b.setOnAction(actionEvent -> {
                System.out.println(b.getText() + " clicked!");
                resetStyle();
                b.setStyle(PINK_STYLE);
                Journey j =(Journey) b.getUserData();
                clickedButton = b;
                if(!create)
                j.displayStage(stage,journeysVbox,b,false,this.account);

                addToListView();

            });

            b.setOnMouseEntered(event -> {
                buttonTransition(b).play();

            });
            b.setOnMouseExited(event -> {
                reversebuttonTransition(b).play();
            });


        }

    }

    /*These methods are used to enlarge a button when the user's mouse enters the button and to return it
    to its original size (once the user's button exits the button)*/
    public static ScaleTransition buttonTransition(Button button) {
        ScaleTransition enlargeTransition = new ScaleTransition(Duration.millis(200), button);
        enlargeTransition.setToX(1.7);
        enlargeTransition.setToY(1.7);
        return enlargeTransition;
    }

    public static ScaleTransition reversebuttonTransition(Button button) {
        ScaleTransition originalSizeTransition = new ScaleTransition(Duration.millis(200), button);
        originalSizeTransition.setToX(1);
        originalSizeTransition.setToY(1);
        return originalSizeTransition;
    }


    //resets all the buttons of the journeysVbox to their original style
    public void resetStyle() {
        for (Node child : journeysVbox.getChildren()) {
            if (child instanceof Button) {
                Button button = (Button) child;
                if (button.getStyle().equals(PINK_STYLE)) {
                    button.setStyle("button-journeys");
                }
            }
        }
    }

    public static ListView getListView() {
        return AccountPage.listView;
    }

    public static void setListView(ListView listView) {
        AccountPage.listView = listView;
    }

    public static Button getClickedButton() {
        return clickedButton;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public static void setClickedButton(Button clickedButton) {
        AccountPage.clickedButton = clickedButton;
    }

    public static void addToListView() {
        if (getListView().getItems() != null) {
            getListView().getItems().clear();
        }
        if(clickedButton != null) {
            double expenseSum = 0.0;
            Journey curr = (Journey) getClickedButton().getUserData();

            for (int i = 0; i < curr.getDestinations().size(); i++) {

                double destinationExpenseSum = 0.0;
                getListView().getItems().add(curr.getDestinations().get(i));

                Destination currDestination = curr.getDestinations().get(i);
                for (int j = 0; j < curr.getDestinations().get(i).getExpenses().size(); j++) {
                    getListView().getItems().add(curr.getDestinations().get(i).getExpenses().get(j));
                    double expensePrice = curr.getDestinations().get(i).getExpenses().get(j).getPrice();
                    expenseSum += expensePrice;
                    destinationExpenseSum += expensePrice;

                }
                Label expenseSumlabel = new Label("Destination budget: " + currDestination.getBudget() + "$ Spendings: " + destinationExpenseSum + "$");
                if (currDestination.getBudget() >= destinationExpenseSum) {
                    expenseSumlabel.setTextFill(Color.GREEN);
                } else {
                    expenseSumlabel.setTextFill(Color.RED);
                }
                getListView().getItems().add(expenseSumlabel);
                getListView().getItems().add("");

            }
            totalLabel.setText("Journey budget: " + curr.getBudget() + "$ Spendings: " + expenseSum + "$");
            if (curr.getBudget() >= expenseSum) {
                totalLabel.setTextFill(Color.GREEN);
            } else {
                totalLabel.setTextFill(Color.RED);
            }
        }


    }

    public double getSCENE_HEIGHT() {
        return this.SCENE_HEIGHT;
    }

    public double getSCENE_WIDTH() {
        return this.SCENE_WIDTH;
    }

    public Account getAccount() {
        return this.account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public BorderPane getBorderPane() {
        return this.borderPane;
    }

    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
    }

    public Label getUsernameLabel() {
        return this.usernameLabel;
    }

    public void setUsernameLabel(Label usernameLabel) {
        this.usernameLabel = usernameLabel;
    }

    public VBox getJourneysVbox() {
        return this.journeysVbox;
    }

    public void setJourneysVbox(VBox journeysVbox) {
        this.journeysVbox = journeysVbox;
    }

    public Region getLeftRegion() {
        return this.leftRegion;
    }

    public void setLeftRegion(Region leftRegion) {
        this.leftRegion = leftRegion;
    }

    public Button getCreateJourney() {
        return this.createJourney;
    }

    public void setCreateJourney(Button createJourney) {
        this.createJourney = createJourney;
    }

    public String getPINK_STYLE() {
        return this.PINK_STYLE;
    }

    public Button getItinerarySimulator() {
        return this.itinerarySimulator;
    }

    public void setItinerarySimulator(Button itinerarySimulator) {
        this.itinerarySimulator = itinerarySimulator;
    }

    public Button getCreateButton() {
        return this.createButton;
    }

    public void setCreateButton(Button createButton) {
        this.createButton = createButton;
    }

    public static Label getTotalLabel() {
        return AccountPage.totalLabel;
    }

    public static void setTotalLabel(Label totalLabel) {
        AccountPage.totalLabel = totalLabel;
    }

    public VBox getCreateAndGetJourneys() {
        return this.createAndGetJourneys;
    }

    public void setCreateAndGetJourneys(VBox createAndGetJourneys) {
        this.createAndGetJourneys = createAndGetJourneys;
    }

    public List<Button> getJourneyButtons() {
        return this.journeyButtons;
    }

    public void setJourneyButtons(List<Button> journeyButtons) {
        this.journeyButtons = journeyButtons;
    }

    public String getACCOUNTSTYLE() {
        return this.ACCOUNTSTYLE;
    }

    public String getACCOUNT_ICON_URL() {
        return this.ACCOUNT_ICON_URL;
    }

    public HBox getDestExpHbox() {
        return this.destExpHbox;
    }

    public void setDestExpHbox(HBox destExpHbox) {
        this.destExpHbox = destExpHbox;
    }
}
