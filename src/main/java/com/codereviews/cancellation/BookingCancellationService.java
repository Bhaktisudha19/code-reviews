package com.codereviews.cancellation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookingCancellationService {
    private static final BigDecimal FULL_REFUND = new BigDecimal("1.00");
    private static final BigDecimal PARTIAL_REFUND = new BigDecimal("0.50");
    private static final BigDecimal NO_REFUND = BigDecimal.ZERO;

    private final BookingRepository bookingRepository;
    private final RefundGateway refundGateway;
    private final InventoryClient inventoryClient;
    private final EmailClient emailClient;
    private final Map<String, CancellationResult> processedCancellations = new ConcurrentHashMap<>();

    public BookingCancellationService(
            BookingRepository bookingRepository,
            RefundGateway refundGateway,
            InventoryClient inventoryClient,
            EmailClient emailClient
    ) {
        this.bookingRepository = bookingRepository;
        this.refundGateway = refundGateway;
        this.inventoryClient = inventoryClient;
        this.emailClient = emailClient;
    }

    public CancellationResult cancel(CancellationRequest request) {
        if (processedCancellations.containsKey(request.getBookingId())) {
            return processedCancellations.get(request.getBookingId());
        }

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + request.getBookingId()));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            CancellationResult result = new CancellationResult(
                    booking.getBookingId(),
                    booking.getStatus(),
                    BigDecimal.ZERO,
                    booking.getCurrency(),
                    "Booking was already cancelled"
            );
            processedCancellations.put(request.getBookingId(), result);
            return result;
        }

        BigDecimal refundAmount = calculateRefund(booking, request.getReason());
        refundGateway.refund(booking.getBookingId(), refundAmount, booking.getCurrency());
        inventoryClient.releaseRoom(booking.getHotelId(), booking.getBookingId());

        booking.cancel();
        bookingRepository.save(booking);

        emailClient.sendCancellationEmail(
                booking.getCustomerEmail(),
                booking.getBookingId(),
                refundAmount,
                booking.getCurrency()
        );

        CancellationResult result = new CancellationResult(
                booking.getBookingId(),
                booking.getStatus(),
                refundAmount,
                booking.getCurrency(),
                "Cancellation completed"
        );
        processedCancellations.put(request.getBookingId(), result);
        return result;
    }

    private BigDecimal calculateRefund(Booking booking, CancellationReason reason) {
        if (reason == CancellationReason.PAYMENT_FAILED || reason == CancellationReason.SUPPLIER_UNAVAILABLE) {
            return money(booking.getTotalAmount().multiply(FULL_REFUND));
        }

        long daysBeforeCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), booking.getCheckInDate());

        if (daysBeforeCheckIn >= 7) {
            return money(booking.getTotalAmount().multiply(FULL_REFUND));
        }

        if (daysBeforeCheckIn >= 2) {
            return money(booking.getTotalAmount().multiply(PARTIAL_REFUND));
        }

        return money(booking.getTotalAmount().multiply(NO_REFUND));
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
