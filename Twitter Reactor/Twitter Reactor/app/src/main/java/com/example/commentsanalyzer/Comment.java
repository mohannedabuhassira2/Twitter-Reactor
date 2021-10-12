package com.example.commentsanalyzer;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
    private long id;
    private String profilePicUrl;
    private String name;
    private String fullText;
    private int prediction;

    public Comment(long id, String profilePicUrl, String name, String fullText, int prediction) {
        this.id = id;
        profilePicUrl = profilePicUrl.replace("_normal", "");
        this.profilePicUrl = profilePicUrl;
        this.name = name;
        this.fullText = fullText;
        this.prediction = prediction;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getProfilePicUrl() { return profilePicUrl; }

    public String getName() {
        return name;
    }

    public String getFullText() {
        return fullText;
    }

    public int getPrediction() { return prediction; }

    public void setPrediction(int prediction) { this.prediction = prediction; }

    protected Comment(Parcel in) {
        profilePicUrl = in.readString();
        name = in.readString();
        fullText = in.readString();
        prediction = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(profilePicUrl);
        dest.writeString(name);
        dest.writeString(fullText);
        dest.writeInt(prediction);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}