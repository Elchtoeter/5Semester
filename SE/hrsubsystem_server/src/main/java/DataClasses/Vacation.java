package DataClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vacation implements Serializable {
    private int daysLeft;
    private final List<BookedVacations> bookedVacationsList;

    public Vacation() {
        bookedVacationsList = new ArrayList<>();
    }

    public void addVacationDays(int daysToAdd){
        this.daysLeft += daysToAdd;
    }

    public int getDaysLeft(){
        return daysLeft;
    }

    public void addBookedVacation(BookedVacations newVacation){
        bookedVacationsList.add(newVacation);
    }

    public List<BookedVacations> getBookedVacationsList(){
        return Collections.unmodifiableList(bookedVacationsList);
    }


}
