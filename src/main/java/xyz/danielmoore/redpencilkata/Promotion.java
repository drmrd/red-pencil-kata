package xyz.danielmoore.redpencilkata;

import java.time.OffsetDateTime;

/**
 * A class for tracking Red Pencil promotions of a product.
 */
class Promotion {

    private static final int PROMOTION_LENGTH_IN_DAYS = 30;

    private DateGenerator dateGenerator;
    private final OffsetDateTime startDate;
    private OffsetDateTime endDate;

    Promotion(DateGenerator dateGenerator) {
        this.dateGenerator = dateGenerator;
        this.startDate = dateGenerator.getCurrentDate();
        this.endDate = this.startDate.plusDays(PROMOTION_LENGTH_IN_DAYS);
    }

    OffsetDateTime getStartDate() { return startDate; }
    OffsetDateTime getEndDate() { return endDate; }

    boolean isActive() {
        return dateGenerator.getCurrentDate().isBefore(getEndDate());
    }

    void endNow() {
        if (!this.isActive()) {
            throw new IllegalStateException("Attempting to end a promotion " +
                    "that is already over.");
        }
        endDate = dateGenerator.getCurrentDate();
    }
}
