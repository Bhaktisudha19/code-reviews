package com.codereviews.loyalty;

public class LoyaltyAccount {
    private final String customerId;
    private Tier tier;
    private int points;

    public LoyaltyAccount(String customerId, Tier tier, int points) {
        this.customerId = customerId;
        this.tier = tier;
        this.points = points;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Tier getTier() {
        return tier;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
        if (this.points > 100_000) {
            this.tier = Tier.PLATINUM;
        } else if (this.points > 40_000) {
            this.tier = Tier.GOLD;
        } else if (this.points > 10_000) {
            this.tier = Tier.SILVER;
        }
    }
}
