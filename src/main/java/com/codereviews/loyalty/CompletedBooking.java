package com.codereviews.loyalty;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CompletedBooking {
    private final String bookingId;
    private final String customerId;
    private final String hotelId;
    private final BigDecimal paidAmount;
    private final String currency;
    private final LocalDate checkoutDate;
    private final BookingChannel channel;
    private final boolean cancelled;

    public CompletedBooking(
            String bookingId,
            String customerId,
            String hotelId,
            BigDecimal paidAmount,
            String currency,
            LocalDate checkoutDate,
            BookingChannel channel,
            boolean cancelled
    ) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.hotelId = hotelId;
        this.paidAmount = paidAmount;
        this.currency = currency;
        this.checkoutDate = checkoutDate;
        this.channel = channel;
        this.cancelled = cancelled;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public BookingChannel getChannel() {
        return channel;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
