package com.navable.app.model;

public class Location {
    private Long id;
    private String name;
    private String type;
    private Long floorId;
    private String floorName;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public Long getFloorId() { return floorId; }
    public String getFloorName() { return floorName; }

    @Override
    public String toString() { return name; }
}
