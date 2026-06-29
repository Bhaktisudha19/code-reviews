package com.codereviews.cancellation;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Booking {
    private final String bookingId;
    private final String customerEmail;
    private final String hotelId;
    private final LocalDate checkInDate;
    private final BigDecimal totalAmount;
    private final String currency;
    private BookingStatus status;

    public Booking(
            String bookingId,
            String customerEmail,
            String hotelId,
            LocalDate checkInDate,
            BigDecimal totalAmount,
            String currency,
            BookingStatus status
    ) {
        this.bookingId = bookingId;
        this.customerEmail = customerEmail;
        this.hotelId = hotelId;
        this.checkInDate = checkInDate;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getHotelId() {
        return hotelId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }
}
