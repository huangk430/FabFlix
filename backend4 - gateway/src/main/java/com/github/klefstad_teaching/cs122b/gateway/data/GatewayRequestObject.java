package com.github.klefstad_teaching.cs122b.gateway.data;

import java.sql.Timestamp;

public class GatewayRequestObject {
    private Integer id;
    private String ipAddress;
    private String path;
    private Timestamp callTime;

    public Integer getId() {
        return id;
    }

    public GatewayRequestObject setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public GatewayRequestObject setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public String getPath() {
        return path;
    }

    public GatewayRequestObject setPath(String path) {
        this.path = path;
        return this;
    }

    public Timestamp getCallTime() {
        return callTime;
    }

    public GatewayRequestObject setCallTime(Timestamp callTime) {
        this.callTime = callTime;
        return this;
    }
}
