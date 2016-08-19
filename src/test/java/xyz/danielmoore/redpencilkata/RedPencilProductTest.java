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

    @Before
    public void setUp() throws Exception {
        this.timestampGenerator = new MockTimestampGenerator();
        this.price = new BigDecimal(100);

        // Set the price update time to January 1st, 2016 at 12AM GMT
        this.creationTime = OffsetDateTime.of(2016, 1, 1, 0, 0, 0, 0,
                ZoneOffset.ofHours(0));
        timestampGenerator.setCurrentTimestamp(creationTime);

        product = new Product(price, timestampGenerator);
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
        BigDecimal twoHundred = new BigDecimal("200");
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

    @After
    public void tearDown() {
        product = null;
    }

}
