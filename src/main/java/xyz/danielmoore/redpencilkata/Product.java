package xyz.danielmoore.redpencilkata;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * A basic product class for tracking the price of a product and the most
 * recent time at which the product's price changed.
 */
class Product {

    private BigDecimal price;
    /**
     * The current rounding mode used in price calculations. ROUND_HALF_EVEN
     * is also known as banker's rounding and is the standard in currency
     * calculations.
     */
    private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;
    private static final int CURRENCY_PRECISION = 2;
    private static final int DIVISION_PRECISION = 4;

    private TimestampGenerator timestampGenerator;
    private OffsetDateTime lastUpdated;
    private boolean isPromoted;

    public Product(BigDecimal price, TimestampGenerator timestampGenerator) {
        this.price = price.setScale(CURRENCY_PRECISION);
        this.timestampGenerator = timestampGenerator;
        this.lastUpdated = timestampGenerator.getCurrentTimestamp();
        this.isPromoted = false;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price.divide(this.price, DIVISION_PRECISION, ROUNDING_MODE)
                .compareTo(new BigDecimal(".95")) <= 0) {
            this.isPromoted = true;
        }
        this.price = price.setScale(CURRENCY_PRECISION);
        this.lastUpdated = timestampGenerator.getCurrentTimestamp();
    }

    public OffsetDateTime getPriceUpdateTime() {
        return lastUpdated;
    }

    public boolean isPromoted() {
        return isPromoted;
    }
}
