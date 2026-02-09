package com.neuroisp.dto;



public record HotspotActiveClient(
        String username,
        String ipAddress,
        String macAddress,
        String uptime,
        String server
) {}
