package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Configurations;

@JsonObject
public class ConfigurationsData
{
    @JsonField(name = "activeReward")
    public boolean activeReward;

    public ConfigurationsData()
    {

    }

    public Configurations getConfigurations()
    {
        Configurations configuration = new Configurations();
        configuration.activeReward = activeReward;

        return configuration;
    }
}
