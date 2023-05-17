package school.bonobono.fyb.domain.user.Service;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.bonobono.fyb.domain.user.Dto.UserDto;

import java.util.HashMap;

@Service
public class SmsService {
    @Value("${coolsms.apiKey}")
    private String smsKey;

    @Value("${coolsms.secretKey}")
    private String smsSecretKey;

    public void sendMessage(String phoneNumber, String randNumber) throws CoolsmsException {
        Message coolSMS = new Message(smsKey, smsSecretKey);
        coolSMS.send(getInformation(phoneNumber, randNumber));
    }

    private static HashMap<String, String> getInformation(String  phoneNumber, String randNumber) {
        HashMap<String, String> sendInformation = new HashMap<>();
        sendInformation.put("to", phoneNumber);
        sendInformation.put("from", "010-4345-4377");
        sendInformation.put("type", "SMS");
        sendInformation.put("text", "FYB 휴대폰인증 인증번호는" + "[ " + randNumber + " ]" + "입니다.");
        sendInformation.put("app_version", "test app 1.2");
        return sendInformation;
    }
}
