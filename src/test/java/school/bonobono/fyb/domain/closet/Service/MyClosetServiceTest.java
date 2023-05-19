package school.bonobono.fyb.domain.closet.Service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.domain.closet.Dto.ClosetDto;
import school.bonobono.fyb.domain.closet.Entity.Closet;
import school.bonobono.fyb.domain.closet.Repository.ClosetRepository;
import school.bonobono.fyb.domain.user.Entity.Authority;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.domain.user.Service.UserService;
import school.bonobono.fyb.global.aws.service.S3Service;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MyClosetServiceTest {

    @MockBean
    private S3Service s3Service;

    @Autowired
    private MyClosetService myClosetService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClosetRepository closetRepository;

    @DisplayName("유저가 내 옷장에 옷을 저장한다.")
    @Test
    void addMyCloset() {
        // given
        FybUser user = getUserAndSave();
        ClosetDto.SaveDto request = ClosetDto.SaveDto.builder()
                .productName("상품 이름")
                .productNotes("상품 내용")
                .productKind("상의")
                .build();

        // when
        ClosetDto.DetailDto response = myClosetService.addMyCloset(request, user);

        // then
        assertThat(response)
                .extracting("productName", "productNotes", "productKind")
                .contains("상품 이름", "상품 내용", "상의");

        Optional<Closet> closetOptional = closetRepository.findById(response.getId());
        assertThat(closetOptional.isPresent()).isTrue();
        assertThat(closetOptional.get())
                .extracting("productName", "productNotes", "productKind")
                .contains("상품 이름", "상품 내용", "상의");
    }

    @DisplayName("사용자가 자신의 옷장을 조회한다.")
    @Test
    void readMyCloset() {
        // given
        FybUser user = getUserAndSave();

        Closet closet1 = Closet.builder()
                .productName("1")
                .productNotes("1_설명")
                .productKind("상의")
                .closetImagePath("test.png")
                .user(user)
                .build();
        user.getClosets().add(closet1);

        Closet closet2 = Closet.builder()
                .productName("2")
                .productNotes("2_설명")
                .productKind("하의")
                .closetImagePath("test.png")
                .user(user)
                .build();
        user.getClosets().add(closet2);

        Closet closet3 = Closet.builder()
                .productName("3")
                .productNotes("3_설명")
                .productKind("악세사리")
                .closetImagePath("test.png")
                .user(user)
                .build();
        user.getClosets().add(closet3);

        closetRepository.save(closet1);
        closetRepository.save(closet2);
        closetRepository.save(closet3);

        // when
        List<ClosetDto.DetailDto> response = myClosetService.readMyCloset(user);

        // then
        assertThat(response)
                .extracting(ClosetDto.DetailDto::getProductName)
                .containsExactlyInAnyOrder("1", "2", "3");
    }

    @DisplayName("유저가 자신의 옷장안에 이미지를 추가한다.")
    @Test
    void updateImage() {
        FybUser user = getUserAndSave();
        Closet closet = closetRepository.save(Closet.builder()
                .productName("1")
                .productNotes("1_설명")
                .productKind("상의")
                .closetImagePath("test.png")
                .user(user)
                .build()
        );

        // given
        MultipartFile multipartFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Spring Framework".getBytes()
        );

        given(s3Service.uploadClosetImage(any(MultipartFile.class)))
                .willReturn("upload_complete_image");

        // when
        ClosetDto.DetailDto response = myClosetService.updateImage(multipartFile, closet.getId());

        // then
        assertThat(response.getClosetImagePath()).isEqualTo("upload_complete_image");

        Optional<Closet> closetOptional = closetRepository.findById(closet.getId());
        assertThat(closetOptional.isPresent()).isTrue();
        assertThat(closetOptional.get().getClosetImagePath()).isEqualTo("upload_complete_image");
    }

    @DisplayName("유저가 옷장안에 있는 옷을 삭제한다.")
    @Test
    void deleteMyCloset() {
        // given
        FybUser user = getUserAndSave();
        Closet closet = closetRepository.save(Closet.builder()
                .productName("1")
                .productNotes("1_설명")
                .productKind("상의")
                .closetImagePath("test.png")
                .user(user)
                .build()
        );

        ClosetDto.DeleteDto request = ClosetDto.DeleteDto.builder()
                .id(closet.getId())
                .build();

        // when
        ClosetDto.DetailDto response = myClosetService.deleteCloset(request);

        // then
        assertThat(response.getProductName()).isEqualTo("1");

        Optional<Closet> closetOptional = closetRepository.findById(request.getId());
        assertThat(closetOptional.isEmpty()).isTrue();
    }

    @DisplayName("유저가 자신의 옷장안에 있는 옷을 수정한다.")
    @Test
    void updateMyCloset() {
        // given
        FybUser user = getUserAndSave();
        Closet closet = closetRepository.save(Closet.builder()
                .productName("1")
                .productNotes("1_설명")
                .productKind("상의")
                .closetImagePath("test.png")
                .user(user)
                .build()
        );

        ClosetDto.UpdateDto request = ClosetDto.UpdateDto.builder()
                .id(closet.getId())
                .productName("2")
                .productNotes("2_설명")
                .productKind("하의")
                .build();
        // when
        ClosetDto.DetailDto response = myClosetService.updateCloset(request);

        // then
        Optional<Closet> closetOptional = closetRepository.findById(request.getId());
        assertThat(closetOptional.isPresent()).isTrue();
        assertThat(closetOptional.get())
                .extracting("productName","productNotes","productKind")
                .contains("2","2_설명","하의");
    }

    // method
    private Set<Authority> getUserAuthority() {
        return Collections.singleton(Authority.builder()
                .authorityName("ROLE_USER")
                .build());
    }

    private FybUser getUserAndSave() {
        FybUser user = FybUser.builder()
                .email("test@test.com")
                .pw("abc123!")
                .name("테스트 계정")
                .gender('M')
                .height(180)
                .weight(70)
                .age(23)
                .authorities(getUserAuthority())
                .userData("ABC")
                .build();
        return userRepository.save(user);
    }
}