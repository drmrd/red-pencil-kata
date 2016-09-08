package xyz.danielmoore.redpencilkata;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.*;

public class RedPencilProductTest {

    private final Price TWO_HUNDRED = new Price(200);
    private final Price ONE_HUNDRED = new Price(100);
    private final Price EIGHTY = new Price(80);
    private final Price SEVENTY_FIVE = new Price(75);
    private final Price SIXTY_FIVE = new Price(65);

    private final OffsetDateTime NOW = OffsetDateTime.now();

    private Product product;
    private Price price;
    private MockDateGenerator dateGenerator;
    private OffsetDateTime creationTime;

    @Before
    public void setUp() throws Exception {
        this.dateGenerator = new MockDateGenerator();
        this.price = ONE_HUNDRED;

        // Set the value update time to January 1st, 2016 at 12AM GMT
        this.creationTime = OffsetDateTime.of(2016, 1, 1, 0, 0, 0, 0,
                ZoneOffset.ofHours(0));

        dateGenerator.setCurrentDate(creationTime);
        product = new Product(price, dateGenerator);

        // Set the dateGenerator equal to NOW
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
        product.setPrice(new Price(new BigDecimal("95.01")));
        assertFalse(product.isPromoted());
    }

    @Test
    public void changingPriceMoreThan30PercentDoesNotLeadToPromotion() {
        product.setPrice(new Price(new BigDecimal("69.9")));
        assertFalse(product.isPromoted());
    }

    @Test
    public void aPriceChangeWithin30DaysOfThePreviousPriceChangeDoesNotLeadToPromotion() {
        travelThroughTime(-15);
        product.setPrice(TWO_HUNDRED);
        assertFalse(product.isPromoted());

        Price cheatingPromoPrice = new Price(new BigDecimal("140.00"));
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
         * of promo value
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
         * A new value in the valid promo range for both the original and
         * promo value
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
         * A new value that is less than 70% of original and more than 70%
         * of promo value.
         */
        product.setPrice(SIXTY_FIVE);
        assertTrue(product.isPromoted());
    }

    @Test
    public void priceChangesInFirst30DaysDoNotStartPromotions() {
        travelThroughTime(creationTime.plusDays(15));
        product.setPrice(SEVENTY_FIVE);

        assertFalse(product.isPromoted());
    }

    @Test
    public void newProductsDoNotBeginPromoted() {
        travelThroughTime(creationTime);
        assertFalse(product.isPromoted());

        waitAnHour();
        assertFalse(product.isPromoted());
    }

    @Test
    public void canStartNewPromotion30DaysAfterLastPromotionBrokeRules() {
        travelThroughTime(-30);
        product.setPrice(SEVENTY_FIVE); // Promotion-inducing price change
        product.setPrice(SIXTY_FIVE); // Price change violating promotion rules
        assertFalse(product.isPromoted());

        product.setPrice(ONE_HUNDRED); // Reset product price (for clarity)

        returnToThePresent();
        waitAnHour();
        product.setPrice(SEVENTY_FIVE);
        assertTrue(product.isPromoted());
    }

    @After
    public void tearDown() {
        product = null;
        returnToThePresent();
    }

    private void returnToThePresent() {
        dateGenerator.setCurrentDate(NOW);
    }

    private void travelThroughTime(int daysToTravel) {
        dateGenerator.addDaysToCurrentDate(daysToTravel);
    }

    private void travelThroughTime(OffsetDateTime specificDate) {
        dateGenerator.setCurrentDate(specificDate);
    }

    private void waitAnHour() {
        dateGenerator.addHoursToCurrentDate(1);
    }
}
