package com.speedy.radar;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Speedy on 2017/5/4.
 */

public class Friend implements Parcelable{

    public String name;

    public int imageResId;

    public Friend(){

    }

    public Friend(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    protected Friend(Parcel in) {
        name = in.readString();
        imageResId = in.readInt();
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(imageResId);
    }
}
