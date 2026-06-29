package com.codereviews.cancellation;

public class CancellationRequest {
    private final String bookingId;
    private final CancellationReason reason;
    private final String requestedBy;

    public CancellationRequest(String bookingId, CancellationReason reason, String requestedBy) {
        this.bookingId = bookingId;
        this.reason = reason;
        this.requestedBy = requestedBy;
    }

    public String getBookingId() {
        return bookingId;
    }

    public CancellationReason getReason() {
        return reason;
    }

    public String getRequestedBy() {
        return requestedBy;
    }
}
