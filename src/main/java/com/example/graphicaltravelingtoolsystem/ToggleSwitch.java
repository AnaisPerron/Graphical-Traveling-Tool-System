package com.example.graphicaltravelingtoolsystem;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ToggleSwitch extends Parent {
    private BooleanProperty switchedOn = new SimpleBooleanProperty(false);
    private TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100));


    private FillTransition fillTransition = new FillTransition(Duration.millis(100));

    private ParallelTransition animation = new ParallelTransition(translateTransition, fillTransition);


    public ToggleSwitch(boolean isPathToggle) {

        final double BACK_WIDTH = 50;
        final double BACK_HEIGHT = 25;



        Rectangle background = new Rectangle(BACK_WIDTH, BACK_HEIGHT);
        background.setArcWidth(BACK_HEIGHT);
        background.setArcHeight(BACK_HEIGHT);
        background.setFill(Color.WHITE);
        background.setStroke(Color.LIGHTGRAY);

        Circle circle = new Circle(BACK_HEIGHT / 2);
        circle.setCenterX(BACK_HEIGHT / 2);
        circle.setCenterY(BACK_HEIGHT / 2);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.LIGHTGRAY);

        translateTransition.setNode(circle);
        fillTransition.setShape(background);


        getChildren().addAll(background, circle);

        switchedOn.addListener((value, oldvalue, newvalue) -> {
            boolean isOn = newvalue.booleanValue();
            translateTransition.setToX(isOn ? BACK_WIDTH - BACK_HEIGHT : 0);
            fillTransition.setFromValue(isOn ? Color.WHITE : Color.rgb(71, 113, 128));
            fillTransition.setToValue(isOn ? Color.rgb(71, 113, 128) : Color.WHITE);
            animation.play();
            if(isPathToggle) {
                if(isOn){
                    System.out.println("Option: Add Path");
                }
                else{System.out.println("Option: Add Location");}

            }
            else{
                if(isOn) {
                    System.out.println("Option: Calculate Distance");
                    ItinerarySimulator.getTotalLabel().setText("Total Distance: ");
                    ItinerarySimulator.getTotalSign().setText("km");
                }
                else{
                    System.out.println("Option: Calculate Price");
                    ItinerarySimulator.getTotalLabel().setText("Total Price: ");
                    ItinerarySimulator.getTotalSign().setText("$");

                }

            }

        });

        setOnMouseClicked(event -> {
            switchedOn.set(!switchedOn.get());
            if(isPathToggle) {
                ItinerarySimulator.setAddVertex(!ItinerarySimulator.isAddVertex());

            }
            else{
                ItinerarySimulator.setPriceOption(!ItinerarySimulator.isPriceOption());
            }

        });


    }

    public boolean isSwitchedOn() {
        return switchedOn.get();
    }

    public BooleanProperty switchedOnProperty() {
        return this.switchedOn;
    }

    public void setSwitchedOn(boolean switchedOn) {
        this.switchedOn.set(switchedOn);
    }

    public TranslateTransition getTranslateTransition() {
        return this.translateTransition;
    }

    public void setTranslateTransition(TranslateTransition translateTransition) {
        this.translateTransition = translateTransition;
    }

    public FillTransition getFillTransition() {
        return this.fillTransition;
    }

    public void setFillTransition(FillTransition fillTransition) {
        this.fillTransition = fillTransition;
    }

    public ParallelTransition getAnimation() {
        return this.animation;
    }

    public void setAnimation(ParallelTransition animation) {
        this.animation = animation;
    }
}