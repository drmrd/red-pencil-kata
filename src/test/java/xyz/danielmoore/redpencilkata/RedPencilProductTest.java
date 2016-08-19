package xyz.danielmoore.redpencilkata;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RedPencilProductTest {

    private Product product;
    private BigDecimal price;
    private MockTimestampGenerator timestampGenerator;
    private OffsetDateTime creationTime;
    private OffsetDateTime today;

    @Before
    public void setUp() throws Exception {
        this.timestampGenerator = new MockTimestampGenerator();
        this.price = new BigDecimal(100);

        // Set the price update time to January 1st, 2016 at 12AM GMT
        this.creationTime = OffsetDateTime.of(2016, 1, 1, 0, 0, 0, 0,
                ZoneOffset.ofHours(0));
        this.today = OffsetDateTime.now();

        timestampGenerator.setCurrentTimestamp(creationTime);
        product = new Product(price, timestampGenerator);

        timestampGenerator.setCurrentTimestamp(today);
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
        BigDecimal twoHundred = new BigDecimal("200.00");
        product.setPrice(twoHundred);

        assertEquals(twoHundred, product.getPrice());
    }

    @Test
    public void canAccessTheTimeOfLastPriceUpdate() {
        assertEquals(this.creationTime, product.getPriceUpdateTime());
    }

    @Test
    public void settingANewPriceUpdatesBothThePriceAndTime() {
        OffsetDateTime currentTime = OffsetDateTime.now();
        BigDecimal newPrice = new BigDecimal("200");

        assertEquals(-1, product.getPrice().compareTo(newPrice));
        assertTrue(product.getPriceUpdateTime().isBefore(currentTime));

        this.timestampGenerator.setCurrentTimestamp(currentTime);
        product.setPrice(newPrice);

        assertEquals(0, product.getPrice().compareTo(newPrice));
        assertEquals(currentTime, product.getPriceUpdateTime());
    }

    @Test
    public void canCheckIfProductIsPromoted() {
        assertFalse(product.isPromoted());
    }

    @Test
    public void changingPriceOnNewProductChangesPromotionStatus() {
        product.setPrice(new BigDecimal("75"));
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
        OffsetDateTime today = OffsetDateTime.now();
        OffsetDateTime fifteenDaysAgo = today.minusDays(15);
        BigDecimal twoHundred = new BigDecimal("200.00");
        BigDecimal cheatingPromoPrice = new BigDecimal("140.00");

        timestampGenerator.setCurrentTimestamp(fifteenDaysAgo);
        product.setPrice(twoHundred);
        assertFalse(product.isPromoted());

        timestampGenerator.setCurrentTimestamp(today);
        product.setPrice(cheatingPromoPrice);
        assertFalse(product.isPromoted());
    }

    @After
    public void tearDown() {
        product = null;
    }
}
