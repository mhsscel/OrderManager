package com.genesis.studio.sibs.ordermanager.service;

import com.genesis.studio.sibs.ordermanager.model.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LogManager.getLogger(OrderService.class);
    @Autowired
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendOrderCompletedEmail(Order order) {
        logger.info("By Emailing: Your order with ID " + order.getId() + " has been completed.");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(order.getUser().getEmail());
        message.setSubject("Order Completed");
        message.setText("Your order with ID " + order.getId() + " has been completed.");
        mailSender.send(message);
    }
}
