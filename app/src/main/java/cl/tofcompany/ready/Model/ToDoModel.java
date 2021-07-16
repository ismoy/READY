package cl.tofcompany.ready.Model;


public class ToDoModel {
    private String task, date, time;
    private int id, status;

    public ToDoModel() {
    }

    public ToDoModel(String task , String date , String time , int id , int status) {
        this.task = task;
        this.date = date;
        this.time = time;
        this.id = id;
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
