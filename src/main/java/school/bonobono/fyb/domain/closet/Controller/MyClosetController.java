package school.bonobono.fyb.domain.closet.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.domain.closet.Dto.ClosetDto;
import school.bonobono.fyb.domain.closet.Service.MyClosetService;
import school.bonobono.fyb.global.Model.CustomResponseEntity;
import school.bonobono.fyb.global.Model.StatusTrue;

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
    public CustomResponseEntity<List<ClosetDto.DetailDto>> readMyCloset(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return CustomResponseEntity.success(myClosetService.readMyCloset(userDetails));
    }

    // 옷 사진 추가 등록
    @PutMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<ClosetDto.DetailDto> updateImage(
            @RequestParam("file") MultipartFile multipartFile, @RequestParam("id") Long id
    ) {
        return CustomResponseEntity.success(myClosetService.updateImage(multipartFile, id));
    }

    // 옷장 추가하기
    @PostMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<ClosetDto.SaveDto> addMyCloset(
            @RequestBody final ClosetDto.SaveDto request,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return CustomResponseEntity.success(myClosetService.addMyCloset(request, userDetails));
    }

    // 옷장 삭제
    @DeleteMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<ClosetDto.DetailDto> deleteMyCloset(
            @RequestBody final ClosetDto.DeleteDto request
    ) {
        return CustomResponseEntity.success(myClosetService.deleteCloset(request));
    }

    // 옷장 업데이트
    @PatchMapping("closet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StatusTrue> updateMyCloset(
            @RequestBody final ClosetDto.readResponse request
    ) {
        return myClosetService.updateCloset(request);
    }
}
