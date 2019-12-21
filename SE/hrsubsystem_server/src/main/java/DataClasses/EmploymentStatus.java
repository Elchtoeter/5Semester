package DataClasses;

import java.io.Serializable;

public class EmploymentStatus implements Serializable {
    public EmploymentStatus(long uid, EStatus status) {
        this.uid = uid;
        this.status = status;
    }
    public static enum EStatus{
        EMPLOYED, TEMPORARY, TERMINATED
    }
    private final long uid;
    private final EStatus status;

    public long getUid() {
        return uid;
    }

    public EStatus getStatus() {
        return status;
    }



}
