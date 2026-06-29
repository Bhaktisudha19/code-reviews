package com.codereviews.cancellation;

public interface InventoryClient {
    void releaseRoom(String hotelId, String bookingId);
}
