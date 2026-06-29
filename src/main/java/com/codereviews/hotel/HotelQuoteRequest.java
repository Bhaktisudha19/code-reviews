package com.codereviews.hotel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class HotelQuoteRequest {
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int rooms;
    private final int guests;
    private final BigDecimal nightlyRate;
    private final String currency;
    private final String loyaltyTier;

    public HotelQuoteRequest(
            LocalDate checkIn,
            LocalDate checkOut,
            int rooms,
            int guests,
            BigDecimal nightlyRate,
            String currency,
            String loyaltyTier
    ) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.rooms = rooms;
        this.guests = guests;
        this.nightlyRate = nightlyRate;
        this.currency = currency == null || currency.isBlank() ? "USD" : currency;
        this.loyaltyTier = loyaltyTier == null || loyaltyTier.isBlank() ? "none" : loyaltyTier;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public int getRooms() {
        return rooms;
    }

    public int getGuests() {
        return guests;
    }

    public BigDecimal getNightlyRate() {
        return nightlyRate;
    }

    public String getCurrency() {
        return currency;
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HotelQuoteRequest)) {
            return false;
        }
        HotelQuoteRequest that = (HotelQuoteRequest) o;
        return rooms == that.rooms
                && guests == that.guests
                && Objects.equals(checkIn, that.checkIn)
                && Objects.equals(checkOut, that.checkOut)
                && Objects.equals(nightlyRate, that.nightlyRate)
                && Objects.equals(currency, that.currency)
                && Objects.equals(loyaltyTier, that.loyaltyTier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkIn, checkOut, rooms, guests, nightlyRate, currency, loyaltyTier);
    }
}
