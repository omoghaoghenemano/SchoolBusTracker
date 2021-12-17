package com.example.gpstracking;

public class Myuser {
    public String Email;
    public String Name;
    public String Username;
    public String Password;

    public Myuser() {

    }

    public Myuser(String email, String name, String username, String password) {
        Email = email;
        Name = name;
        Username = username;
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
