package com.codereviews.cancellation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingCancellationServiceTest {
    private FakeBookingRepository bookingRepository;
    private FakeRefundGateway refundGateway;
    private FakeInventoryClient inventoryClient;
    private FakeEmailClient emailClient;
    private BookingCancellationService service;

    @BeforeEach
    void setUp() {
        bookingRepository = new FakeBookingRepository();
        refundGateway = new FakeRefundGateway();
        inventoryClient = new FakeInventoryClient();
        emailClient = new FakeEmailClient();
        service = new BookingCancellationService(bookingRepository, refundGateway, inventoryClient, emailClient);
    }

    @Test
    void givesFullRefundWhenCancelledAtLeastSevenDaysBeforeCheckIn() {
        Booking booking = booking(
                "B-100",
                LocalDate.now().plusDays(10),
                new BigDecimal("300.00"),
                BookingStatus.CONFIRMED
        );
        bookingRepository.save(booking);

        CancellationResult result = service.cancel(new CancellationRequest(
                "B-100",
                CancellationReason.CUSTOMER_REQUEST,
                "customer"
        ));

        assertEquals(BookingStatus.CANCELLED, result.getStatus());
        assertEquals(new BigDecimal("300.00"), result.getRefundAmount());
        assertEquals(1, refundGateway.refundCount);
        assertEquals(1, inventoryClient.releaseCount);
        assertEquals(1, emailClient.emailCount);
    }

    @Test
    void givesPartialRefundWhenCancelledTwoDaysBeforeCheckIn() {
        Booking booking = booking(
                "B-200",
                LocalDate.now().plusDays(3),
                new BigDecimal("500.00"),
                BookingStatus.CONFIRMED
        );
        bookingRepository.save(booking);

        CancellationResult result = service.cancel(new CancellationRequest(
                "B-200",
                CancellationReason.CUSTOMER_REQUEST,
                "agent"
        ));

        assertEquals(new BigDecimal("250.00"), result.getRefundAmount());
        assertEquals(BookingStatus.CANCELLED, bookingRepository.findById("B-200").get().getStatus());
    }

    @Test
    void givesNoRefundWhenCancelledCloseToCheckIn() {
        Booking booking = booking(
                "B-300",
                LocalDate.now().plusDays(1),
                new BigDecimal("200.00"),
                BookingStatus.CONFIRMED
        );
        bookingRepository.save(booking);

        CancellationResult result = service.cancel(new CancellationRequest(
                "B-300",
                CancellationReason.CUSTOMER_REQUEST,
                "customer"
        ));

        assertEquals(new BigDecimal("0.00"), result.getRefundAmount());
    }

    @Test
    void givesFullRefundForSupplierUnavailableCancellation() {
        Booking booking = booking(
                "B-400",
                LocalDate.now().plusDays(1),
                new BigDecimal("700.00"),
                BookingStatus.CONFIRMED
        );
        bookingRepository.save(booking);

        CancellationResult result = service.cancel(new CancellationRequest(
                "B-400",
                CancellationReason.SUPPLIER_UNAVAILABLE,
                "system"
        ));

        assertEquals(new BigDecimal("700.00"), result.getRefundAmount());
    }

    @Test
    void returnsStoredResultForRepeatedCancellation() {
        Booking booking = booking(
                "B-500",
                LocalDate.now().plusDays(8),
                new BigDecimal("150.00"),
                BookingStatus.CONFIRMED
        );
        bookingRepository.save(booking);

        service.cancel(new CancellationRequest("B-500", CancellationReason.CUSTOMER_REQUEST, "customer"));
        CancellationResult secondResult = service.cancel(new CancellationRequest(
                "B-500",
                CancellationReason.CUSTOMER_REQUEST,
                "customer"
        ));

        assertEquals(new BigDecimal("150.00"), secondResult.getRefundAmount());
        assertEquals(1, refundGateway.refundCount);
        assertEquals(1, inventoryClient.releaseCount);
        assertEquals(1, emailClient.emailCount);
    }

    @Test
    void throwsWhenBookingDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> service.cancel(new CancellationRequest(
                "missing",
                CancellationReason.CUSTOMER_REQUEST,
                "customer"
        )));
    }

    private Booking booking(String bookingId, LocalDate checkInDate, BigDecimal totalAmount, BookingStatus status) {
        return new Booking(
                bookingId,
                "guest@example.com",
                "H-10",
                checkInDate,
                totalAmount,
                "USD",
                status
        );
    }

    private static class FakeBookingRepository implements BookingRepository {
        private final Map<String, Booking> bookings = new HashMap<>();

        @Override
        public Optional<Booking> findById(String bookingId) {
            return Optional.ofNullable(bookings.get(bookingId));
        }

        @Override
        public void save(Booking booking) {
            bookings.put(booking.getBookingId(), booking);
        }
    }

    private static class FakeRefundGateway implements RefundGateway {
        private int refundCount;

        @Override
        public String refund(String bookingId, BigDecimal amount, String currency) {
            refundCount++;
            return "refund-" + bookingId;
        }
    }

    private static class FakeInventoryClient implements InventoryClient {
        private int releaseCount;

        @Override
        public void releaseRoom(String hotelId, String bookingId) {
            releaseCount++;
        }
    }

    private static class FakeEmailClient implements EmailClient {
        private int emailCount;

        @Override
        public void sendCancellationEmail(String email, String bookingId, BigDecimal refundAmount, String currency) {
            emailCount++;
        }
    }
}
