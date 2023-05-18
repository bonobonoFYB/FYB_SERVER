package school.bonobono.fyb.domain.user.Service;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.bonobono.fyb.domain.user.Dto.UserDto;
import school.bonobono.fyb.global.exception.CustomException;
import school.bonobono.fyb.global.model.Result;

import java.util.HashMap;

@Service
@Slf4j
public class SmsService {
    @Value("${coolsms.apiKey}")
    private String smsKey;

    @Value("${coolsms.secretKey}")
    private String smsSecretKey;

    public Boolean sendMessage(String phoneNumber, String randNumber) {
        Message coolSMS = new Message(smsKey, smsSecretKey);

        HashMap<String, String> sendInformation = new HashMap<>();
        sendInformation.put("to", phoneNumber);
        sendInformation.put("from", "010-4345-4377");
        sendInformation.put("type", "SMS");
        sendInformation.put("text", "FYB 휴대폰인증 인증번호는" + "[ " + randNumber + " ]" + "입니다.");
        sendInformation.put("app_version", "test app 1.2");

        try {
            coolSMS.send(sendInformation);
        } catch (CoolsmsException e) {
            throw new CustomException(Result.FAIL);
        }

        return true;
    }
}
