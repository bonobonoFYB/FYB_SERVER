package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.Dto.MyClosetDto;
import school.bonobono.fyb.Service.MyClosetService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.constant.Constable;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("main")
public class MyClosetController {
    private final MyClosetService myClosetService;

    // 옷장 조회
    @GetMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<MyClosetDto.readResponse> readMyCloset(HttpServletRequest headerRequest){
        return myClosetService.readMyCloset(headerRequest);
    }

    // 옷 사진 추가 등록
    @PutMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Object> updateImage(
            @RequestParam("file") MultipartFile multipartFile, @RequestParam("id") Long id
    ) throws IOException {
        return myClosetService.updateImage(multipartFile,id);
    }

    // 옷장 추가하기
    @PostMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Object> addMyCloset(
            @RequestBody final MyClosetDto.addRequest request, HttpServletRequest headerRequest
    ){
        return myClosetService.addMyCloset(request,headerRequest);
    }

    // 옷장 삭제
    @DeleteMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable deleteMyCloset(
            @RequestBody final MyClosetDto.deleteRequest request, HttpServletRequest headerRequest
    ){
        return myClosetService.deleteCloset(request,headerRequest);
    }

    // 옷장 업데이트
    @PatchMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable deleteMyCloset(
            @RequestBody final MyClosetDto.readResponse request, HttpServletRequest headerRequest
    ){
     return myClosetService.updateCloset(request,headerRequest);
    }
}
