package com.neuroisp.dto;



public record PppoeActiveClient(
        String username,
        String ipAddress,
        String uptime,
        String service
) {}
