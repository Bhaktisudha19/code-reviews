package com.codereviews.hotel;

import java.math.BigDecimal;

public record HotelQuote(
        long nights,
        int rooms,
        int guests,
        String currency,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal total
) {
}
