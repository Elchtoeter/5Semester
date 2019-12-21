package DataClasses;

import java.io.Serializable;
import java.util.Date;

public class BookedVacations implements Serializable {
    private final Date start;
    private final Date end;

    public BookedVacations(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
}
