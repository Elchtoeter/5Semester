package DataClasses;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    public User(Role role, String name, Date dateOfBirth, Pay pay, Vacation vacation, EmploymentStatus status) {
        this.role = role;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.pay = pay;
        this.vacation = vacation;
        this.status = status;
        uid = 0;
    }

    public static enum Role {
        USER, SUPERUSER
    }

    private final Role role;
    private final String name;
    private final Date dateOfBirth;
    private final Pay pay;
    private final Vacation vacation;
    private final long uid;
    private final EmploymentStatus status;

    public Role getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public Pay getPay() {
        return pay;
    }

    public Vacation getVacation() {
        return vacation;
    }

    public long getUid() {
        return uid;
    }

    public EmploymentStatus getStatus() {
        return status;
    }
}
