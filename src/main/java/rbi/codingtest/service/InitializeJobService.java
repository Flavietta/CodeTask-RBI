package rbi.codingtest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.*;

@Component
@Order(0)
public class InitializeJobService implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(InitializeJobService.class);

    @Autowired
    private LoyaltyService loyaltyService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Get next Weekend
        LocalDateTime nextWeekEnd = LocalDateTime.now().with(LocalTime.of(0,0)).
                with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        long duration = Duration.between(LocalTime.now(),nextWeekEnd).toMinutes();
        //Schedule a task to be executed at the end of each week
        ScheduledFuture<?> result = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                loyaltyService.transferPendingToAvailablePoints();
                logger.debug("Points Are being transferred from pending to available");
            }
        }, duration ,7*24*60 , TimeUnit.MINUTES);

        logger.info("Transferring Points job has been initialized");
    }
}

