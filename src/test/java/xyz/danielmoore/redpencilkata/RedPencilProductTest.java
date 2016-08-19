package xyz.danielmoore.redpencilkata;

import java.math.BigDecimal;

import org.junit.Test;

import static org.junit.Assert.*;

public class RedPencilProductTest {

    private Product product;
    private BigDecimal price;
    private TimestampGenerator timestampGenerator;

    @Test
    public void canCreateProductFromPrice() throws Exception {
        this.timestampGenerator = new TimestampGenerator();
        this.price = new BigDecimal(100);

        product = new Product(price, timestampGenerator);
        assertNotNull(product);
    }

}
