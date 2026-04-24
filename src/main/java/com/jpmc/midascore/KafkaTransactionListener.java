package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.TransactionValidator;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransactionListener {
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final TransactionValidator transactionValidator;

    public KafkaTransactionListener(
            UserRepository userRepository,
            TransactionRecordRepository transactionRecordRepository,
            TransactionValidator transactionValidator) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.transactionValidator = transactionValidator;
    }

    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core-group"
    )
    public void listen(Transaction transaction) {
        // Step 1: Look up sender and recipient from DB
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // Step 2: Validate
        if (!transactionValidator.isValid(transaction, sender, recipient)) {
            System.out.println("Invalid transaction discarded: " + transaction);
            return; // discard — no DB changes
        }

        // Step 3: Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());

        // Step 4: Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Step 5: Record the transaction
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount());
        transactionRecordRepository.save(record);

        System.out.println("Transaction recorded: " + transaction);

    }
}