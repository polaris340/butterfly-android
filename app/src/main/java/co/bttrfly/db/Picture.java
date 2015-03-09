package co.bttrfly.db;

import android.graphics.Color;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by jiho on 1/13/15.
 */

@DatabaseTable
public class Picture {
    @DatabaseField(id = true)
    private long id;

    @DatabaseField
    private String title;

    @DatabaseField
    private String uploaderName;

    @DatabaseField
    private String thumbnailUrl;

    @DatabaseField
    private String pictureUrl;

    @DatabaseField
    private double latitude;

    @DatabaseField
    private double longitude;

    @DatabaseField
    private float imageRatio;

    @DatabaseField
    private long originalWidth;

    @DatabaseField
    private long originalHeight;

    @DatabaseField
    private String capturedTime;

    @DatabaseField
    private long likeCount;

    @DatabaseField
    private long sendCount;

    @DatabaseField
    private boolean isLiked;

    @DatabaseField
    private String primaryColor;

    @DatabaseField
    private long sendPictureId;

    @DatabaseField
    private String countryName;

    @DatabaseField
    private boolean isMine;


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

    public void setOriginalWidth(long originalWidth) {
        this.originalWidth = originalWidth;
    }
    public long getOriginalWidth() {
        return originalWidth;
    }

    public void setOriginalHeight(long originalHeight) {
        this.originalHeight = originalHeight;
    }
    public long getOriginalHeight() {
        return originalHeight;
    }

    public void setCapturedTime(String capturedTime) {
        this.capturedTime = capturedTime;
    }
    public String getCapturedTime() {
        return capturedTime;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }
    public long getLikeCount() {
        return likeCount;
    }

    public void setSendCount(long sendCount) {
        this.sendCount = sendCount;
    }
    public long getSendCount() {
        return sendCount;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }
    public boolean getIsLiked() {
        return isLiked;
    }

    public void setSendPictureId(long sendPictureId) {
        this.sendPictureId = sendPictureId;
    }
    public long getSendPictureId() {
        return sendPictureId;
    }

    public void setPrimaryColor(String color) {
        primaryColor = color;
    }
    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    public String getCountryName() {
        return countryName;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }
    public boolean getIsMine() {
        return isMine;
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

    public String getLikeCountString() {
        if (likeCount > 1000000) {
            return (likeCount / 1000000) + "m ";
        } else if (likeCount > 1000) {
            return (likeCount / 1000) + "k ";
        } else {
            return likeCount + " ";
        }
    }

    public String getSendCountString() {
        return " " + sendCount;
    }

    public boolean isReceived() {
        return sendPictureId > 0;
    }
}
