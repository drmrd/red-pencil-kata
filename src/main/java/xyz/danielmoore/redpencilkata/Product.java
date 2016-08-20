package xyz.danielmoore.redpencilkata;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * A basic product class for tracking the price of a product and the most
 * recent time at which the product's price changed.
 */
class Product {
    /**
     * The current price of the product. Storing using BigDecimal gives us
     * access to convenient arithmetic operations, rounding support, etc.
     */
    private BigDecimal price;
    private BigDecimal preSalePrice;

    /**
     * The current rounding mode used in price calculations. ROUND_HALF_EVEN
     * is also known as banker's rounding and is the standard in currency
     * calculations.
     */
    private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;

    /**
     * The decimal place to which prices should be rounded. The BigDecimal and
     * Currency packages make this easy to localize by, e.g., creating a
     * CURRENCY_NAME variable (example: "USD") and setting CURRENCY_PRECISION
     * equal to getInstance(CURRENCY_NAME).getDefaultFractionDigits().
     */
    private static final int CURRENCY_PRECISION = 2;
    // The number of decimal places to round when dividing BigDecimals.
    private static final int DIVISION_PRECISION = 4;

    private TimestampGenerator timestampGenerator;
    private OffsetDateTime lastUpdated;
    private OffsetDateTime promotionStartTimestamp;
    private OffsetDateTime lastPromotionEndTimestamp;
    private boolean isPromoted;

    Product(BigDecimal price, TimestampGenerator timestampGenerator) {
        // Round the input price to the nearest cent before use
        this.price = price.setScale(CURRENCY_PRECISION, ROUNDING_MODE);
        this.preSalePrice = this.price;
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

    BigDecimal getPrice() {
        return price;
    }

    void setPrice(BigDecimal price) {
        price = price.setScale(CURRENCY_PRECISION, ROUNDING_MODE);

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

    OffsetDateTime getLastPromotionEndDate() {
        return lastPromotionEndTimestamp;
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

    private boolean priceChangeShouldEndPromotion(BigDecimal price) {
        if (!this.isPromoted()) return false;
        return this.price.compareTo(price) < 0 ||
                !priceChangeIsAtMost30Percent(preSalePrice, price);
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

    private boolean priceChangeShouldCausePromotion(BigDecimal newPrice) {
        return !this.isPromoted() && this.hasStablePrice() &&
                this.lastPromotionExpiredOver30DaysAgo() &&
                this.priceChangeIsWithinBounds(newPrice);
    }

    private boolean hasStablePrice() {
        /*
         * TODO: Make sure that this also works within 30 days of product
         *       creation. (Ambiguous in kata instructions how to handle.)
         */
        return this.lastUpdated.isBefore(timestampGenerator.getCurrentTimestamp().minusDays(30));
    }

    private static boolean priceChangeIsAtMost30Percent(BigDecimal oldPrice,
                                                        BigDecimal newPrice) {
        BigDecimal relativeDifference = newPrice.divide(oldPrice,
                DIVISION_PRECISION, ROUNDING_MODE);

        return relativeDifference.compareTo(new BigDecimal(".70")) >= 0;
    }

    private boolean priceChangeIsWithinBounds(BigDecimal newPrice) {
        if (this.price.compareTo(newPrice) > 0) {
            BigDecimal relativeDifference = newPrice.divide(this.price,
                    DIVISION_PRECISION, ROUNDING_MODE);
            return relativeDifference.compareTo(new BigDecimal(".95")) <= 0
                    && relativeDifference.compareTo(new BigDecimal(".70")) >= 0;
        }
        return false;
    }
}
