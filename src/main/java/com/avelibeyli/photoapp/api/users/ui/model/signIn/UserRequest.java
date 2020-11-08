package com.avelibeyli.photoapp.api.users.ui.model.signIn;

public class UserRequest {

    private String password;

    private String email;

    public UserRequest() {

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
