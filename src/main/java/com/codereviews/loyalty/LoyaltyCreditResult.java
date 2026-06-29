package com.codereviews.loyalty;

public class LoyaltyCreditResult {
    private final String bookingId;
    private final String customerId;
    private final int creditedPoints;
    private final int totalPoints;
    private final Tier tier;
    private final String message;

    public LoyaltyCreditResult(String bookingId, String customerId, int creditedPoints, int totalPoints, Tier tier, String message) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.creditedPoints = creditedPoints;
        this.totalPoints = totalPoints;
        this.tier = tier;
        this.message = message;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getCreditedPoints() {
        return creditedPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public Tier getTier() {
        return tier;
    }

    public String getMessage() {
        return message;
    }
}
