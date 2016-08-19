package xyz.danielmoore.redpencilkata;


import java.time.OffsetDateTime;

class TimestampGenerator {

    /**
     * Return a timestamp for the current instant.
     */
    OffsetDateTime getTimestamp() {
        return OffsetDateTime.now();
    }

}
