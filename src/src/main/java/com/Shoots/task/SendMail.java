package com.Shoots.task;
import com.Shoots.domain.MailVO;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import java.io.File;

@Component
public class SendMail {
    @Value("${my.sendfile}")
    private String sendfile;
    private JavaMailSender mailSender;


    public SendMail(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private static final Logger logger = LoggerFactory.getLogger(SendMail.class);

    public void sendMail(MailVO vo) {
        /*
        Spring Framework의 MimeMessagePreparator 인터페이스를 사용하여 이메일 메시지를 구성하고 전송하는 방법.
        MimeMessagePreparator를 구현한 익명 클래스를 생성함.
        MimeMessagePreparator는 이메일 메시지를 사전에 준비하는 인터페이스임.
        */

        MimeMessagePreparator mp = new MimeMessagePreparator(){
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {

                /*MimeMessageHelper를 사용하여 MimeMessage를 보다 쉽게 작성할 수 있도록 해줌.
                두번째 인자 true는 멀티파트 메시지를 사용하겠단 의미.
                즉, 메시지 본문에 이미지를 인라인으로 포함하거나 파일을 첨부 가능함. 또한 HTML을 지원하는 경우 HTML로 표시됨.
                * */
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setFrom(vo.getFrom()); //이메일 발신자 설정
                helper.setTo(vo.getTo()); //이메일 수신자 설정
                helper.setSubject(vo.getSubject()); //이메일 제목 설정

                /*
                1. 문자로만 전송하는 경우
                두번째 인자는 html을 사용하겠다는 뜻.
                helper.setText(vo.getContent(), ture);

                2. 이미지를 내장하여 이메일을 보내는 경우 <img>태그에 'cid'속성을 사용해 이미지를 내장함.
                cid(content id)
                이미지 파일은 addInline()메서드를 사용해 MimeMessage에 첨부함
                */

                String content = vo.getText() + "<br><br><img src = 'cid:Home'>";
                helper.setText(content, true);

                FileSystemResource file = new FileSystemResource(new File(sendfile));
                helper.addInline("Home", file);
                //addInline메서드의 첫번째 매개변수에는 cid(content id)를 지정함.

                /*
                3. 파일을 첨부해서 이메일을 보낼시, addAttachment() 메서드를 사용하여 MimeMessage에 파일을 첨부함.
                첫번째 인자 : 첨부될 파일의 이름임.
                두번째 인자 : 첨부 파일
                */

//                helper.addAttachment("셀커크렉스", file);  //파일 첨부하는곳.
            }
        };
        mailSender.send(mp); //메일 전송.
        logger.info("메일 전송완료.");
    }
}
