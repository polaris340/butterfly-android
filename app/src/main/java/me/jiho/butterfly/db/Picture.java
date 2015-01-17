package me.jiho.butterfly.db;

import android.graphics.Color;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by jiho on 1/13/15.
 */

// will changed to db table
public class Picture {
    private long id;
    private String title;
    private String uploaderName;
    private String thumbnailUrl;
    private String pictureUrl;
    private double latitude;
    private double longitude;
    private float imageRatio;
    private int originalWidth;
    private int originalHeight;
    private String capturedTime;
    private int likeCount;
    private boolean isLiked;
    private String primaryColor;


    public Picture() {

    }

    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }
    public String getUploaderName() {
        return uploaderName;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public void setImageRatio(float imageRatio) {
        this.imageRatio = imageRatio;
    }
    public float getImageRatio() {
        return imageRatio;
    }

    public void setOriginalWidth(int originalWidth) {
        this.originalWidth = originalWidth;
    }
    public int getOriginalWidth() {
        return originalWidth;
    }

    public void setOriginalHeight(int originalHeight) {
        this.originalHeight = originalHeight;
    }
    public int getOriginalHeight() {
        return originalHeight;
    }

    public void setCapturedTime(String capturedTime) {
        this.capturedTime = capturedTime;
    }
    public String getCapturedTime() {
        return capturedTime;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    public int getLikeCount() {
        return likeCount;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }
    public boolean getIsLiked() {
        return isLiked;
    }

    public void setPrimaryColor(String color) {
        primaryColor = color;
    }
    public String getPrimaryColor() {
        return primaryColor;
    }

    public int getColor() {
        return Color.parseColor(primaryColor);
    }

    public static Picture fromJson(String jsonString) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return gson.fromJson(jsonString, Picture.class);
    }

    public static Picture[] fromJsonArray(String jsonArrayString) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.fromJson(jsonArrayString, Picture[].class);
    }
}
