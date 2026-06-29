package com.codereviews.loyalty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoyaltyCreditServiceTest {
    private FakeCompletedBookingRepository bookingRepository;
    private FakeLoyaltyAccountRepository accountRepository;
    private FakeNotificationClient notificationClient;
    private FakeAuditLog auditLog;
    private LoyaltyCreditService service;

    @BeforeEach
    void setUp() {
        bookingRepository = new FakeCompletedBookingRepository();
        accountRepository = new FakeLoyaltyAccountRepository();
        notificationClient = new FakeNotificationClient();
        auditLog = new FakeAuditLog();
        service = new LoyaltyCreditService(bookingRepository, accountRepository, notificationClient, auditLog);
    }

    @Test
    void creditsBasicPointsForCompletedBooking() {
        bookingRepository.save(booking("B-100", "C-1", new BigDecimal("120.00"), BookingChannel.WEB, false));
        accountRepository.save(new LoyaltyAccount("C-1", Tier.BASIC, 0));

        LoyaltyCreditResult result = service.creditForBooking("B-100");

        assertEquals(1200, result.getCreditedPoints());
        assertEquals(1200, result.getTotalPoints());
        assertEquals(Tier.BASIC, result.getTier());
        assertEquals(1, auditLog.count);
        assertEquals(1, notificationClient.count);
    }

    @Test
    void appliesMobileBonus() {
        bookingRepository.save(booking("B-200", "C-2", new BigDecimal("100.00"), BookingChannel.MOBILE_APP, false));
        accountRepository.save(new LoyaltyAccount("C-2", Tier.BASIC, 0));

        LoyaltyCreditResult result = service.creditForBooking("B-200");

        assertEquals(1200, result.getCreditedPoints());
    }

    @Test
    void appliesGoldTierMultiplier() {
        bookingRepository.save(booking("B-300", "C-3", new BigDecimal("80.00"), BookingChannel.WEB, false));
        accountRepository.save(new LoyaltyAccount("C-3", Tier.GOLD, 50_000));

        LoyaltyCreditResult result = service.creditForBooking("B-300");

        assertEquals(1000, result.getCreditedPoints());
        assertEquals(Tier.GOLD, result.getTier());
    }

    @Test
    void doesNotCreditCancelledBooking() {
        bookingRepository.save(booking("B-400", "C-4", new BigDecimal("200.00"), BookingChannel.WEB, true));

        LoyaltyCreditResult result = service.creditForBooking("B-400");

        assertEquals(0, result.getCreditedPoints());
        assertEquals("Cancelled bookings are not eligible", result.getMessage());
    }

    @Test
    void returnsAlreadyCreditedForRepeatedBooking() {
        bookingRepository.save(booking("B-500", "C-5", new BigDecimal("90.00"), BookingChannel.WEB, false));
        accountRepository.save(new LoyaltyAccount("C-5", Tier.BASIC, 0));

        service.creditForBooking("B-500");
        LoyaltyCreditResult secondResult = service.creditForBooking("B-500");

        assertEquals(0, secondResult.getCreditedPoints());
        assertEquals(900, secondResult.getTotalPoints());
        assertEquals(1, auditLog.count);
        assertEquals(1, notificationClient.count);
    }

    @Test
    void throwsForMissingBooking() {
        assertThrows(IllegalArgumentException.class, () -> service.creditForBooking("missing"));
    }

    private CompletedBooking booking(String bookingId, String customerId, BigDecimal amount, BookingChannel channel, boolean cancelled) {
        return new CompletedBooking(
                bookingId,
                customerId,
                "H-1",
                amount,
                "USD",
                LocalDate.now().minusDays(1),
                channel,
                cancelled
        );
    }

    private static class FakeCompletedBookingRepository implements CompletedBookingRepository {
        private final Map<String, CompletedBooking> bookings = new HashMap<>();

        @Override
        public Optional<CompletedBooking> findByBookingId(String bookingId) {
            return Optional.ofNullable(bookings.get(bookingId));
        }

        void save(CompletedBooking booking) {
            bookings.put(booking.getBookingId(), booking);
        }
    }

    private static class FakeLoyaltyAccountRepository implements LoyaltyAccountRepository {
        private final Map<String, LoyaltyAccount> accounts = new HashMap<>();

        @Override
        public Optional<LoyaltyAccount> findByCustomerId(String customerId) {
            return Optional.ofNullable(accounts.get(customerId));
        }

        @Override
        public void save(LoyaltyAccount account) {
            accounts.put(account.getCustomerId(), account);
        }
    }

    private static class FakeNotificationClient implements LoyaltyNotificationClient {
        private int count;

        @Override
        public void sendPointsCredited(String customerId, String bookingId, int creditedPoints) {
            count++;
        }
    }

    private static class FakeAuditLog implements LoyaltyAuditLog {
        private int count;

        @Override
        public void recordCredit(String customerId, String bookingId, int creditedPoints, String reason) {
            count++;
        }
    }
}
