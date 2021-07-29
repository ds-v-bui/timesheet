package com.dsvn.starterkit.schedulingtasks;

import com.dsvn.starterkit.repositories.AccessTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired private AccessTokenRepository accessTokenRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void reportCurrentTime() {
        accessTokenRepository.deleteExpiresToken();
    }
}
