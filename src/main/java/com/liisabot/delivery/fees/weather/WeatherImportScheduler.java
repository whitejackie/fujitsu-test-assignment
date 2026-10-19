package com.liisabot.delivery.fees.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.liisabot.delivery.fees.service.WeatherImportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Schedules the weather import to run periodically and on application startup.
 */
@Slf4j
@Component 
@RequiredArgsConstructor 
public class WeatherImportScheduler {
    private final WeatherImportService importService;

    @Value("${weather.import.enabled:true}")
    private boolean runOnStartup;

    /**
     * Runs the weather import at the scheduled time.
     */
    @Scheduled(cron = "${weather.import.cron}")
    public void scheduledImport() {
        runImport();
    }

    /**
     * Runs one import right after startup if runOnStartup is true so the database is not empty.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void importOnStartup() {
        if (runOnStartup) {
            runImport();
        }
    }

    private void runImport() {
        try {
            int saved = importService.importLatestData();
            log.info("Weather import finished, {} new observations stored", saved);
        } catch (Exception e) {
            log.error("Weather import failed", e);
        }
    }
}
