package com.codereviews.cancellation;

import java.math.BigDecimal;

public interface RefundGateway {
    String refund(String bookingId, BigDecimal amount, String currency);
}
