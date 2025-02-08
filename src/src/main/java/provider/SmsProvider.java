package provider;


import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class SmsProvider {

    private final DefaultMessageService messageService;

    @Value("${sms.from-number}") String FROM;

    public SmsProvider(
            @Value("${sms.api-key}") String API_KEY,
            @Value("${sms.api-secret-key}") String API_SECRET_KEY,
            @Value("${sms.domain}") String DOMAIN
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, DOMAIN);
    }


    //단일 메시지 전송
    //http://localhost:1000/Shoots/test/send-sms/수신번호 입력
    public boolean sendSms(String to) {
        Message message = new Message();
        message.setFrom(FROM); //발신번호
        message.setTo(to); //수신번호
        message.setText("문자내용");

        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));

        String statusCode = response.getStatusCode();
        boolean result = statusCode.equals("2000");

        return result;
    }


    //여러 메시지 전송 >> match에서 동시에 여러명 보낼때 사용
    //http://localhost:1000/Shoots/test/send-many >> 해당 주소로 가면 바로 메시지 전송됨
    public void sendMany(List<Map<String, Object>> userList) {
        ArrayList<Message> messageList = new ArrayList<>();

        for (Map<String, Object> user : userList) {
            String phoneNumber = (String) user.get("tel");
            String userName = (String) user.get("name");

            String matchTime = (String) user.get("MATCH_TIME");
            String formattedMatchTime = matchTime.substring(0, 5);

            String businessName = (String) user.get("business_name");

            String messageType = (String) user.get("messageType");

            if (phoneNumber == null || phoneNumber.isEmpty()) {
                continue;
            }

            String messageText;
            if ("cancel".equals(messageType)) {
                messageText = userName + "님! " + businessName + " " + formattedMatchTime + " 예정된 매치는 신청 인원 미달로 취소되어 환불이 진행되었습니다. * 문의는 Shoots 페이지 내의 1:1문의를 이용해 주시길 바랍니다 *";
            } else if ("confirm".equals(messageType)) {
                messageText = userName + "님! " + businessName + " " + formattedMatchTime + " 예정된 매치가 확정되었습니다. 즐거운 경기 되세요! (신청 취소 및 환불 불가) * 문의는 Shoots 페이지 내의 1:1문의를 이용해 주시길 바랍니다 *";
            } else {
                continue;
            }

            Message message = new Message();
            message.setFrom(FROM);
            message.setTo(phoneNumber);
            message.setText(messageText);
            messageList.add(message);

            System.out.println("message = " + message.getText());
        }

        try {
            MultipleDetailMessageSentResponse response = this.messageService.send(messageList, false, true);
            System.out.println(response);
        } catch (NurigoMessageNotReceivedException exception) {
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

}
