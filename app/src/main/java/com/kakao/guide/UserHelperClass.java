package com.kakao.guide;

public class UserHelperClass {

    String code, name, gender, pass, mail, phone;


    public UserHelperClass() {
    }

    public UserHelperClass(String code, String name, String gender, String pass, String mail, String phone) {
        this.code = code;
        this.name = name;
        this.gender = gender;
        this.pass = pass;
        this.mail = mail;
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
