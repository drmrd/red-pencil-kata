package xyz.danielmoore.redpencilkata;

import java.time.OffsetDateTime;

class MockDateGenerator extends DateGenerator {
    private OffsetDateTime currentDate;

    @Override
    OffsetDateTime getCurrentDate() {
        if (currentDate == null) {
            currentDate = OffsetDateTime.now();
        }
        return currentDate;
    }

    void setCurrentDate(OffsetDateTime currentDate) {
        this.currentDate = currentDate;
    }

    void addDaysToCurrentDate(int days) {
        this.currentDate = this.currentDate.plusDays(days);
    }

    void addHoursToCurrentDate(int hours) {
        this.currentDate = this.currentDate.plusHours(hours);
    }
}
