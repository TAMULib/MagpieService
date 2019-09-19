package edu.tamu.app.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private static final Logger logger = Logger.getLogger(TransactionService.class);

    private static final Map<String, LocalDateTime> transactions = new ConcurrentHashMap<String, LocalDateTime>();

    public void add(String tid, Duration duration) {
        logger.debug(String.format("Adding transaction with id %s", tid));
        transactions.put(tid, now().plus(duration));
    }

    public void remove(String tid) {
        logger.debug(String.format("Removing transaction with id %s", tid));
        transactions.remove(tid);
    }

    public boolean isAboutToExpire(String tid) {
        if (transactions.containsKey(tid)) {
            return transactions.get(tid).isBefore(now().plusSeconds(15));
        }
        logger.info(String.format("Transaction with id %s not found!", tid));
        return false;
    }

    public int count() {
        return transactions.size();
    }

    public void clear() {
        transactions.clear();
    }

    private LocalDateTime now() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.now());
    }

    @Scheduled(fixedRate = 180000)
    void expire() {
        transactions.forEach((tid, t) -> {
            if (t.isBefore(now())) {
                remove(tid);
            }
        });
    }

}
