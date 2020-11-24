package com.example.hnsupgrade;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class gameInfo implements Serializable{
    private String lobbyName;
    private boolean hints;
    private double circleSize;
    private String circleCenterPicked;
    private String lastHintUsed;
    private int totalHintsUsed;
    private boolean hiderReady;
    private boolean hasSeekerReachedCircle;
    private String seekerLocation;
    private boolean hiderFound;
    private String timeTaken;

    public gameInfo() {
    }

    public gameInfo(String lobbyName, boolean hints, double circleSize, String circleCenterPicked, String lastHintUsed, int totalHintsUsed, boolean hiderReady, boolean hasSeekerReachedCircle, String seekerLocation, boolean hiderFound, String timeTaken) {
        this.lobbyName = lobbyName;
        this.hints = hints;
        this.circleSize = circleSize;
        this.circleCenterPicked = circleCenterPicked;
        this.lastHintUsed = lastHintUsed;
        this.totalHintsUsed = totalHintsUsed;
        this.hiderReady = hiderReady;
        this.hasSeekerReachedCircle = hasSeekerReachedCircle;
        this.seekerLocation = seekerLocation;
        this.hiderFound = hiderFound;
        this.timeTaken = timeTaken;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public void setHiderFound(boolean hiderFound) {
        this.hiderFound = hiderFound;
    }

    public boolean isHiderFound() {
        return hiderFound;
    }

    public String getSeekerLocation() {
        return seekerLocation;
    }

    public void setSeekerLocation(String seekerLocation) {
        this.seekerLocation = seekerLocation;
    }

    public boolean isHasSeekerReachedCircle() {
        return hasSeekerReachedCircle;
    }

    public void setHasSeekerReachedCircle(boolean hasSeekerReachedCircle) {
        this.hasSeekerReachedCircle = hasSeekerReachedCircle;
    }



    public boolean isHiderReady() {
        return hiderReady;
    }

    public void setHiderReady(boolean hiderReady) {
        this.hiderReady = hiderReady;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public void setHints(boolean hints) {
        this.hints = hints;
    }

    public void setCircleSize(double circleSize) {
        this.circleSize = circleSize;
    }

    public void setCircleCenterPicked(String circleCenterPicked) {
        this.circleCenterPicked = circleCenterPicked;
    }

    public void setLastHintUsed(String lastHintUsed) {
        this.lastHintUsed = lastHintUsed;
    }

    public void setTotalHintsUsed(int totalHintsUsed) {
        this.totalHintsUsed = totalHintsUsed;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public boolean isHints() {
        return hints;
    }

    public double getCircleSize() {
        return circleSize;
    }

    public String getCircleCenterPicked() {
        return circleCenterPicked;
    }

    public String getLastHintUsed() {
        return lastHintUsed;
    }

    public int getTotalHintsUsed() {
        return totalHintsUsed;
    }
}
