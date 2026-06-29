package com.codereviews.loyalty;

public interface LoyaltyNotificationClient {
    void sendPointsCredited(String customerId, String bookingId, int creditedPoints);
}
