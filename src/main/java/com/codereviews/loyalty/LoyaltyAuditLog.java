package com.codereviews.loyalty;

public interface LoyaltyAuditLog {
    void recordCredit(String customerId, String bookingId, int creditedPoints, String reason);
}
