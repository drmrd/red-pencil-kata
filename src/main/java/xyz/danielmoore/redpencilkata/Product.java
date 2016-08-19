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
    private boolean isPromoted;

    Product(BigDecimal price, TimestampGenerator timestampGenerator) {
        this.price = price.setScale(CURRENCY_PRECISION);
        this.timestampGenerator = timestampGenerator;
        this.lastUpdated = timestampGenerator.getCurrentTimestamp();
        this.isPromoted = false;
    }

    BigDecimal getPrice() {
        return price;
    }

    void setPrice(BigDecimal price) {
        if (this.price.compareTo(price) > 0) {
            BigDecimal relativeDifference = price.divide(this.price,
                    DIVISION_PRECISION, ROUNDING_MODE);
            if (relativeDifference.compareTo(new BigDecimal(".95")) <= 0 &&
                    relativeDifference.compareTo(new BigDecimal(".70")) >= 0) {
                this.isPromoted = true;
            }
        }
        this.price = price.setScale(CURRENCY_PRECISION);
        this.lastUpdated = timestampGenerator.getCurrentTimestamp();
    }

    OffsetDateTime getPriceUpdateTime() {
        return lastUpdated;
    }

    boolean isPromoted() {
        return isPromoted;
    }
}
