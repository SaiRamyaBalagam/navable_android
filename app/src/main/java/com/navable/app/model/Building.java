package com.navable.app.model;

public class Building {
    private Long id;
    private String name;
    private String address;
    private Long organizationId;
    private String organizationName;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public Long getOrganizationId() { return organizationId; }
    public String getOrganizationName() { return organizationName; }

    @Override
    public String toString() { return name; }
}
