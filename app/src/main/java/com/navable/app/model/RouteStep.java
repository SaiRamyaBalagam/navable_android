package com.navable.app.model;

import java.io.Serializable;

public class RouteStep implements Serializable {
    private Long locationId;
    private String locationName;
    private String locationType;
    private Double distanceFromPrevious;

    public Long getLocationId() { return locationId; }
    public String getLocationName() { return locationName; }
    public String getLocationType() { return locationType; }
    public Double getDistanceFromPrevious() { return distanceFromPrevious; }
}
