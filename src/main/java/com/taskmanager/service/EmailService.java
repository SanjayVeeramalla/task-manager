// EmailService
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.name:Task Manager}")
    private String appName;
    
    @Value("${app.url:http://localhost:8080}")
    private String appUrl;
    
    public void sendPasswordResetEmail(String toEmail, String resetUrl, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(toEmail);
            helper.setSubject(appName + " - Password Reset Request");
            
            String htmlContent = buildPasswordResetEmailContent(resetUrl);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    private String buildPasswordResetEmailContent(String resetUrl) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { text-align: center; margin-bottom: 30px; }" +
                ".logo { font-size: 24px; font-weight: bold; color: #6366f1; }" +
                ".button { display: inline-block; padding: 12px 24px; background-color: #6366f1; color: white; text-decoration: none; border-radius: 6px; margin: 20px 0; }" +
                ".footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #666; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<div class='logo'>🗂️ " + appName + "</div>" +
                "</div>" +
                "<h2>Password Reset Request</h2>" +
                "<p>You have requested to reset your password. Click the button below to create a new password:</p>" +
                "<div style='text-align: center;'>" +
                "<a href='" + resetUrl + "' class='button'>Reset Password</a>" +
                "</div>" +
                "<p>If the button doesn't work, copy and paste this link into your browser:</p>" +
                "<p style='word-break: break-all; color: #6366f1;'>" + resetUrl + "</p>" +
                "<p><strong>This link will expire in 1 hour.</strong></p>" +
                "<p>If you didn't request this password reset, please ignore this email.</p>" +
                "<div class='footer'>" +
                "<p>This is an automated email from " + appName + ". Please do not reply to this email.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
