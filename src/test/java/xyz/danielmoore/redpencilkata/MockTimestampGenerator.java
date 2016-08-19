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

    public void setCurrentTimestamp(OffsetDateTime currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }
}
