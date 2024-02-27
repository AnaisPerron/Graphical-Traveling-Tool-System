package com.example.graphicaltravelingtoolsystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class LoginPage extends Application implements Serializable  {
    private final double SCENE_WIDTH = 700;
    private final double SCENE_HEIGHT = 500;
    private  final String signupFile = Objects.requireNonNull(getClass().getResource("loginpage.css")).toExternalForm();


    private Button doneButton = new Button("Create Account");

    private Label idLabel = new Label("Username:");
    private TextField idTextField;

    private static boolean create = true;
    private Label passwordLabel = new Label("Password:");
    private PasswordField passwordField;
    private Label errorLabel = new Label();
    private Label questionLabel = new Label("Already have an account?");
    private Label changeOptionLabel = new Label("Sign in!");
    //private final String fileName = "C:\\Users\\anana\\IdeaProjects\\GraphicalTravelingToolSystem\\target\\classes\\com\\example\\graphicaltravelingtoolsystem\\accounts.ser";;// File to store serialized accounts
    static String currentDir = System.getProperty("user.dir");
   static String fileName = currentDir + "\\target\\classes\\com\\example\\graphicaltravelingtoolsystem\\accounts.ser";
    //File fileName = new File("accounts.ser");

    private static HashSet<Account> accountSet = getaccountSetFromSerializedFile();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        errorLabel.setTextFill(Color.RED);

         Label welcomeLabel = new Label("Welcome to your personalized GTTS!");
         welcomeLabel.setStyle("-fx-font-size: 15px;");

        idTextField = new TextField();
        idTextField.setPromptText("Username");
        idTextField.setMaxWidth(200);
        idTextField.setAlignment(Pos.CENTER);



        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(200);
         passwordField.setAlignment(Pos.CENTER);

        VBox optionVbox = new VBox(10,welcomeLabel, idTextField, passwordField);
        optionVbox.setAlignment(Pos.CENTER);
        optionVbox.setPadding(new Insets(10));

        changeOptionLabel.setUnderline(true);
        changeOptionLabel.setTextFill(Color.rgb(68, 74, 89));
        VBox createOrSignInVbox = new VBox(2, questionLabel, changeOptionLabel);
        createOrSignInVbox.setAlignment(Pos.CENTER);
        createOrSignInVbox.setPadding(new Insets(10));
        VBox entireVbox = new VBox(optionVbox, doneButton, createOrSignInVbox);
        entireVbox.setAlignment(Pos.CENTER);

        
        changeOptionLabel.setOnMouseEntered(event ->{
            changeOptionLabel.setTextFill(Color.LIGHTGRAY);
            
            
        });

        changeOptionLabel.setOnMouseExited(event ->{
            changeOptionLabel.setTextFill(Color.rgb(68, 74, 89));
            

        });
        
        changeOptionLabel.setOnMouseClicked(event ->{
            setCreate(!isCreate());
            if(!create) {
                getquestionLabel().setText("First time here?");
                getChangeOptionLabel().setText("Create an Account!");
                doneButton.setText("Sign in");
            }
            else{
                getquestionLabel().setText("Already have an account?");
                getChangeOptionLabel().setText("Sign in!");
                doneButton.setText("Create account");

            }
        });

        doneButton.setOnAction(event ->{
            if (passwordField.getText().isEmpty() || idTextField.getText().isEmpty()) {
                displayAlert("Information incomplete", "Blank Text Error");
            }
            else if(create) {
                if (existingAccount(idTextField.getText())) {
                    displayAlert("This account already exists!", "Existing Account Error");
                } else {
                    Account account = new Account(idTextField.getText(), passwordField.getText());
                    this.accountSet.add(account);
                    System.out.println("Your account has been created.");
                    try {
                        clearSerializedData();
                        serializeData();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    serializeData();
                    for (Account acc : getaccountSetFromSerializedFile()) {
                        System.out.println(acc.getUsername());
                    }
                    AccountPage accountPage = new AccountPage(account);
                    Stage stage = new Stage();
                    try {
                        accountPage.start(stage);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    primaryStage.close();
                }
            }
            else{
                if(accountUsernameNotFound(idTextField.getText())){
                    displayAlert("This account does not exist!", "Account does not exist Error ");
                }
                else if(wrongPassword(idTextField.getText(), passwordField.getText())){
                    displayAlert("Wrong Password", "Wrong Password Error ");

                }
                else{
                    AccountPage accountPage = new AccountPage(getAccount(idTextField.getText(), passwordField.getText()));
                    Stage stage = new Stage();
                    try {
                        accountPage.start(stage);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    primaryStage.close();
                }
            }
        });
         Scene scene = new Scene(entireVbox,SCENE_WIDTH,SCENE_HEIGHT);
         scene.getStylesheets().add(signupFile);
         primaryStage.setScene(scene);
         primaryStage.setTitle("Log in Screen");
         primaryStage.show();

    }


    public void displayAlert(String header, String title){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setTitle(title);
        System.out.println(alert.getTitle() + " displayed");
        alert.showAndWait();
    }


    public static HashSet<Account> getaccountSetFromSerializedFile() {
        HashSet<Account> serAccountSet = null;

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            Object obj = inputStream.readObject();

            if (obj instanceof HashSet) {
                serAccountSet = (HashSet<Account>)  obj;
            }
        }
        catch (InvalidClassException i){
            try {
                clearSerializedData();
            } catch (IOException e) {
                return new HashSet<Account>();
            }

        }
        catch (EOFException e) {
            System.out.println("The file is empty... time to create accounts!");
            return new HashSet<Account>();
        }
        catch (ClassNotFoundException e) {
            System.out.println("CLass not found :(");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(serAccountSet != null) {
            return serAccountSet;
        }
        else {
            System.out.println("serAccountSet null");
            return new HashSet<Account>();
        }
    }

    public static void serializeData(){
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            outputStream.writeObject(accountSet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to clear the data in the serialized file
    public static void clearSerializedData() throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            // Write an empty object or null to the file
            outputStream.writeObject(null);
        }
    }
    //This method is created in order for each account to have a unique username
    public boolean existingAccount(String usedId){
        for (Account acc : accountSet){
            if (acc.getUsername().equals(usedId)){
                return true;
            }
        }
        return false;
    }

    public boolean accountUsernameNotFound(String id){
        for (Account acc : accountSet){
            if (acc.getUsername().equals(id)){
                return false;
            }
        }
        return true;
    }

    public Account getAccount(String id, String password){
        for (Account acc : accountSet){
            if (acc.getUsername().equals(id) && acc.getPassword().equals(password)){
                return acc;
            }
        }
        return null;
    }
    public boolean wrongPassword(String id, String password){
        for (Account acc : accountSet){
            if (acc.getUsername().equals(id) && acc.getPassword().equals(password)){
                return false;
            }
        }
        return true;
    }
    public double getSCENE_WIDTH() {
        return this.SCENE_WIDTH;
    }

    public double getSCENE_HEIGHT() {
        return this.SCENE_HEIGHT;
    }

    public String getSignupFile() {
        return this.signupFile;
    }

    public Button getDoneButton() {
        return this.doneButton;
    }

    public void setDoneButton(Button doneButton) {
        this.doneButton = doneButton;
    }

    public Label getIdLabel() {
        return this.idLabel;
    }

    public void setIdLabel(Label idLabel) {
        this.idLabel = idLabel;
    }

    public TextField getIdTextField() {
        return this.idTextField;
    }

    public void setIdTextField(TextField idTextField) {
        this.idTextField = idTextField;
    }

    public static boolean isCreate() {
        return LoginPage.create;
    }

    public static void setCreate(boolean create) {
        LoginPage.create = create;
    }

    public Label getPasswordLabel() {
        return this.passwordLabel;
    }

    public void setPasswordLabel(Label passwordLabel) {
        this.passwordLabel = passwordLabel;
    }

    public TextField getpasswordField() {
        return this.passwordField;
    }

    public void setpasswordField(PasswordField passwordField) {
        this.passwordField = passwordField;
    }

    public Label getErrorLabel() {
        return this.errorLabel;
    }

    public void setErrorLabel(Label errorLabel) {
        this.errorLabel = errorLabel;
    }

    public Label getquestionLabel() {
        return this.questionLabel;
    }

    public void setquestionLabel(Label questionLabel) {
        this.questionLabel = questionLabel;
    }

    public Label getChangeOptionLabel() {
        return this.changeOptionLabel;
    }

    public void setChangeOptionLabel(Label changeOptionLabel) {
        this.changeOptionLabel = changeOptionLabel;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(PasswordField passwordField) {
        this.passwordField = passwordField;
    }

    public Label getQuestionLabel() {
        return questionLabel;
    }

    public void setQuestionLabel(Label questionLabel) {
        this.questionLabel = questionLabel;
    }



    public HashSet<Account> getAccountSet() {
        return this.accountSet;
    }

    public void setAccountSet(HashSet<Account> accountSet) {
        this.accountSet = accountSet;
    }

    public HashSet<Account> getaccountSet() {

        return this.accountSet;
    }





}
