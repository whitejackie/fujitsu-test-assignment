package com.liisabot.delivery.fees.weather;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Getter;
import lombok.Setter;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Represents the XML structure of weather observations, including a timestamp and a list of station observations.
 */
@Getter @Setter
@JsonIgnoreProperties (ignoreUnknown = true)
@JsonRootName("observations")
public class ObservationsXml {
    @JacksonXmlProperty(isAttribute = true)
    private long timestamp;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty (localName = "station")
    private List<StationXml> stations = new ArrayList<>();

    @Getter @Setter
    @JsonIgnoreProperties (ignoreUnknown = true)
    public static class StationXml {
        private String name;
        private String wmocode;
        private String airtemperature;
        private String windspeed;
        private String phenomenon;
    }
}
