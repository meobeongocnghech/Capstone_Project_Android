package com.viettrekker.mountaintrekkingadviser.model;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchPlace implements Searchable {
    private String mTitle;
    private int id;

    public SearchPlace(String title, int id) {
        mTitle = title;
        this.id = id;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public SearchPlace setTitle(String title) {
        mTitle = title;
        return this;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
