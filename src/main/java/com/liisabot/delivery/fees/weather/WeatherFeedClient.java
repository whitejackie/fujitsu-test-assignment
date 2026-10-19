package com.liisabot.delivery.fees.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.liisabot.delivery.fees.exception.WeatherImportException;

/** Downloads the raw weather XML over HTTP. */
@Component
public class WeatherFeedClient {

    private final RestClient restClient = RestClient.create();
    private final String url;

    public WeatherFeedClient(@Value("${weather.import.url}") String url) {
        this.url = url;
    }

    /**
     * Downloads the feed.
     *
     * @return the raw response body
     * @throws WeatherImportException if the portal cannot be reached or returns an error
     */
    public byte[] fetch() {
        try {
            return restClient.get().uri(url).retrieve().body(byte[].class);
        } catch (RestClientException e) {
            throw new WeatherImportException("Could not download weather data from " + url, e);
        }
    }
}