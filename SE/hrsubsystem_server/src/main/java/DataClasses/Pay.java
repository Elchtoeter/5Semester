package DataClasses;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

public class Pay implements Serializable {


    private Currency currency;
    private BigDecimal pay;
    private final long uid;

    public Pay(long uid) {
        this.uid = uid;
    }

    public void setPay(BigDecimal pay){
        this.pay = pay;
    }

    public void setCurrency(Currency currency){
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getPay() {
        return pay;
    }
}
