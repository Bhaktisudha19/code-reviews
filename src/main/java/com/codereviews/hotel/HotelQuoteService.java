package com.codereviews.hotel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Calculates pre-booking price estimates for checkout and shopping flows.
 *
 * The service owns quote calculation only. It does not reserve inventory, capture payment,
 * or guarantee supplier settlement amounts. Callers should receive a fast, deterministic,
 * auditable breakdown or fail before showing an unreliable price to the customer.
 */
public class HotelQuoteService {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.12");
    private static final BigDecimal WEEKEND_MULTIPLIER = new BigDecimal("1.18");
    private static final BigDecimal EXTRA_GUEST_FEE = new BigDecimal("25.00");
    private static final Map<String, BigDecimal> LOYALTY_DISCOUNTS = Map.of(
            "silver", new BigDecimal("0.03"),
            "gold", new BigDecimal("0.07"),
            "platinum", new BigDecimal("0.12")
    );

    private final Map<HotelQuoteRequest, HotelQuote> quoteCache = new ConcurrentHashMap<>();

    public HotelQuote quote(HotelQuoteRequest request) {
        if (quoteCache.containsKey(request)) {
            return quoteCache.get(request);
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        int rooms = request.getRooms() == 0 ? 1 : request.getRooms();
        int guests = request.getGuests() == 0 ? rooms : request.getGuests();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (int offset = 0; offset < nights; offset++) {
            LocalDate stayDate = request.getCheckIn().plusDays(offset);
            BigDecimal dayRate = request.getNightlyRate();

            if (isWeekend(stayDate)) {
                dayRate = dayRate.multiply(WEEKEND_MULTIPLIER);
            }

            subtotal = subtotal.add(dayRate.multiply(BigDecimal.valueOf(rooms)));
        }

        if (guests > rooms * 2) {
            subtotal = subtotal.add(EXTRA_GUEST_FEE.multiply(BigDecimal.valueOf(guests - rooms * 2)));
        }

        BigDecimal discountRate = LOYALTY_DISCOUNTS.getOrDefault(request.getLoyaltyTier(), BigDecimal.ZERO);
        BigDecimal discount = subtotal.multiply(discountRate);
        BigDecimal taxableAmount = subtotal.subtract(discount);
        BigDecimal tax = taxableAmount.multiply(TAX_RATE);
        BigDecimal total = taxableAmount.add(tax);

        HotelQuote quote = new HotelQuote(
                nights,
                rooms,
                guests,
                request.getCurrency(),
                money(subtotal),
                money(discount),
                money(tax),
                money(total)
        );

        quoteCache.put(request, quote);
        return quote;
    }

    public void clearCache() {
        quoteCache.clear();
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY;
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
