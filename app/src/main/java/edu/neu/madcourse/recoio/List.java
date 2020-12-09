package edu.neu.madcourse.recoio;

import java.util.ArrayList;

public class List {
    private String listName;
    private String listUID;
    private final ArrayList<String> listReviews;

    public List(String listUID, String listName) {
        this.listName = listName;
        this.listUID = listUID;
        this.listReviews = new ArrayList<>();
    }

    public String getListName() {
        return listName;
    }

    public String getListUID() {
        return listUID;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public ArrayList<String> getListReviews() {
        return listReviews;
    }
}
