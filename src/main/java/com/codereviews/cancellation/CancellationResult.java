package com.codereviews.cancellation;

import java.math.BigDecimal;

public class CancellationResult {
    private final String bookingId;
    private final BookingStatus status;
    private final BigDecimal refundAmount;
    private final String currency;
    private final String message;

    public CancellationResult(String bookingId, BookingStatus status, BigDecimal refundAmount, String currency, String message) {
        this.bookingId = bookingId;
        this.status = status;
        this.refundAmount = refundAmount;
        this.currency = currency;
        this.message = message;
    }

    public String getBookingId() {
        return bookingId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMessage() {
        return message;
    }
}
