package com.codereviews.loyalty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

public class LoyaltyCreditService {
    private static final BigDecimal POINTS_PER_DOLLAR = new BigDecimal("10");
    private static final BigDecimal MOBILE_BONUS_MULTIPLIER = new BigDecimal("1.20");
    private static final BigDecimal PARTNER_MULTIPLIER = new BigDecimal("0.50");

    private final CompletedBookingRepository bookingRepository;
    private final LoyaltyAccountRepository accountRepository;
    private final LoyaltyNotificationClient notificationClient;
    private final LoyaltyAuditLog auditLog;
    private final Set<String> creditedBookings = new HashSet<>();

    public LoyaltyCreditService(
            CompletedBookingRepository bookingRepository,
            LoyaltyAccountRepository accountRepository,
            LoyaltyNotificationClient notificationClient,
            LoyaltyAuditLog auditLog
    ) {
        this.bookingRepository = bookingRepository;
        this.accountRepository = accountRepository;
        this.notificationClient = notificationClient;
        this.auditLog = auditLog;
    }

    public LoyaltyCreditResult creditForBooking(String bookingId) {
        if (creditedBookings.contains(bookingId)) {
            CompletedBooking booking = bookingRepository.findByBookingId(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
            LoyaltyAccount account = accountRepository.findByCustomerId(booking.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Loyalty account not found: " + booking.getCustomerId()));
            return new LoyaltyCreditResult(bookingId, booking.getCustomerId(), 0, account.getPoints(), account.getTier(), "Already credited");
        }

        CompletedBooking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (booking.isCancelled()) {
            return new LoyaltyCreditResult(bookingId, booking.getCustomerId(), 0, 0, Tier.BASIC, "Cancelled bookings are not eligible");
        }

        LoyaltyAccount account = accountRepository.findByCustomerId(booking.getCustomerId())
                .orElse(new LoyaltyAccount(booking.getCustomerId(), Tier.BASIC, 0));

        int pointsToCredit = calculatePoints(booking, account.getTier());
        account.addPoints(pointsToCredit);
        accountRepository.save(account);

        creditedBookings.add(bookingId);
        auditLog.recordCredit(account.getCustomerId(), bookingId, pointsToCredit, "COMPLETED_BOOKING");
        notificationClient.sendPointsCredited(account.getCustomerId(), bookingId, pointsToCredit);

        return new LoyaltyCreditResult(
                bookingId,
                account.getCustomerId(),
                pointsToCredit,
                account.getPoints(),
                account.getTier(),
                "Points credited"
        );
    }

    private int calculatePoints(CompletedBooking booking, Tier tier) {
        BigDecimal basePoints = booking.getPaidAmount().multiply(POINTS_PER_DOLLAR);

        if (booking.getChannel() == BookingChannel.MOBILE_APP) {
            basePoints = basePoints.multiply(MOBILE_BONUS_MULTIPLIER);
        }

        if (booking.getChannel() == BookingChannel.PARTNER_API) {
            basePoints = basePoints.multiply(PARTNER_MULTIPLIER);
        }

        if (tier == Tier.GOLD) {
            basePoints = basePoints.multiply(new BigDecimal("1.25"));
        }

        if (tier == Tier.PLATINUM) {
            basePoints = basePoints.multiply(new BigDecimal("1.50"));
        }

        long daysSinceCheckout = ChronoUnit.DAYS.between(booking.getCheckoutDate(), LocalDate.now());
        if (daysSinceCheckout > 30) {
            basePoints = basePoints.divide(new BigDecimal("2"), RoundingMode.HALF_UP);
        }

        return basePoints.setScale(0, RoundingMode.HALF_UP).intValue();
    }
}
