const quoteCache = new Map();

const TAX_RATE = 0.12;
const WEEKEND_MULTIPLIER = 1.18;
const LOYALTY_DISCOUNTS = {
  silver: 0.03,
  gold: 0.07,
  platinum: 0.12
};

export function quoteHotelStay(request) {
  const cacheKey = JSON.stringify(request);

  if (quoteCache.has(cacheKey)) {
    return quoteCache.get(cacheKey);
  }

  const checkIn = new Date(request.checkIn);
  const checkOut = new Date(request.checkOut);
  const nights = Math.ceil((checkOut - checkIn) / 86400000);
  const rooms = request.rooms || 1;
  const guests = request.guests || rooms;
  const loyaltyTier = request.loyaltyTier || "none";
  const nightlyRate = request.nightlyRate;

  let subtotal = 0;

  for (let offset = 0; offset < nights; offset += 1) {
    const stayDate = new Date(checkIn);
    stayDate.setDate(checkIn.getDate() + offset);

    let dayRate = nightlyRate;
    const day = stayDate.getDay();

    if (day === 5 || day === 6) {
      dayRate *= WEEKEND_MULTIPLIER;
    }

    subtotal += dayRate * rooms;
  }

  if (guests > rooms * 2) {
    subtotal += (guests - rooms * 2) * 25;
  }

  const discount = subtotal * (LOYALTY_DISCOUNTS[loyaltyTier] || 0);
  const taxableAmount = subtotal - discount;
  const tax = taxableAmount * TAX_RATE;
  const total = taxableAmount + tax;

  const quote = {
    nights,
    rooms,
    guests,
    currency: request.currency || "USD",
    subtotal: roundMoney(subtotal),
    discount: roundMoney(discount),
    tax: roundMoney(tax),
    total: roundMoney(total)
  };

  quoteCache.set(cacheKey, quote);
  return quote;
}

export function clearQuoteCache() {
  quoteCache.clear();
}

function roundMoney(value) {
  return Math.round(value * 100) / 100;
}
