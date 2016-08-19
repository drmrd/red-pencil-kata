package xyz.danielmoore.redpencilkata;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RedPencilProductTest {

    private Product product;
    private BigDecimal price;
    private TimestampGenerator timestampGenerator;

    @Before
    public void setUp() throws Exception {
        this.timestampGenerator = new TimestampGenerator();
        this.price = new BigDecimal(100);

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

    @After
    public void tearDown() {
        product = null;
    }

}
