package xyz.danielmoore.redpencilkata;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RedPencilProductTest {

    private final BigDecimal TWO_HUNDRED = new BigDecimal("200.00");
    private final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");
    private final BigDecimal SEVENTY_FIVE = new BigDecimal("75.00");

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
        this.currentTime = NOW;

        timestampGenerator.setCurrentTimestamp(creationTime);
        product = new Product(price, timestampGenerator);

        timestampGenerator.setCurrentTimestamp(currentTime);
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
        assertTrue(product.getPriceUpdateTime().isBefore(this.currentTime));

        this.timestampGenerator.setCurrentTimestamp(this.currentTime);
        product.setPrice(TWO_HUNDRED);

        assertEquals(0, product.getPrice().compareTo(TWO_HUNDRED));
        assertEquals(this.currentTime, product.getPriceUpdateTime());
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
        timestampGenerator.setCurrentTimestamp(NOW.minusDays(15));
        product.setPrice(TWO_HUNDRED);
        assertFalse(product.isPromoted());

        BigDecimal cheatingPromoPrice = new BigDecimal("140.00");
        timestampGenerator.setCurrentTimestamp(NOW);
        product.setPrice(cheatingPromoPrice);
        assertFalse(product.isPromoted());
    }

    @Test
    public void promotionsEndWithin30Days() {
        timestampGenerator.setCurrentTimestamp(NOW.minusDays(31));
        product.setPrice(SEVENTY_FIVE);

        assertFalse(product.isPromoted());
    }

    @After
    public void tearDown() {
        product = null;
    }
}
