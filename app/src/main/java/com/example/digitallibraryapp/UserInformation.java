package com.example.digitallibraryapp;

public class UserInformation {

    private String name;
    private String department;
    private int rollNO;
    private String class_;

    public UserInformation(String name, String department, int rollNO, String class_) {
        this.name = name;
        this.department = department;
        this.rollNO = rollNO;
        this.class_ = class_;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getRollNO() {
        return rollNO;
    }

    public void setRollNO(int rollNO) {
        this.rollNO = rollNO;
    }

    public String getClass_() {
        return class_;
    }

    public void setClass_(String class_) {
        this.class_ = class_;
    }
}

