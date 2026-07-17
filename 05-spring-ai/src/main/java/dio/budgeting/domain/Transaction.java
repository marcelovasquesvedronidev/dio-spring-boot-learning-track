package dio.budgeting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Transaction {
    private TransactionId id;
    private String description;
    private long amount;
    private Category category;
    private String userId;
    private LocalDate date;

    public Transaction(String description, long amount, Category category, String userId) {
        this.id = new TransactionId();
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.userId = userId;
        this.date = LocalDate.now();
    }

    public Transaction(TransactionId transactionId, String description, long amount, Category category) {
        this.id = transactionId;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.userId = UUID.randomUUID().toString();
        this.date = LocalDate.now();
    }
}
