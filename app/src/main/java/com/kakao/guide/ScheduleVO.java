package com.kakao.guide;

public class ScheduleVO {

    private String name;
    private String owner;
    private String visible;
    private String people;
    private String route;

    public ScheduleVO(){}

    public ScheduleVO(String name, String owner, String visible, String people, String route) {
        this.name = name;
        this.owner = owner;
        this.visible = visible;
        this.people = people;
        this.route = route;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getVisible() {
        return visible;
    }
    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getPeople() {
        return people;
    }
    public void setPeople(String people) {
        this.people = people;
    }

    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }

}