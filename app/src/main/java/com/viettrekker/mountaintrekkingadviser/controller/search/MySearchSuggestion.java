package com.viettrekker.mountaintrekkingadviser.controller.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class MySearchSuggestion implements SearchSuggestion {

    private String mTitle;
    private String mImagePath;
    private int id;
    private boolean mIsHistory = false;

    public MySearchSuggestion(String mTitle, String mImagePath, int id) {
        this.mTitle = mTitle;
        this.mImagePath = mImagePath;
        this.id = id;
    }

    public MySearchSuggestion(Parcel source) {
        String [] data =  new String[2];
        source.readStringArray(data);
        mTitle = data[0];
        mImagePath = data[1];
        mIsHistory = source.readLong() == 1 ? true : false;
        id = source.readInt();
    }

    public void setIsHistory(boolean mIsHistory) {
        this.mIsHistory = mIsHistory;
    }

    public boolean isHistory() {
        return mIsHistory;
    }

    @Override
    public String getBody() {
        return mTitle + '\n' + mImagePath + '\n' + id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{mTitle, mImagePath});
        parcel.writeLong(mIsHistory ? 1 : 0);
        parcel.writeInt(id);
    }

    public static final Creator<MySearchSuggestion> CREATOR = new Creator<MySearchSuggestion>() {
        @Override
        public MySearchSuggestion createFromParcel(Parcel in) {
            return new MySearchSuggestion(in);
        }

        @Override
        public MySearchSuggestion[] newArray(int size) {
            return new MySearchSuggestion[size];
        }
    };
}
