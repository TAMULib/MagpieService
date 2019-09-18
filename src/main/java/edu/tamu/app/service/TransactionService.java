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
        logger.info(String.format("Adding transaction with id %s", tid));
        transactions.put(tid, now().plus(duration));
    }

    public void remove(String tid) {
        logger.info(String.format("Removing transaction with id %s", tid));
        transactions.remove(tid);
    }

    public boolean isExpired(String tid) {
        if (transactions.containsKey(tid)) {
            return transactions.get(tid).isAfter(now());
        }
        logger.info(String.format("Transaction with id %s not found!", tid));
        return true;
    }

    public boolean isAboutToExpire(String tid) {
        if (transactions.containsKey(tid)) {
            return transactions.get(tid).isAfter(now().minusSeconds(15));
        }
        logger.info(String.format("Transaction with id %s not found!", tid));
        return false;
    }

    private LocalDateTime now() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.now());
    }

    @Scheduled(fixedRate = 180000)
    private void expireTransactions() {
        transactions.forEach((tid, t) -> {
            if (t.isAfter(now())) {
                remove(tid);
            }
        });
    }

}
