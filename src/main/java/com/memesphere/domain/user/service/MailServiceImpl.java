package com.memesphere.domain.user.service;

import com.memesphere.domain.user.converter.UserConverter;
import com.memesphere.domain.user.dto.response.EmailResponse;
import com.memesphere.domain.user.entity.User;
import com.memesphere.domain.user.repository.UserRepository;
import com.memesphere.global.apipayload.code.status.ErrorStatus;
import com.memesphere.global.apipayload.exception.GeneralException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final TemplateEngine templateEngine;

    private static final String title = "MemeSphere 임시 비밀번호 안내 이메일입니다.";
    private static final String fromAddress = "memesphere01@gmail.com";

    public EmailResponse createMail(String tmpPassword, String memberEmail) {
        User existingUser = userRepository.findByEmail(memberEmail).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        String password = existingUser.getPassword();

        if (password == null) {
            throw new GeneralException(ErrorStatus.SOCIAL_LOGIN_NOT_ALLOWED);
        }

        return UserConverter.toEmailResponse(tmpPassword, memberEmail, title, fromAddress);
    }

    public void sendMail(EmailResponse email) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // true로 설정하여 HTML 지원

            Context context = new Context();
            context.setVariable("tmpPassword", email.getMessage());
            String message = templateEngine.process("mail", context);

            helper.setTo(email.getToAddress());
            helper.setSubject(email.getTitle());
            helper.setText(message, true); // HTML 형식으로 설정
            helper.setFrom(email.getFromAddress());
            helper.setReplyTo(email.getFromAddress());

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new GeneralException(ErrorStatus.EMAIL_SEND_FAILED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
