package com.avelibeyli.photoapp.api.users.ui.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserRequest {

    @NotNull(message = "FirstName cannot be null")
    private String firstName;

    @NotNull(message = "LastName cannot be null")
    private String lastName;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, max = 30, message = "The password must be between 8 and 30 characters")
    private String password;

    @NotNull(message = "Email cannot be null")
    @Email
    private String email;

    public UserRequest() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
}
