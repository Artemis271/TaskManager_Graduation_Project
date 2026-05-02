package com.yuranium.gateway.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "routes")
public class RouteProperties
{
    private List<String> openEndpoints = new ArrayList<>();

    public List<String> getOpenEndpoints()
    {
        return openEndpoints;
    }

    public void setOpenEndpoints(List<String> openEndpoints)
    {
        this.openEndpoints = openEndpoints;
    }
}