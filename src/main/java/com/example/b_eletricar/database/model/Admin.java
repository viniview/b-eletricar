package com.example.b_eletricar.database.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Admin {
    @Id     
    private String userName;
    private String password;    
    private String email;
    private String cellNumber;

    public Admin(String userName, String password, String email, String cellNumber) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.cellNumber = cellNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }
}
