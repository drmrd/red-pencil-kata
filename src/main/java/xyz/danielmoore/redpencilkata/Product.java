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
        this.promotion = new Promotion(dateGenerator);
    }

    Price getPrice() {
        return price;
    }
    OffsetDateTime getPriceUpdateTime() {
        return lastPriceChangeDate;
    }

    void setPrice(Price price) {
        OffsetDateTime today = dateGenerator.getCurrentDate();

        updatePromotionStatus(price);

        this.price = price;
        this.lastPriceChangeDate = today;
    }

    private void updatePromotionStatus(Price newPrice) {
        if (priceChangeShouldCausePromotion(newPrice)) {
            this.preSalePrice = this.price;
            this.promotion = new Promotion(dateGenerator);
        } else if (priceChangeShouldEndPromotion(newPrice)) {
            this.preSalePrice = newPrice;
            this.promotion.endNow();
        }
    }

    boolean isPromoted() {
        return this.promotion.isActive();
    }

    private boolean priceChangeShouldEndPromotion(Price newPrice) {
        if (!this.isPromoted()) return false;
        return price.isLessThan(newPrice) ||
                !priceChangeIsAtMost30Percent(preSalePrice, newPrice);
    }

    private boolean promotionGracePeriodIsOver() {
        if (this.isPromoted()) return false;
        return promotion.getEndDate() == null ||
                promotion.getEndDate().isBefore(dateGenerator
                        .getCurrentDate().minusDays(30));
    }

    private boolean priceChangeShouldCausePromotion(Price newPrice) {
        return !this.isPromoted() && this.isStablyPriced() &&
                this.promotionGracePeriodIsOver() &&
                priceChangeIsAtLeast5Percent(this.price, newPrice) &&
                priceChangeIsAtMost30Percent(this.price, newPrice);
    }

    private boolean isStablyPriced() {
        return this.lastPriceChangeDate.isBefore(dateGenerator.getCurrentDate()
                .minusDays(30));
    }

    private static boolean priceChangeIsAtLeast5Percent(Price oldPrice,
                                                        Price newPrice) {
        return Price.absoluteDifference(oldPrice, newPrice)
                .isAtLeastPercentOfPrice(5, oldPrice);
    }

    private static boolean priceChangeIsAtMost30Percent(Price oldPrice,
                                                        Price newPrice) {
        return Price.absoluteDifference(oldPrice, newPrice)
                .isAtMostPercentOfPrice(30, oldPrice);
    }
}
