package com.example.myapplication.bean;

import java.io.Serializable;
import java.util.List;

public class AiImgData implements Serializable {
    private String task_status;
    private int task_progress;
    private List<AiImgSubTaskResultList> sub_task_result_list;
    private int task_id;

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    public int getTask_progress() {
        return task_progress;
    }

    public void setTask_progress(int task_progress) {
        this.task_progress = task_progress;
    }

    public List<AiImgSubTaskResultList> getSub_task_result_list() {
        return sub_task_result_list;
    }

    public void setSub_task_result_list(List<AiImgSubTaskResultList> sub_task_result_list) {
        this.sub_task_result_list = sub_task_result_list;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }
}
