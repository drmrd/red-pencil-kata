package xyz.danielmoore.redpencilkata;

import java.time.OffsetDateTime;

/**
 * A basic product class for tracking the value of a product and the most
 * recent time at which the product's value changed.
 */
class Product {
    /**
     * The current value of the product.
     */
    private Price price;
    private Price preSalePrice;

    private TimestampGenerator timestampGenerator;
    private OffsetDateTime lastUpdated;
    private OffsetDateTime promotionStartTimestamp;
    private OffsetDateTime lastPromotionEndTimestamp;

    private boolean isPromoted;

    Product(Price price, TimestampGenerator timestampGenerator) {
        this.price = this.preSalePrice = price;
        this.timestampGenerator = timestampGenerator;
        this.lastUpdated = timestampGenerator.getCurrentTimestamp();
        this.promotionStartTimestamp = this.lastUpdated;
        /*
         * The kata doesn't say what to do in the first 30 days explicitly,
         * but we prevent promotions in the first 30 days to prevent vendors
         * from circumventing promotion rules by repeatedly creating new
         * products and discounting them immediately.
         */
        this.lastPromotionEndTimestamp = this.lastUpdated;
        this.isPromoted = false;
    }

    Price getPrice() {
        return price;
    }

    void setPrice(Price price) {
        OffsetDateTime updatedTimestamp = timestampGenerator
                .getCurrentTimestamp();
        if (priceChangeShouldCausePromotion(price)) {
            this.isPromoted = true;
            this.preSalePrice = this.price;
            this.promotionStartTimestamp = updatedTimestamp;
            // Insert other promo handling code here as needed.
        } else if (priceChangeShouldEndPromotion(price)) {
            this.isPromoted = false;
            this.preSalePrice = price;
            OffsetDateTime normalPromotionEndTimestamp = this.promotionStartTimestamp.plusDays(30);
            this.lastPromotionEndTimestamp = (updatedTimestamp
                    .isBefore(normalPromotionEndTimestamp)) ?
                    updatedTimestamp : normalPromotionEndTimestamp;
        }

        this.price = price;
        this.lastUpdated = updatedTimestamp;
    }

    OffsetDateTime getPriceUpdateTime() {
        return lastUpdated;
    }

    boolean isPromoted() {
        if (!(isPromoted && mostRecentPromotionStartedInLast30Days())) {
            isPromoted = false;
            this.preSalePrice = price;
            OffsetDateTime currentTimestamp = timestampGenerator
                    .getCurrentTimestamp();
            OffsetDateTime normalPromotionEndTimestamp = this.promotionStartTimestamp.plusDays(30);

            this.lastPromotionEndTimestamp = (currentTimestamp
                    .isBefore(normalPromotionEndTimestamp)) ?
                    currentTimestamp : normalPromotionEndTimestamp;
        }
        return isPromoted;
    }

    private boolean priceChangeShouldEndPromotion(Price newPrice) {
        if (!this.isPromoted()) return false;
        return price.isLessThan(newPrice) ||
                !priceChangeIsAtMost30Percent(preSalePrice, newPrice);
    }

    private boolean mostRecentPromotionStartedInLast30Days() {
        return promotionStartTimestamp.isAfter(timestampGenerator
                .getCurrentTimestamp().minusDays(30));
    }

    private boolean lastPromotionExpiredOver30DaysAgo() {
        if (this.isPromoted()) return false;
        return lastPromotionEndTimestamp == null ||
                lastPromotionEndTimestamp.isBefore(timestampGenerator
                        .getCurrentTimestamp().minusDays(30));
    }

    private boolean priceChangeShouldCausePromotion(Price newPrice) {
        return !this.isPromoted() && this.isStablyPriced() &&
                this.lastPromotionExpiredOver30DaysAgo() &&
                this.priceChangeIsWithinBounds(newPrice);
    }

    private boolean isStablyPriced() {
        return this.lastUpdated.isBefore(timestampGenerator.getCurrentTimestamp().minusDays(30));
    }

    private static boolean priceChangeIsAtMost30Percent(Price oldPrice,
                                                        Price newPrice) {
        return Price.absoluteDifference(oldPrice, newPrice)
                .isAtMostPercentOfPrice(30, oldPrice);
    }

    private boolean priceChangeIsWithinBounds(Price newPrice) {
        Price difference = Price.absoluteDifference(this.price, newPrice);

        return difference.isAtMostPercentOfPrice(30, this.price) &&
                difference.isAtLeastPercentOfPrice(5, this.price);
    }
}
