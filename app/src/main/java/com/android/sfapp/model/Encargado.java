package com.android.sfapp.model;

public class Encargado {

    private int numberDoc;
    private String lastName;
    private String firstName;
    private String phone;
    private String status;

    public Encargado(int numberDoc, String lastName, String firstName, String phone, String status) {
        this.numberDoc = numberDoc;
        this.lastName = lastName;
        this.firstName = firstName;
        this.phone = phone;
        this.status = status;
    }

    public int getNumberDoc() {
        return numberDoc;
    }
    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public void changeStatus(){
        if (status.equals("H")){
            status = "D";
        }else {
            status = "H";
        }
    }

    @Override
    public String toString() {
        return "Encargado{" +
                "numberDoc=" + numberDoc +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
