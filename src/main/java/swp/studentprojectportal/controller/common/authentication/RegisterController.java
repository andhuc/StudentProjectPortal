package swp.studentprojectportal.controller.common.authentication;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import swp.studentprojectportal.model.User;
import swp.studentprojectportal.service.servicesimpl.SettingService;
import swp.studentprojectportal.service.servicesimpl.UserService;
import swp.studentprojectportal.utils.Validate;

import java.security.NoSuchAlgorithmException;

@Controller
public class RegisterController {
    @Autowired
    int userRoleId;
    @Autowired
    UserService userService;
    @Autowired
    SettingService settingService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registerAccount(WebRequest request, Model model, HttpSession session)
            throws NoSuchAlgorithmException {
        // get parameter from request
        String fullname = request.getParameter("fullname").trim();
        String termCheckbox = request.getParameter("termCheckbox");
        String username = request.getParameter("username").replace(" ", "");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // set session to save data
        session.setAttribute("r-fullname", fullname);
        session.setAttribute("r-username", username);

        // create model user
        User user = new User();
        user.setActive(false);
        user.setFullName(fullname);
        user.setPassword(password);
        user.setSetting(settingService.findById(userRoleId));

        if (Validate.validEmail(username)) {
            user.setEmail(username);
            session.setAttribute("verifyMail", true);
        }
        if (Validate.validPhoneNumber(username)) {
            user.setPhone(username);
            session.setAttribute("verifyMail", false);
        }

        // Validate Full Name
        if (!Validate.validFullname(fullname)) {
            return handleError(model, session, "Fullname must contain at least 2 words");
        }

        // Check Term Checkbox
        if (termCheckbox == null) {
            return handleError(model, session, "You must agree with our terms and conditions");
        }

        // Validate Email or Phone
        if (user.getEmail() == null && user.getPhone() == null) {
            return handleError(model, session, "Invalid email or phone number");
        }

        // Check Email Existence
        if (user.getEmail() != null) {
            if (userService.checkExistMail(user.getEmail())) {
                return handleError(model, session, "Email already exists!");
            }
            if (!userService.checkEmailDomain(user.getEmail())) {
                return handleError(model, session, "Email domain is not allowed!");
            }
        }

        // Check Phone Number Existence
        if (user.getPhone() != null && userService.checkExistPhoneNumber(user.getPhone())) {
            return handleError(model, session, "Phone number already exists!");
        }

        // Validate Password
        if (!Validate.validPassword(password)) {
            return handleError(model, session, "Password must contain at least 8 characters and have uppercase, lowercase, and a number");
        }


        if (!password.equals(confirmPassword)) {
            return handleError(model, session, "Password and confirm password must be the same");
        }

        // set session to verify
        session.setAttribute("userauthen", user);
        session.setAttribute("href", "verify");
        return "redirect:/verifypage";
    }

    // Helper method to handle errors
    private String handleError(Model model, HttpSession session, String errorMessage) {
        model.addAttribute("errmsg", errorMessage);
        model.addAttribute("fullname", session.getAttribute("r-fullname"));
        model.addAttribute("username", session.getAttribute("r-username"));
        session.removeAttribute("r-fullname");
        session.removeAttribute("r-username");
        return "authentication/register";
    }
}
