package xyz.danielmoore.redpencilkata;

import java.time.OffsetDateTime;

/**
 * A class for tracking Red Pencil promotions of a product.
 */
public class Promotion {

    private DateGenerator dateGenerator;
    private final OffsetDateTime startDate;
    private OffsetDateTime endDate;

    Promotion(DateGenerator dateGenerator) {
        this.dateGenerator = dateGenerator;
        this.startDate = dateGenerator.getCurrentDate();
        this.endDate = this.startDate.plusDays(30);
    }
}
