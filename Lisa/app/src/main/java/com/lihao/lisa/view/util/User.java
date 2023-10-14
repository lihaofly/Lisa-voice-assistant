package com.lihao.lisa.view.util;

public class User {

    //To be delete
    public final static int USER_TYPE_SENDER = 1;
    public final static int USER_TYPE_RECIEVER = 2;
    public final static int USER_TYPE_WEATHER = 3;

    protected String nickname;
    protected String profileUrl;
    protected String userID;

    public User(String nickname, String profileUrl, String userID){
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.userID = userID;
    }

    public User(String nickname){
        this.nickname = nickname;
        this.profileUrl = "";
        this.userID = "";
    }

    public String getNickname(){
        return nickname;
    }

    public String getProfileUrl(){
        return nickname;
    }

    public String getUserId(){
        return userID;
    }

}
