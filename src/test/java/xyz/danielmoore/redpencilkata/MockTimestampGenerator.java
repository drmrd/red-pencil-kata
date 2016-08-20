package xyz.danielmoore.redpencilkata;

import java.time.OffsetDateTime;

class MockTimestampGenerator extends TimestampGenerator {
    private OffsetDateTime currentTimestamp;

    @Override
    OffsetDateTime getCurrentTimestamp() {
        if (currentTimestamp == null) {
            currentTimestamp = OffsetDateTime.now();
        }
        return currentTimestamp;
    }

    void setCurrentTimestamp(OffsetDateTime currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }

    void addDaysToCurrentTimestamp(int days) {
        this.currentTimestamp = this.currentTimestamp.plusDays(days);
    }

    void addHoursToCurrentTimestamp(int hours) {
        this.currentTimestamp = this.currentTimestamp.plusHours(hours);
    }
}
