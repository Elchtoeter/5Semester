package Server;

import DataClasses.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HRService {

    @RequestMapping("/getUser")
    public User getUser(@RequestParam(value = "id") String name) {
        return null;
    }
}
