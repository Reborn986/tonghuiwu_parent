package com.example.myapplication.bean;

import java.io.Serializable;

public class AiImgRoot implements Serializable {
    private AiImgData data;
    private long log_id;

    public AiImgData getData() {
        return data;
    }

    public void setData(AiImgData data) {
        this.data = data;
    }

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }
}
