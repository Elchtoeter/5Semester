package at.fuerst.hrsubsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Controller
public class SimpleController {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public SimpleController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Value("${spring.application.name}")
    String appName;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("employees", employeeRepository.findAll());
        return "redirect:home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("appName", appName);
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
        return "redirect:home";
    }

    @RequestMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id, Model model) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        employeeRepository.delete(employee);
        model.addAttribute("users", employeeRepository.findAll());
        return "redirect:/home";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("employee", employee);
        return "update-user";
    }

    @RequestMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid Employee employee,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            employee.setEmployee_id(id);
            return "update-user";
        }
        employeeRepository.deleteById(id);
        employeeRepository.save(employee);
        model.addAttribute("users", employeeRepository.findAll());
        return "redirect:/home";
    }

    @RequestMapping("/status")
    public @ResponseBody EmployeeStatus getEmployeeStatus(@RequestParam("id")long id){
        Employee emp = employeeRepository.findById(id).orElseThrow(()-> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        ));
        return new EmployeeStatus(emp.getEmployed().getDisplayValue(),emp.getEmployee_id());
    }

    private class EmployeeStatus {
        public final String employed;
        public final long id;

        private EmployeeStatus(String employed, long id) {
            this.employed = employed;
            this.id = id;
        }
    }
}