package com.example.momentu.ecommercemobileapp;

public class upload {
    private String mName;
    private String mImageUrl;

    public  upload(){

    }

    public upload(String name,String url){
        if (name.trim().equals(""))
        {
            name="Unkown";
        }
        this.mName=name;
        this.mImageUrl=url;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
