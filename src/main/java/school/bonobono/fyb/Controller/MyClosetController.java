package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.bonobono.fyb.Dto.MyClosetDto;
import school.bonobono.fyb.Service.MyClosetService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/main/closet")
public class MyClosetController {
    private final MyClosetService myClosetService;

    @GetMapping("read")
    public List<MyClosetDto.readResponse> readMyCloset(HttpServletRequest headerRequest){
        return myClosetService.readMyCloset(headerRequest);
    }
}
