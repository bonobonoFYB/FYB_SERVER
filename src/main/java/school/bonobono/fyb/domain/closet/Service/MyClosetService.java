package school.bonobono.fyb.domain.closet.Service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.domain.closet.Dto.ClosetDto;
import school.bonobono.fyb.domain.closet.Entity.Closet;
import school.bonobono.fyb.domain.closet.Repository.MyClosetRepository;
import school.bonobono.fyb.domain.user.Dto.TokenInfoResponseDto;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.Config.Jwt.SecurityUtil;
import school.bonobono.fyb.global.Exception.CustomException;
import school.bonobono.fyb.global.Model.Result;
import school.bonobono.fyb.global.Model.StatusTrue;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static school.bonobono.fyb.global.Model.StatusTrue.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyClosetService {
    private final MyClosetRepository myClosetRepository;
    private final UserRepository userRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // Validation 및 단순화
    private static void readMyClosetValidate(List<Closet> list) {
        if (list.isEmpty()) {
            throw new CustomException(Result.MY_CLOSET_EMPTY);
        }
    }

    private static void addMyClosetValidate(ClosetDto.SaveDto request) {
        if (request.getProductName() == null) {
            throw new CustomException(Result.MY_CLOSET_PNAME_IS_NULL);
        }

        if (request.getProductKind() == null) {
            throw new CustomException(Result.MY_CLOSET_PKIND_IS_NULL);
        }
    }

    private static void updateValidate(ClosetDto.readResponse request) {
        if (request.getId() == null) {
            throw new CustomException(Result.MY_CLOSET_PNAME_IS_NULL);
        }

        if (request.getPname() == null) {
            throw new CustomException(Result.MY_CLOSET_PNAME_IS_NULL);
        }

        if (request.getPkind() == null) {
            throw new CustomException(Result.MY_CLOSET_PKIND_IS_NULL);
        }

    }

    private TokenInfoResponseDto getTokenInfo() {
        return TokenInfoResponseDto.Response(
                Objects.requireNonNull(SecurityUtil.getCurrentUsername()
                        .flatMap(
                                userRepository::findOneWithAuthoritiesByEmail)
                        .orElse(null))
        );
    }

    // Service
    @Transactional
    public List<ClosetDto.DetailDto> readMyCloset(UserDetails userDetails) {
        FybUser user = getUser(userDetails.getUsername());
        List<Closet> closets = user.getClosets();
        readMyClosetValidate(closets);
        return closets.stream().map(ClosetDto.DetailDto::response).toList();
    }

    @Transactional
    public ClosetDto.SaveDto addMyCloset(ClosetDto.SaveDto request, UserDetails userDetails) {
        addMyClosetValidate(request);
        FybUser user = getUser(userDetails.getUsername());
        Closet closet = myClosetRepository.save(
                Closet.builder()
                        .user(user)
                        .productName(request.getProductName())
                        .productNotes(request.getProductNotes())
                        .productKind(request.getProductKind())
                        .build()
        );
        return ClosetDto.SaveDto.response(closet);
    }

    @Transactional
    public ResponseEntity<StatusTrue> deleteCloset(ClosetDto.deleteRequest request) {
        if (request.getId() == null) {
            throw new CustomException(Result.MY_CLOSET_ID_IS_NULL);
        }

        myClosetRepository.deleteById(request.getId());

        return new ResponseEntity<>(MY_CLOSET_DELETE_STATUS_TRUE, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<StatusTrue> updateCloset(ClosetDto.readResponse request) {

        updateValidate(request);

        myClosetRepository.save(
                Closet.builder()
                        .id(request.getId())
                        .uid(getTokenInfo().getId())
                        .pkind(request.getPkind())
                        .pnotes(request.getPnotes())
                        .pname(request.getPname())
                        .closetImagePath(getTokenInfo().getProfileImagePath())
                        .build()
        );

        return new ResponseEntity<>(MY_CLOSET_UPDATE_STATUS_TRUE, HttpStatus.OK);
    }

    public ClosetDto.DetailDto updateImage(MultipartFile multipartFile, Long id) {
        UUID uuid = UUID.randomUUID();
        String imageName = "closet/" + uuid;
        uploadImage(multipartFile, imageName);

        Closet closet = myClosetRepository.findById(id).orElseThrow(
                () -> new CustomException(Result.NOT_FOUND_CLOSET)
        );
        closet.updateImagePath(amazonS3Client.getUrl(bucket, imageName).toString());

        return ClosetDto.DetailDto.response(closet);
    }

    public FybUser getUser(String email) {
        return userRepository.findOneWithAuthoritiesByEmail(email).orElseThrow(
                () -> new CustomException(Result.NOT_FOUND_USER)
        );
    }

    private void uploadImage(MultipartFile multipartFile, String imageName) {
        // String ext = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        ObjectMetadata objMeta = new ObjectMetadata();
        try {
            objMeta.setContentLength(multipartFile.getInputStream().available());
            amazonS3Client.putObject(bucket, imageName, multipartFile.getInputStream(), objMeta);
        } catch (IOException e) {
            throw new CustomException(Result.IMAGE_UPLOAD_FAIL);
        }
    }
}
