package at.fuerst.testart;

import javafx.scene.control.Skin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Date;
import java.time.LocalDate;

@Controller
public class SimpleController {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public SimpleController(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    @Value("${spring.application.name}")
    String appName;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        Employee Test = new Employee();
        Test.setDob(Date.valueOf(LocalDate.of(1995,9,3)));
        Test.setName("Bernhard Fuerst");
        Test.setEmployed("Em");
        employeeRepository.save(Test);
        System.out.println("fooo");
        model.addAttribute("employees", employeeRepository.findAll());
        return "home";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Employee employee) {
        return "add-user";
    }

    @PostMapping("/adduser")
    public String addUser(@Valid Employee employee, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-user";
        }
        employeeRepository.save(employee);
        model.addAttribute("employees", employeeRepository.findAll());
        return "home";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        model.addAttribute("employee", employee);
        return "update-user";
    }
}