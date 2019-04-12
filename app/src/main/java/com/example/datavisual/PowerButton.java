package com.example.datavisual;

public class PowerButton {

    private  String name;
    private int imageId;
    public PowerButton(String name, int imageId){
        this.name = name;
        this.imageId = imageId;
    }
    public String getName(){
        return name;
    }
    public int getImageId(){
        return imageId;
    }

}

