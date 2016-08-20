package xyz.danielmoore.redpencilkata;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.*;

public class RedPencilProductTest {

    private final BigDecimal TWO_HUNDRED = new BigDecimal("200.00");
    private final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");
    private final BigDecimal EIGHTY = new BigDecimal("80.00");
    private final BigDecimal SEVENTY_FIVE = new BigDecimal("75.00");
    private final BigDecimal SIXTY_FIVE = new BigDecimal("65.00");

    private final OffsetDateTime NOW = OffsetDateTime.now();

    private Product product;
    private BigDecimal price;
    private MockTimestampGenerator timestampGenerator;
    private OffsetDateTime creationTime;
    private OffsetDateTime currentTime;

    @Before
    public void setUp() throws Exception {
        this.timestampGenerator = new MockTimestampGenerator();
        this.price = ONE_HUNDRED;

        // Set the price update time to January 1st, 2016 at 12AM GMT
        this.creationTime = OffsetDateTime.of(2016, 1, 1, 0, 0, 0, 0,
                ZoneOffset.ofHours(0));

        timestampGenerator.setCurrentTimestamp(creationTime);
        product = new Product(price, timestampGenerator);

        // Set the timestampGenerator equal to NOW
        returnToThePresent();
    }

    @Test
    public void canCreateProductFromPrice() throws Exception {
        assertNotNull(product);
    }

    @Test
    public void canAccessTheCurrentPrice() {
        assertEquals(0, product.getPrice().compareTo(this.price));
    }

    @Test
    public void canSetTheCurrentPrice() {
        product.setPrice(TWO_HUNDRED);

        assertEquals(TWO_HUNDRED, product.getPrice());
    }

    @Test
    public void canAccessTheTimeOfLastPriceUpdate() {
        assertEquals(this.creationTime, product.getPriceUpdateTime());
    }

    @Test
    public void settingANewPriceUpdatesBothThePriceAndTime() {
        assertEquals(-1, product.getPrice().compareTo(TWO_HUNDRED));
        assertTrue(product.getPriceUpdateTime().isBefore(NOW));

        product.setPrice(TWO_HUNDRED);

        assertEquals(0, product.getPrice().compareTo(TWO_HUNDRED));
        assertEquals(NOW, product.getPriceUpdateTime());
    }

    @Test
    public void canCheckIfProductIsPromoted() {
        assertFalse(product.isPromoted());
    }

    @Test
    public void changingPriceOnNewProductChangesPromotionStatus() {
        product.setPrice(SEVENTY_FIVE);
        assertTrue(product.isPromoted());
    }

    @Test
    public void changingPriceLessThan5PercentDoesNotLeadToPromotion() {
        product.setPrice(new BigDecimal("95.01"));
        assertFalse(product.isPromoted());
    }

    @Test
    public void changingPriceMoreThan30PercentDoesNotLeadToPromotion() {
        product.setPrice(new BigDecimal("69.9"));
        assertFalse(product.isPromoted());
    }

    @Test
    public void aPriceChangeWithin30DaysOfThePreviousPriceChangeDoesNotLeadToPromotion() {
        travelThroughTime(-15);
        product.setPrice(TWO_HUNDRED);
        assertFalse(product.isPromoted());

        BigDecimal cheatingPromoPrice = new BigDecimal("140.00");
        returnToThePresent();
        product.setPrice(cheatingPromoPrice);
        assertFalse(product.isPromoted());
    }

    @Test
    public void promotionsEndWithin30Days() {
        travelThroughTime(-30);
        product.setPrice(SEVENTY_FIVE);

        returnToThePresent();
        assertFalse(product.isPromoted());
    }

    @Test
    public void priceChangeDuringPromotionDoesNotExtendPromotionLength() {
        travelThroughTime(-30);
        product.setPrice(EIGHTY);

        travelThroughTime(29);
        product.setPrice(SEVENTY_FIVE);

        returnToThePresent();
        assertFalse(product.isPromoted());
    }

    @Test
    public void increasingPriceDuringPromotionEndsThePromotion() {
        product.setPrice(SEVENTY_FIVE);
        assertTrue(product.isPromoted());

        product.setPrice(EIGHTY);
        assertFalse(product.isPromoted());
    }

    @Test
    public void reducingPriceDuringPromotionMoreThan30PercentOfOriginalEndsPromotion() {
        product.setPrice(SEVENTY_FIVE);

        /*
         * Set product to an amount less than 70% of original and more than 70%
         * of promo price
         */
        product.setPrice(SIXTY_FIVE);

        assertFalse(product.isPromoted());
    }

    @Test
    public void cantStartNewPromotionWithin30DaysOfLastOne() {
        travelThroughTime(-59);
        product.setPrice(EIGHTY);

        returnToThePresent();

        /*
         * A new price in the valid promo range for both the original and
         * promo price
         */
        product.setPrice(SEVENTY_FIVE);

        assertFalse(product.isPromoted());
    }

    @Test
    public void canStartNewPromotion30DaysAfterTheLastOne() {
        travelThroughTime(-60);
        product.setPrice(SEVENTY_FIVE);

        returnToThePresent();
        waitAnHour(); // Move past end of 30 day grace period.

        /*
         * A new price that is less than 70% of original and more than 70%
         * of promo price.
         */
        product.setPrice(SIXTY_FIVE);
        assertTrue(product.isPromoted());
    }

    @After
    public void tearDown() {
        product = null;
        returnToThePresent();
    }

    private void returnToThePresent() {
        timestampGenerator.setCurrentTimestamp(NOW);
    }

    private void travelThroughTime(int daysToTravel) {
        timestampGenerator.addDaysToCurrentTimestamp(daysToTravel);
    }

    private void waitAnHour() {
        timestampGenerator.addHoursToCurrentTimestamp(1);
    }
}
