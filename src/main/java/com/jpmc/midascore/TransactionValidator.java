package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.stereotype.Component;

@Component
public class TransactionValidator {

    public boolean isValid(Transaction transaction, UserRecord sender, UserRecord recipient) {
        // Rule 1: sender must exist
        if (sender == null) return false;

        // Rule 2: recipient must exist
        if (recipient == null) return false;

        // Rule 3: sender must have enough balance
        if (sender.getBalance() < transaction.getAmount()) return false;

        return true;
    }
}