package swp.studentprojectportal.controller.common;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import swp.studentprojectportal.model.User;
import swp.studentprojectportal.services.servicesimpl.RegisterService;
import swp.studentprojectportal.services.servicesimpl.ResetPassword;
import swp.studentprojectportal.services.servicesimpl.UserService;
import swp.studentprojectportal.utility.Validate;

@Controller
public class forgotPasswordController {
    @Autowired
    UserService userService;
    @Autowired
    RegisterService registerService;
    @Autowired
    ResetPassword resetPassword;
    @GetMapping("/forgotPassword")
    public String forgotPasswordPage(HttpSession session) {
        return "forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(WebRequest request, HttpSession session) {
        String username = request.getParameter("username");
        User user = new User();
        if(Validate.validEmail(username)) {user.setEmail(username);}
        if(Validate.validPhoneNumber(username)) {user.setPhone(username);}

        if(user.getEmail() == null && user.getPhone() == null) {
            // set session username and phone number is not valid
            return "redirect:/forgotPassword";
        }

        if(!userService.checkExistMail(user.getEmail()) && !userService.checkEmailDomain(user.getEmail())) {
            // set session username and phone number not exist
            return "redirect:/forgotPassword";
        }

        session.setAttribute("userauthen", user);
        session.setAttribute("href", "reset-password");
        return "redirect:/verifypage";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(HttpSession session,@RequestParam("key") String token) {
        User user = resetPassword.resetPasswordByToken(token);
        if(user != null) {
            session.removeAttribute("user");
            session.setAttribute("user", user);
            return "resetPassword";
        } else {
            return "redirect:/forgotPassword";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(WebRequest request, Model model, HttpSession session) {
        String newPassword = request.getParameter("newPassword");
        String reNewPassword = request.getParameter("reNewPassword");
        User user= (User) session.getAttribute("user");

        // check old password empty
        if(user.getPassword() != "") {
            return "redirect:/forgotPassword";
        }

        // check equal password and re-password
        if(newPassword.equals(reNewPassword)){
            user.setPassword(newPassword);
            session.setAttribute("user", user);
            model.addAttribute("errmsg", "Reset password successfully");
            //save to database
            User u = userService.saveUser(user);
            return "redirect:/forgotPassword";
        } else {
            model.addAttribute("errmsg", "New Password and Re-new Password do not match");
        }

        return "resetPassword";
    }

}
