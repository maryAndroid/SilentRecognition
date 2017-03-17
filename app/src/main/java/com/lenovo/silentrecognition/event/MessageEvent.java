package com.lenovo.silentrecognition.event;

/**
 * Created by mary on 2017/3/1.
 */

public class MessageEvent {
    private String msg;
    public MessageEvent(String msg){
        this.msg = msg;
    }
    public String getMsg(){
        return msg;
    }
}
