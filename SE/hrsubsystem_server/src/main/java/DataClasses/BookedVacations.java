package DataClasses;

import java.io.Serializable;
import java.time.*;

public class BookedVacations implements Serializable {
    private final LocalDate start;
    private final LocalDate end;
    private final long uid;

    public BookedVacations(LocalDate start, LocalDate end, long uid) {
        this.start = start;
        this.end = end;
        this.uid = uid;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }
}
