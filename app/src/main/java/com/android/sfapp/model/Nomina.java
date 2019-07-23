package com.android.sfapp.model;

public class Nomina {

    private int numberDoc;
    private String typeDoc;
    private String lastName;
    private String firstName;
    private String phone;
    private String email;
    private String salary;

    public Nomina(int numberDoc, String typeDoc, String lastName, String firstName, String phone,  String email,  String salary) {
        this.numberDoc = numberDoc;
        this.typeDoc = typeDoc;
        this.lastName = lastName;
        this.firstName = firstName;
        this.phone = phone;
        this.email = email;
        this.salary = salary;
    }

    public int getNumberDoc() {
        return numberDoc;
    }

    public String getTypeDoc() {
        return typeDoc;
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

    public String getEmail() {
        return email;
    }

    public String getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return "Nomina{" +
                "numberDoc=" + numberDoc +
                ", typeDoc='" + typeDoc + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", salary='" + salary + '\'' +
                '}';
    }
}

