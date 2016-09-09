package xyz.danielmoore.redpencilkata;

import java.time.OffsetDateTime;

/** A simple class for providing the current date and time. */
class DateGenerator {

    /** Return the date/time for the current instant. */
    OffsetDateTime getCurrentDate() {
        return OffsetDateTime.now();
    }

}
