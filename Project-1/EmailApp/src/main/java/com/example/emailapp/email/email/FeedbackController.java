package com.example.emailapp.email.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.ValidationException;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    //@Autowired
    private Email email;

    public FeedbackController(Email email) {
        this.email = email;
    }

    @PostMapping
    public void sendFeedback (@RequestBody Feedback feedback, BindingResult bindingResult) throws ValidationException {
        if(bindingResult.hasErrors()){
            throw new ValidationException("Feedback is not valid");
        }

        //Send Email
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.email.getHost());
        mailSender.setPort(this.email.getPort());
        mailSender.setUsername(this.email.getUsername());
        mailSender.setPassword(this.email.getPassword());

        //Create email instance
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(feedback.getEmail());
        mailMessage.setTo("rc@feedback.com");
        mailMessage.setSubject("New feedback from " + feedback.getName());
        mailMessage.setText(feedback.getFeedback());

        //send Email
        mailSender.send(mailMessage);
    }
}
