package com.example.myapplication.bean;

import java.io.Serializable;
import java.util.List;

public class AiImgSubTaskResultList implements Serializable {
    private int sub_task_error_code;
    private String sub_task_status;
    private int sub_task_progress;
    private List<AiImgFinalList> final_image_list;

    public List<AiImgFinalList> getFinal_image_list() {
        return final_image_list;
    }

    public void setFinal_image_list(List<AiImgFinalList> final_image_list) {
        this.final_image_list = final_image_list;
    }

    public int getSub_task_error_code() {
        return sub_task_error_code;
    }

    public void setSub_task_error_code(int sub_task_error_code) {
        this.sub_task_error_code = sub_task_error_code;
    }

    public String getSub_task_status() {
        return sub_task_status;
    }

    public void setSub_task_status(String sub_task_status) {
        this.sub_task_status = sub_task_status;
    }

    public int getSub_task_progress() {
        return sub_task_progress;
    }

    public void setSub_task_progress(int sub_task_progress) {
        this.sub_task_progress = sub_task_progress;
    }
}
