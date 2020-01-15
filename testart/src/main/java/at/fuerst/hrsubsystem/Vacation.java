package at.fuerst.hrsubsystem;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="vacation")
public class Vacation {

    private LocalDate startDate, endDate;
    @Id
    @GeneratedValue
    private long vac_id;

    public long getVac_id() {
        return vac_id;
    }

    public void setVac_id(long vac_id) {
        this.vac_id = vac_id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    public void setStartDate(LocalDate start) {
        this.startDate = start;
    }

    public void setEndDate(LocalDate end) {
        this.endDate = end;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
