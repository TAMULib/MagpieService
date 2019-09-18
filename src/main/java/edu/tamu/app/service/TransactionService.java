package edu.tamu.app.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final static Map<String, LocalDateTime> transactions = new ConcurrentHashMap<String, LocalDateTime>();

    public void add(String tid, Duration duration) {
        transactions.put(tid, now().plus(duration));
    }

    public void remove(String tid) {
        transactions.remove(tid);
    }

    public boolean isExpired(String tid) {
        if (transactions.containsKey(tid)) {
            return transactions.get(tid).isAfter(now());
        }
        return true;
    }

    public boolean isAboutToExpire(String tid) {
        if (transactions.containsKey(tid)) {
            return transactions.get(tid).isAfter(now().minusSeconds(15));
        }
        throw new RuntimeException(String.format("Transaction with id %s not found!", tid));
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
