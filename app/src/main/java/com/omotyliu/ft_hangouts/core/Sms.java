package com.omotyliu.ft_hangouts.core;

public class Sms {

    public static final int USER_ID = 0;

    private int id;

    private String message = "";

    private long creationTime;

    private int consumerId;

    private String type;

    private int authorId;

    private boolean read;

    private String authorNumber = "";

    private String addressNumber = "";

    public Sms() {

    }


    public int getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(int consumerId) {
        this.consumerId = consumerId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getAuthor()
    {
        return authorId;
    }

    public static int getUserId() {
        return USER_ID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }


    public String getAuthorNumber() {
        return authorNumber;
    }

    public void setAuthorNumber(String authorNumber) {
        this.authorNumber = authorNumber;
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
