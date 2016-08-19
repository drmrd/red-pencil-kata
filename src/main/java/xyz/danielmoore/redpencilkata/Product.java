package xyz.danielmoore.redpencilkata;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * A basic product class for tracking the price of a product and the most
 * recent time at which the product's price changed.
 */
class Product {

    private BigDecimal price;
    private TimestampGenerator timestampGenerator;
    private OffsetDateTime lastUpdated;

    public Product(BigDecimal price, TimestampGenerator timestampGenerator) {
        this.price = price;
        this.timestampGenerator = timestampGenerator;
        this.lastUpdated = timestampGenerator.getTimestamp();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OffsetDateTime getPriceUpdateTime() {
        return lastUpdated;
    }
}
