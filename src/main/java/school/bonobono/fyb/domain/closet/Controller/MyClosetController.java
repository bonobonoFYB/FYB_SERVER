package school.bonobono.fyb.domain.closet.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.domain.closet.Dto.MyClosetDto;
import school.bonobono.fyb.global.Model.StatusTrue;
import school.bonobono.fyb.domain.closet.Service.MyClosetService;

import java.io.IOException;
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
    public List<MyClosetDto.readResponse> readMyCloset(
    ) {
        return myClosetService.readMyCloset();
    }

    // 옷 사진 추가 등록
    @PutMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Object> updateImage(
            @RequestParam("file") MultipartFile multipartFile, @RequestParam("id") Long id
    ) throws IOException {
        return myClosetService.updateImage(multipartFile, id);
    }

    // 옷장 추가하기
    @PostMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Object> addMyCloset(
            @RequestBody final MyClosetDto.addRequest request
    ) {
        return myClosetService.addMyCloset(request);
    }

    // 옷장 삭제
    @DeleteMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StatusTrue> deleteMyCloset(
            @RequestBody final MyClosetDto.deleteRequest request
    ) {
        return myClosetService.deleteCloset(request);
    }

    // 옷장 업데이트
    @PatchMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StatusTrue> updateMyCloset(
            @RequestBody final MyClosetDto.readResponse request
    ) {
        return myClosetService.updateCloset(request);
    }
}
