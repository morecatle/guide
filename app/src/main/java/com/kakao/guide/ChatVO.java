package com.kakao.guide;

public class ChatVO {

    //private int imageID ;
    private String id;
    private String other;
    private String content;
    private String time;

    public ChatVO(){}

    public ChatVO(String id, String other, String content, String time) {
        //this.imageID = imageID;
        this.id = id;
        this.other = other;
        this.content = content;
        this.time = time;
    }

//    public int getImageID() {
//        return imageID;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOther() {
        return other;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }
}