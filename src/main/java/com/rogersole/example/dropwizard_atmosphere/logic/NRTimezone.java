package com.rogersole.example.dropwizard_atmosphere.logic;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.base.Optional;
import com.rogersole.example.dropwizard_atmosphere.exception.TimezoneException;

/**
 * Exposes the basic method to calculate a datetime given the timezone name.
 * 
 * @author rogersole
 *
 */
public class NRTimezone {
    /**
     * Given a timezone string, returns it's time in ISO8601 String format. If no time zone is given
     * or the given one is incorrect, an exception is returned.
     * 
     * @param timezone
     * @return ISO8601 string formated date+time
     * @throws TimezoneException
     */
    public static synchronized String calculateTimezone(Optional<String> timezone) throws TimezoneException {
        // check if the time zone is supplied
        if (!timezone.isPresent() || (timezone.isPresent() && timezone.get().length() == 0))
            throw new TimezoneException("No time zone specified");

        // check if the specified time zone exists and/or is correctly parseable
        DateTimeZone dtz = null;
        try {
            dtz = DateTimeZone.forID(timezone.get());
        }
        catch (IllegalArgumentException ex) {
            throw new TimezoneException("The specified time zone ('" + timezone.get() + "') is not valid");
        }

        // get current moment in default time zone
        DateTime dt = new DateTime();
        // translate to the specified one
        DateTime tzTime = dt.withZone(dtz);

        return tzTime.toString();
    }
}
