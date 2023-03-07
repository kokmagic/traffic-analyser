package sia.trafficanalyser.controllers;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import sia.trafficanalyser.repository.models.User;
import sia.trafficanalyser.payload.request.ForgotPasswordRequest;
import sia.trafficanalyser.payload.request.ResetPasswordRequest;
import sia.trafficanalyser.payload.response.MessageResponse;
import sia.trafficanalyser.security.services.UserDetailsServiceImpl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@CrossOrigin
@RestController
@RequestMapping("/api/password")
public class ResetPasswordController {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @PostMapping("/forgot")
    public ResponseEntity<?> processForgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        String username = forgotPasswordRequest.getUsername();
        String token = RandomString.make(30);
        try {
            userDetailsService.updateResetPasswordToken(token, username);
            String resetPasswordLink = "http://localhost:8081" + "/reset_password/" + token;
            sendEmail(username, resetPasswordLink);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("We have sent a reset password link to your email. Please check."));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (UnsupportedEncodingException | MessagingException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error while sending email"));
        }
    }

    public void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("GASupport@support.com", "Gleb and Andrew support");
        helper.setTo(recipientEmail);

        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset or change your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @PostMapping("/reset")
    public ResponseEntity<?> processResetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();
        String password = resetPasswordRequest.getNewPassword();

        User user = userDetailsService.getByResetPasswordToken(token);

        if (user == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Invalid token."));
        } else {
            userDetailsService.updatePassword(user, password);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Password restored successfully"));
        }
    }
}
