package com.viettrekker.mountaintrekkingadviser.model;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchMember implements Searchable{
    private String mTitle;
    private int id;
    private String firstName;
    private String lastName;

    public SearchMember(String title, int id) {
        mTitle = title;
        this.id = id;
    }

    public SearchMember(String mTitle, int id, String firstName, String lastName) {
        this.mTitle = mTitle;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public SearchMember setTitle(String title) {
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
