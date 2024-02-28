package com.example.listcase;

import java.util.ArrayList;
import java.util.Objects;

public class CategoryProperty {
    private int id;
    private String title;

    private byte[] blob;

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public static int getPostionItem(ArrayList<CategoryProperty> arr, int title)
    {
        for (int i=0; i < arr.size();i++) if (arr.get(i).getId() == title) return i;
        return -1;
    }

    public CategoryProperty(String title) {
        this.title = title;
    }

    public CategoryProperty() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
