package com.codereviews.loyalty;

import java.util.Optional;

public interface LoyaltyAccountRepository {
    Optional<LoyaltyAccount> findByCustomerId(String customerId);

    void save(LoyaltyAccount account);
}
