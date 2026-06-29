package com.codereviews.hotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HotelQuoteServiceTest {
    private HotelQuoteService service;

    @BeforeEach
    void setUp() {
        service = new HotelQuoteService();
    }

    @Test
    void quotesOneNightWeekdayStay() {
        HotelQuote quote = service.quote(new HotelQuoteRequest(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 2),
                1,
                2,
                new BigDecimal("100.00"),
                "USD",
                null
        ));

        assertEquals(1, quote.getNights());
        assertEquals(1, quote.getRooms());
        assertEquals(2, quote.getGuests());
        assertEquals("USD", quote.getCurrency());
        assertEquals(new BigDecimal("100.00"), quote.getSubtotal());
        assertEquals(new BigDecimal("0.00"), quote.getDiscount());
        assertEquals(new BigDecimal("12.00"), quote.getTax());
        assertEquals(new BigDecimal("112.00"), quote.getTotal());
    }

    @Test
    void appliesWeekendPricingAndLoyaltyDiscount() {
        HotelQuote quote = service.quote(new HotelQuoteRequest(
                LocalDate.of(2026, 7, 3),
                LocalDate.of(2026, 7, 5),
                1,
                2,
                new BigDecimal("100.00"),
                "USD",
                "gold"
        ));

        assertEquals(2, quote.getNights());
        assertEquals(new BigDecimal("236.00"), quote.getSubtotal());
        assertEquals(new BigDecimal("16.52"), quote.getDiscount());
        assertEquals(new BigDecimal("26.34"), quote.getTax());
        assertEquals(new BigDecimal("245.82"), quote.getTotal());
    }

    @Test
    void addsExtraGuestFeesWhenRoomCapacityIsExceeded() {
        HotelQuote quote = service.quote(new HotelQuoteRequest(
                LocalDate.of(2026, 7, 6),
                LocalDate.of(2026, 7, 7),
                1,
                3,
                new BigDecimal("120.00"),
                "USD",
                "none"
        ));

        assertEquals(new BigDecimal("145.00"), quote.getSubtotal());
        assertEquals(new BigDecimal("162.40"), quote.getTotal());
    }

    @Test
    void usesCachedQuoteForRepeatedRequests() {
        HotelQuoteRequest request = new HotelQuoteRequest(
                LocalDate.of(2026, 7, 6),
                LocalDate.of(2026, 7, 7),
                1,
                1,
                new BigDecimal("90.00"),
                "USD",
                "none"
        );

        HotelQuote firstQuote = service.quote(request);
        HotelQuote secondQuote = service.quote(request);

        assertSame(firstQuote, secondQuote);
    }
}
