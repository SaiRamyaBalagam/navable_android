package com.navable.app.model;

import java.io.Serializable;
import java.util.List;

public class RouteResponse implements Serializable {
    private List<RouteStep> steps;
    private Double totalDistance;
    private Boolean found;

    public List<RouteStep> getSteps() { return steps; }
    public Double getTotalDistance() { return totalDistance; }
    public Boolean getFound() { return found; }
}
