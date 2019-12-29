package DataClasses;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User implements Serializable {

    private final Role role;
    private final String name;
    private final LocalDate dateOfBirth;
    private final Pay pay;
    private final List<BookedVacations> vacations;
    private final long uid;
    private final int daysLeft;
    private final EmploymentStatus status;

    public User(Role role, String name, LocalDate dateOfBirth, Pay pay, int daysLeft, EmploymentStatus status) {
        this.role = role;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.pay = pay;
        this.daysLeft = daysLeft;
        this.vacations = new ArrayList<>();
        this.status = status;
        uid = 0;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public Role getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Pay getPay() {
        return pay;
    }

    public List<BookedVacations> getVacation() {
        return vacations;
    }

    public long getUid() {
        return uid;
    }

    public EmploymentStatus getStatus() {
        return status;
    }

    public enum Role {
        USER, SUPERUSER
    }
}
