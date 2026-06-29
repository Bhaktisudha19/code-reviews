package com.codereviews.hotel;

import java.math.BigDecimal;

public class HotelQuote {
    private final long nights;
    private final int rooms;
    private final int guests;
    private final String currency;
    private final BigDecimal subtotal;
    private final BigDecimal discount;
    private final BigDecimal tax;
    private final BigDecimal total;

    public HotelQuote(
            long nights,
            int rooms,
            int guests,
            String currency,
            BigDecimal subtotal,
            BigDecimal discount,
            BigDecimal tax,
            BigDecimal total
    ) {
        this.nights = nights;
        this.rooms = rooms;
        this.guests = guests;
        this.currency = currency;
        this.subtotal = subtotal;
        this.discount = discount;
        this.tax = tax;
        this.total = total;
    }

    public long getNights() {
        return nights;
    }

    public int getRooms() {
        return rooms;
    }

    public int getGuests() {
        return guests;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
