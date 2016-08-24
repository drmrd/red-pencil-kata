package xyz.danielmoore.redpencilkata;


import java.time.OffsetDateTime;

class DateGenerator {

    /**
     * Return the date/time for the current instant.
     */
    OffsetDateTime getCurrentDate() {
        return OffsetDateTime.now();
    }
}
