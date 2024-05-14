package com.lbs.lbs.Entity;


import org.locationtech.jts.geom.Coordinate;

import java.util.Date;


public class TransportPath {
   private Coordinate coordinate;
   private Date time;
   private String name;
   private String routeName;
   private String condition;

    // Constructor
    public TransportPath(Coordinate coordinate, Date time, String name, String routeName, String condition) {
        this.coordinate = coordinate;
        this.time = time;
        this.name = name;
        this.routeName = routeName;
        this.condition = condition;
    }

    // Getters
    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Date getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getCondition() {
        return condition;
    }

    // Setters
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
    @Override
    public String toString() {
        return "TransportPath{" +
                "coordinate=" + coordinate +
                ", time=" + time +
                ", name='" + name + '\'' +
                ", routeName='" + routeName + '\'' +
                ", condition='" + condition + '\'' +
                '}';
    }
}
