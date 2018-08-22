package com.viettrekker.mountaintrekkingadviser.model;

import ir.mirrajabi.searchdialog.core.Searchable;

public class PlanOwn implements Searchable {
    private String mTitle;
    private int id;

    @Override
    public String getTitle() {
        return mTitle;
    }

    public PlanOwn(String mTitle, int id) {
        this.mTitle = mTitle;
        this.id = id;
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
