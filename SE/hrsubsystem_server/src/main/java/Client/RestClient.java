package Client;

import DataClasses.EmploymentStatus;
import DataClasses.Pay;
import DataClasses.User;
import DataClasses.Vacation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.Date;

public class RestClient {

    public String convertToJson() throws JsonProcessingException {

        User user = new User(User.Role.SUPERUSER,"Bernhard Fuerst",new Date(2002,12,1),new Pay(),new Vacation(), new EmploymentStatus(1, EmploymentStatus.EStatus.EMPLOYED));

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(user);
        return json;
    }

}
