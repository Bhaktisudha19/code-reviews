import test from "node:test";
import assert from "node:assert/strict";

import { clearQuoteCache, quoteHotelStay } from "../src/hotelQuoteService.js";

test("quotes a one-night weekday stay", () => {
  clearQuoteCache();

  const quote = quoteHotelStay({
    checkIn: "2026-07-01",
    checkOut: "2026-07-02",
    rooms: 1,
    guests: 2,
    nightlyRate: 100,
    currency: "USD"
  });

  assert.deepEqual(quote, {
    nights: 1,
    rooms: 1,
    guests: 2,
    currency: "USD",
    subtotal: 100,
    discount: 0,
    tax: 12,
    total: 112
  });
});

test("applies weekend pricing and loyalty discount", () => {
  clearQuoteCache();

  const quote = quoteHotelStay({
    checkIn: "2026-07-03",
    checkOut: "2026-07-05",
    rooms: 1,
    guests: 2,
    nightlyRate: 100,
    loyaltyTier: "gold"
  });

  assert.equal(quote.nights, 2);
  assert.equal(quote.subtotal, 236);
  assert.equal(quote.discount, 16.52);
  assert.equal(quote.tax, 26.34);
  assert.equal(quote.total, 245.82);
});

test("adds extra guest fees when room capacity is exceeded", () => {
  clearQuoteCache();

  const quote = quoteHotelStay({
    checkIn: "2026-07-06",
    checkOut: "2026-07-07",
    rooms: 1,
    guests: 3,
    nightlyRate: 120
  });

  assert.equal(quote.subtotal, 145);
  assert.equal(quote.total, 162.4);
});

test("uses the cached quote for repeated requests", () => {
  clearQuoteCache();

  const request = {
    checkIn: "2026-07-06",
    checkOut: "2026-07-07",
    rooms: 1,
    guests: 1,
    nightlyRate: 90
  };

  const firstQuote = quoteHotelStay(request);
  const secondQuote = quoteHotelStay(request);

  assert.equal(firstQuote, secondQuote);
});
