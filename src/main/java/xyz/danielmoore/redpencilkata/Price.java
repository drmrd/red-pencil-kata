package xyz.danielmoore.redpencilkata;

import java.math.BigDecimal;

/**
 * A simple class for tracking the price of a good or service.
 */
public final class Price implements Comparable{

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

    /**
     * The number of decimal places to round to when dividing BigDecimals.
     */
    private static final int DIVISION_PRECISION = 4;

    /**
     * Storing using BigDecimal provides access to convenient arithmetic
     * operations, rounding support, etc.
     */
    BigDecimal value;

    public Price(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Prices must be non-negative.");
        }

        // Round the input value to the appropriate precision before use
        this.value = value.setScale(CURRENCY_PRECISION, ROUNDING_MODE);
    }

    public Price(double value) {
        this(BigDecimal.valueOf(value));
    }

    public static Price absoluteDifference(Price price1, Price price2) {
        return new Price(price1.value.subtract(price2.value).abs());
    }

    public double percentOfPrice(Price that) {
        return this.value.divide(that.value, DIVISION_PRECISION, ROUNDING_MODE)
                .doubleValue() * 100;
    }

    public boolean isAtMostPercentOfPrice(double percent, Price that) {
        return this.percentOfPrice(that) <= percent;
    }

    public boolean isAtLeastPercentOfPrice(double percent, Price that) {
        return this.percentOfPrice(that) >= percent;
    }

    public boolean isLessThan(Price that) {
        return this.compareTo(that) == -1;
    }

    @Override
    public int compareTo(Object o) {
        Price that = (Price) o;
        return this.value.compareTo(that.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        Price that = (Price) obj;
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
