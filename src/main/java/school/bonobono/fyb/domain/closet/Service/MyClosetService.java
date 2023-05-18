package school.bonobono.fyb.domain.closet.Service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.domain.closet.Dto.ClosetDto;
import school.bonobono.fyb.domain.closet.Entity.Closet;
import school.bonobono.fyb.domain.closet.Repository.ClosetRepository;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.exception.CustomException;
import school.bonobono.fyb.global.model.Result;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyClosetService {
    private final ClosetRepository closetRepository;
    private final UserRepository userRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // Service
    @Transactional(readOnly = true)
    public List<ClosetDto.DetailDto> readMyCloset(FybUser user) {
        FybUser updateUser = userRepository.findById(user.getId()).orElse(user);
        List<Closet> closets = updateUser.getClosets();
        readMyClosetValidate(closets);
        return closets.stream().map(ClosetDto.DetailDto::response).toList();
    }

    @Transactional
    public ClosetDto.DetailDto addMyCloset(ClosetDto.SaveDto request, FybUser user) {
        addMyClosetValidate(request);
        Closet closet = closetRepository.save(
                Closet.builder()
                        .user(user)
                        .productName(request.getProductName())
                        .productNotes(request.getProductNotes())
                        .productKind(request.getProductKind())
                        .build()
        );
        return ClosetDto.DetailDto.response(closet);
    }

    @Transactional
    public ClosetDto.DetailDto deleteCloset(ClosetDto.DeleteDto request) {
        if (request.getId() == null) {
            throw new CustomException(Result.MY_CLOSET_ID_IS_NULL);
        }
        Closet closet = getCloset(request.getId());
        closetRepository.delete(closet);
        return ClosetDto.DetailDto.response(closet);
    }

    @Transactional
    public ClosetDto.DetailDto updateCloset(ClosetDto.UpdateDto request) {

        updateValidate(request);

        Closet closet = getCloset(request.getId());
        closet.updateCloset(request.getProductName(), request.getProductKind(), request.getProductNotes());

        return ClosetDto.DetailDto.response(closet);
    }

    @Transactional
    public ClosetDto.DetailDto updateImage(MultipartFile multipartFile, Long id) {
        UUID uuid = UUID.randomUUID();
        String imageName = "closet/" + uuid;
        uploadImage(multipartFile, imageName);

        Closet closet = getCloset(id);
        closet.updateImagePath(amazonS3Client.getUrl(bucket, imageName).toString());

        return ClosetDto.DetailDto.response(closet);
    }

    // Validation 및 Method 단순화
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

    private static void updateValidate(ClosetDto.UpdateDto request) {
        if (request.getProductName() == null) {
            throw new CustomException(Result.MY_CLOSET_PNAME_IS_NULL);
        }

        if (request.getProductKind() == null) {
            throw new CustomException(Result.MY_CLOSET_PKIND_IS_NULL);
        }

    }

    private Closet getCloset(Long id) {
        return closetRepository.findById(id).orElseThrow(
                () -> new CustomException(Result.NOT_FOUND_CLOSET)
        );
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
