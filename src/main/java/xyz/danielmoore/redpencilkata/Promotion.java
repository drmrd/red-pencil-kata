package xyz.danielmoore.redpencilkata;

import java.time.OffsetDateTime;

/**
 * A class for tracking Red Pencil promotions of a product.
 */
class Promotion {

    private static final int LENGTH_IN_DAYS = 30;
    private static final int GRACE_PERIOD_IN_DAYS = 30;

    private DateGenerator dateGenerator;
    private final OffsetDateTime startDate;
    private OffsetDateTime endDate;

    Promotion(DateGenerator dateGenerator) {
        this.dateGenerator = dateGenerator;
        this.startDate = dateGenerator.getCurrentDate();
        this.endDate = this.startDate.plusDays(LENGTH_IN_DAYS);
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

    boolean gracePeriodIsOver() {
        return this.getEndDate().isBefore(dateGenerator.getCurrentDate()
                .minusDays(GRACE_PERIOD_IN_DAYS));
    }
}
