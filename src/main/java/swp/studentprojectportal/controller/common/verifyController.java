package swp.studentprojectportal.controller.common;

import jakarta.servlet.http.HttpSession;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import swp.studentprojectportal.model.User;
import swp.studentprojectportal.services.servicesimpl.EmailService;
import swp.studentprojectportal.services.servicesimpl.RegisterService;
import swp.studentprojectportal.services.servicesimpl.SettingService;
import swp.studentprojectportal.services.servicesimpl.UserService;
import swp.studentprojectportal.utils.Utility;

@Controller
public class verifyController {
    @Autowired
    int userRoleId;
    @Autowired
    UserService userService;
    @Autowired
    EmailService emailservice;
    @Autowired
    SettingService settingService;
    @Autowired
    RegisterService registerService;

    @GetMapping("/verifypage")
    public String verifyPage(Model model, HttpSession session, WebRequest webRequest) {
        User user = (User) session.getAttribute("userauthen");
        String token = RandomString.make(30); // genarate token

        // Check isverifyemail
        boolean verifyMail = (boolean) session.getAttribute("verifyMail");

        user.setToken(token);
        user.setSetting(settingService.findById(userRoleId));
        userService.saveUser(user);

        // get href
        String href = (String) session.getAttribute("href");
        session.removeAttribute("href");
        String token_sender = Utility.getSiteURL() + "/" + href + "?key=" + token;

        // if user register by email address
        if (user.getEmail() != null && verifyMail == true) {
            emailservice.sendEmail(user.getFullName(), user.getEmail(), token_sender);
            model.addAttribute("email", user.getEmail());
            session.removeAttribute("user");
            return "verifyEmail";
        }

        // if user register by phone number
        if (user.getPhone() != null && verifyMail == false) {
            model.addAttribute("phone", user.getPhone());
            model.addAttribute("token", token_sender);
            session.removeAttribute("user");
            return "verifyPhone";
        }
        session.removeAttribute("user");
        return "redirect:/error";
    }

    @GetMapping("/verify")
    public String registerMail(Model model, HttpSession session, @RequestParam("key") String token) {
        User user = registerService.verifyToken(token);
        if (user != null) {
            session.removeAttribute("user");
            return "verifySuccess";
        }
        return "register";
    }
}
