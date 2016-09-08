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

    private DateGenerator dateGenerator;
    private OffsetDateTime lastPriceChangeDate;

    private Promotion promotion;

    private boolean isPromoted;

    Product(Price price, DateGenerator dateGenerator) {
        this.price = this.preSalePrice = price;
        this.dateGenerator = dateGenerator;
        this.lastPriceChangeDate = dateGenerator.getCurrentDate();
        /*
         * The kata doesn't say what to do in the first 30 days explicitly,
         * but we prevent promotions in the first 30 days to prevent vendors
         * from circumventing promotion rules by repeatedly creating new
         * products and discounting them immediately.
         */
        this.isPromoted = false;

        this.promotion = new Promotion(dateGenerator);
    }

    Price getPrice() {
        return price;
    }

    void setPrice(Price price) {
        OffsetDateTime today = dateGenerator.getCurrentDate();
        if (priceChangeShouldCausePromotion(price)) {
            this.isPromoted = true;
            this.preSalePrice = this.price;

            this.promotion = new Promotion(dateGenerator);
        } else if (priceChangeShouldEndPromotion(price)) {
            this.isPromoted = false;
            this.preSalePrice = price;
            this.promotion.endNow();
        }

        this.price = price;
        this.lastPriceChangeDate = today;
    }

    OffsetDateTime getPriceUpdateTime() {
        return lastPriceChangeDate;
    }

    boolean isPromoted() {
        if (!(isPromoted && mostRecentPromotionStartedInLast30Days())) {
            isPromoted = false;
            this.preSalePrice = price;
        }
        return isPromoted;
    }

    private boolean priceChangeShouldEndPromotion(Price newPrice) {
        if (!this.isPromoted()) return false;
        return price.isLessThan(newPrice) ||
                !priceChangeIsAtMost30Percent(preSalePrice, newPrice);
    }

    private boolean mostRecentPromotionStartedInLast30Days() {
        return promotion.getStartDate().isAfter(dateGenerator
                .getCurrentDate().minusDays(30));
    }

    private boolean lastPromotionExpiredOver30DaysAgo() {
        if (this.isPromoted()) return false;
        return promotion.getEndDate() == null ||
                promotion.getEndDate().isBefore(dateGenerator
                        .getCurrentDate().minusDays(30));
    }

    private boolean priceChangeShouldCausePromotion(Price newPrice) {
        return !this.isPromoted() && this.isStablyPriced() &&
                this.lastPromotionExpiredOver30DaysAgo() &&
                this.priceChangeIsWithinBounds(newPrice);
    }

    private boolean isStablyPriced() {
        return this.lastPriceChangeDate.isBefore(dateGenerator.getCurrentDate().minusDays(30));
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
