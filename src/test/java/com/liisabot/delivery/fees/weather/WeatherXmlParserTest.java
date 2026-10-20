package com.liisabot.delivery.fees.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.liisabot.delivery.fees.domain.WeatherObservation;
import com.liisabot.delivery.fees.exception.WeatherImportException;

public class WeatherXmlParserTest {
    private final WeatherXmlParser parser = new WeatherXmlParser();

    @Test
    void parserStationsAndHandlesEmptyValues() {
        String xml =  """
                <?xml version="1.0" encoding="UTF-8"?>
                <observations timestamp="1700000000">
                    <station>
                        <name>Tartu-Tõravere</name>
                        <wmocode>26242</wmocode>
                        <phenomenon>Light snow shower</phenomenon>
                        <airtemperature>-2.1</airtemperature>
                        <windspeed>4.7</windspeed>
                    </station>
                    <station>
                        <name>Pärnu</name>
                        <wmocode>41803</wmocode>
                        <phenomenon/>
                        <airtemperature></airtemperature>
                        <windspeed>3</windspeed>
                    </station>
                </observations>
                """;

        List<WeatherObservation> result = parser.parse(xml.getBytes(StandardCharsets.UTF_8));

        assertEquals(2, result.size());
        assertEquals("Tartu-Tõravere", result.get(0).getStationName());
        assertEquals("26242", result.get(0).getWmoCode());
        assertEquals(-2.1, result.get(0).getAirTemperature());
        assertEquals(4.7, result.get(0).getWindSpeed());
        assertEquals("Light snow shower", result.get(0).getPhenomenon());
        
        assertNull(result.get(1).getAirTemperature());
        assertNull(result.get(1).getPhenomenon());
    }

    @Test
    void invalidXmlThrowsImportException() {
        byte[] notXml = "this is not xml".getBytes(StandardCharsets.UTF_8);

        assertThrows(WeatherImportException.class, () -> parser.parse(notXml));
    }
}
