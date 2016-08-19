package xyz.danielmoore.redpencilkata;

import java.time.OffsetDateTime;

class MockTimestampGenerator extends TimestampGenerator {
    OffsetDateTime lastTimestamp;

    @Override
    OffsetDateTime getTimestamp() {
        lastTimestamp = OffsetDateTime.now();
        return lastTimestamp;
    }

    OffsetDateTime getLastTimestamp() {
        return lastTimestamp;
    }
}
