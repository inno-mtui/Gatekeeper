package com.example.gatekeeper;

public class Drivers {
    private String plateNumber;
    private String driverName;
    private String gender;
    private String ageRange;
    private String companions;
    private String day;
    private String lastName;
    private String date;



    public Drivers() {

    }

    public Drivers(String plateNumber, String driverName, String gender, String ageRange, String companions, String day, String lastName, String date) {
        this.plateNumber = plateNumber;
        this.driverName = driverName;
        this.gender = gender;
        this.ageRange = ageRange;
        this.companions = companions;
        this.day = day;
        this.lastName = lastName;
        this.date = date;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public String getCompanions() {
        return companions;
    }

    public void setCompanions(String companions) {
        this.companions = companions;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
