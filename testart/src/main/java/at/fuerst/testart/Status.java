package at.fuerst.testart;

public enum Status {
    EMPLOYED("Employed"),
    TEMPORARY("Temporary"),
    TERMINATED("Terminated");

    private String displayValue;

    Status(String name){
        this.displayValue = name;
    }

    public String getDisplayValue(){
        return displayValue;
    }
}
