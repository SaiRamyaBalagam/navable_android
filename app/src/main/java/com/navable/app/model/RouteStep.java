package com.navable.app.model;

import java.io.Serializable;

public class RouteStep implements Serializable {
    private Long locationId;
    private String locationName;
    private String locationType;
    private Double distanceFromPrevious;
    private Double xCoordinate;
    private Double yCoordinate;

    public Long getLocationId() { return locationId; }
    public String getLocationName() { return locationName; }
    public String getLocationType() { return locationType; }
    public Double getDistanceFromPrevious() { return distanceFromPrevious; }
    public Double getXCoordinate() { return xCoordinate; }
    public Double getYCoordinate() { return yCoordinate; }
}
