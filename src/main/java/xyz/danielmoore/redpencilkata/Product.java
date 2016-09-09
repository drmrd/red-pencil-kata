package xyz.danielmoore.redpencilkata;

import java.time.OffsetDateTime;

/**
 * A basic product class for tracking the value of a product and the most
 * recent time at which the product's value changed.
 */
class Product {

    /** The current value of the product. */
    private Price price;
    /** The price of the product before the most recent/current promotion. */
    private Price preSalePrice;

    /**
     * A timestamp-generating class instance used to track the date and time
     * of each price change of the product.
     */
    private DateGenerator dateGenerator;
    private OffsetDateTime lastPriceChangeDate;

    /** The product's most recent (or current) Red Pencil promotion. */
    private Promotion promotion;

    Product(Price price, DateGenerator dateGenerator) {
        this.price = this.preSalePrice = price;
        this.dateGenerator = dateGenerator;
        this.lastPriceChangeDate = dateGenerator.getCurrentDate();

        /*
         * We prevent promotions during the first 30 days after creating a
         * product to avoid vendors circumventing promotion eligibility rules by
         * repeatedly creating and discounting product pages for the same good.
         */
        this.promotion = new Promotion(dateGenerator);
        this.promotion.endNow();
    }

    Price getPrice() {
        return price;
    }
    OffsetDateTime getPriceUpdateTime() {
        return lastPriceChangeDate;
    }
    boolean isPromoted() { return this.promotion.isActive(); }

    void setPrice(Price price) {
        OffsetDateTime today = dateGenerator.getCurrentDate();

        updatePromotionStatus(price);

        this.price = price;
        this.lastPriceChangeDate = today;
    }

    private void updatePromotionStatus(Price newPrice) {
        if (isPromoted() && priceChangeBreaksPromotionRules(newPrice)) {
            this.preSalePrice = newPrice;
            this.promotion.endNow();
        } else if (eligibleForPromotion(newPrice)) {
            this.preSalePrice = this.price;
            this.promotion = new Promotion(dateGenerator);
        }
    }

    private boolean priceChangeBreaksPromotionRules(Price newPrice) {
        return price.isLessThan(newPrice) ||
                !priceChangeIsAtMost30Percent(preSalePrice, newPrice);
    }

    private boolean eligibleForPromotion(Price newPrice) {
        return !this.isPromoted() && this.isStablyPriced() &&
                promotion.gracePeriodIsOver() &&
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
