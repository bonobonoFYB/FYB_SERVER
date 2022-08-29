package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.Dto.MyClosetDto;
import school.bonobono.fyb.Service.MyClosetService;

import javax.servlet.http.HttpServletRequest;
import java.lang.constant.Constable;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/main/closet")
public class MyClosetController {
    private final MyClosetService myClosetService;

    // 옷장 조회
    @GetMapping("read")
    public List<MyClosetDto.readResponse> readMyCloset(HttpServletRequest headerRequest){
        return myClosetService.readMyCloset(headerRequest);
    }

    // 옷장 추가하기
    @PostMapping("add")
    public Constable addMyCloset(
            @RequestBody final MyClosetDto.addRequest request, HttpServletRequest headerRequest
    ){
        return myClosetService.addMyCloset(request,headerRequest);
    }

    // 옷장 삭제
    @PostMapping("delete")
    public Constable deleteMyCloset(
            @RequestParam(name = "id") final Long id, HttpServletRequest headerRequest
    ){
        return myClosetService.deleteCloset(id,headerRequest);
    }
}
