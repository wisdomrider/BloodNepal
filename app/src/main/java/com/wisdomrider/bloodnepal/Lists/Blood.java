package com.wisdomrider.bloodnepal.Lists;

/*
CREated by avi(Wisdomrider)
on 9/5/2018
*/
public class Blood {
    String user,hospital,fb,receiver,age,description,urgency,phone,bloodGroup,location,url;
    boolean isAdmin;
    long time,limit;
    public Blood(){

    }
    public Blood(String age,String bloodGroup, String description,String fb, String hospital,long limit, String location, String phone, String receiver,long time, String urgency, String user) {
        this.user = user;
        this.limit=limit;
        this.hospital=hospital;
        this.receiver = receiver;
        this.time=time;
        this.fb=fb;
        this.age = age;
        this.description = description;
        this.urgency = urgency;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.location = location;
    }

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public long getLimit() {
        return limit;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
    public boolean isAdmin(){
        return isAdmin;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
