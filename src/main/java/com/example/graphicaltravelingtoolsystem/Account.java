package com.example.graphicaltravelingtoolsystem;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Account implements Serializable {
    private String username;
    private String password;

    private Set<Journey> journeys;







    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.journeys = new HashSet<Journey>();

    }


    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Journey> getJourneys() {
        return this.journeys;
    }

    public void setJourneys(Set<Journey> journeys) {
        this.journeys = journeys;
    }
}
