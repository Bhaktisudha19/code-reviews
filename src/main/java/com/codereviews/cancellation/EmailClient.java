package com.codereviews.cancellation;

import java.math.BigDecimal;

public interface EmailClient {
    void sendCancellationEmail(String email, String bookingId, BigDecimal refundAmount, String currency);
}
