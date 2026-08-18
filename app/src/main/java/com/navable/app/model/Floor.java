package com.navable.app.model;

public class Floor {
    private Long id;
    private Integer floorNumber;
    private String name;
    private Long buildingId;

    public Long getId() { return id; }
    public Integer getFloorNumber() { return floorNumber; }
    public String getName() { return name; }
    public Long getBuildingId() { return buildingId; }

    @Override
    public String toString() { return name != null ? name : "Floor " + floorNumber; }
}
