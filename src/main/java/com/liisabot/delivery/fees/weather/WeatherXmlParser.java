package com.liisabot.delivery.fees.weather;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.liisabot.delivery.fees.domain.WeatherObservation;
import com.liisabot.delivery.fees.exception.WeatherImportException;

import tools.jackson.dataformat.xml.XmlMapper;

/**
 * Parses the weather XML data into a list of weather observations.
 */
@Component 
public class WeatherXmlParser {
    private final XmlMapper xmlMapper = XmlMapper.builder().build();

    /**
     * Parses the weather XML data into a list of weather observations.
     *
     * @param xml the XML data to parse
     * @return the list of parsed weather observations
     */
    public List<WeatherObservation> parse(byte[] xml) {
        try {
            ObservationsXml data = xmlMapper.readValue(xml, ObservationsXml.class);
            Instant observationTime = Instant.ofEpochSecond(data.getTimestamp());
            return data.getStations().stream().map(station -> toObservation(station, observationTime)).toList();
        } catch (Exception e) {
            throw new WeatherImportException("Failed to parse weather XML", e);
        }
    }

    private WeatherObservation toObservation(ObservationsXml.StationXml station, Instant observationTime) {
        WeatherObservation observation = new WeatherObservation();
        observation.setStationName(station.getName());
        observation.setWmoCode(station.getWmocode());
        observation.setAirTemperature(toDouble(station.getAirtemperature()));
        observation.setWindSpeed(toDouble(station.getWindspeed()));
        observation.setPhenomenon(station.getPhenomenon());
        observation.setObservationTime(observationTime);
        return observation;
    }

    private Double toDouble(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return Double.valueOf(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
