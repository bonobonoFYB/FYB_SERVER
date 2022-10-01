package school.bonobono.fyb.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.Dto.MyClosetDto;
import school.bonobono.fyb.Dto.TokenInfoResponseDto;
import school.bonobono.fyb.Entity.MyCloset;
import school.bonobono.fyb.Exception.CustomException;
import school.bonobono.fyb.Model.StatusTrue;
import school.bonobono.fyb.Repository.MyClosetRepository;
import school.bonobono.fyb.Repository.TokenRepository;
import school.bonobono.fyb.Repository.UserRepository;
import school.bonobono.fyb.Util.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.constant.Constable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static school.bonobono.fyb.Exception.CustomErrorCode.*;
import static school.bonobono.fyb.Model.Model.AUTHORIZATION_HEADER;
import static school.bonobono.fyb.Model.StatusTrue.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyClosetService {
    private final MyClosetRepository myClosetRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    @Value("${app.upload.closet.dir}")
    private String uploadDir;

    // Validation 및 단순화
    private static void readMyClosetValidate(List<MyClosetDto.readResponse> list) {
        if (list.isEmpty()) {
            throw new CustomException(MY_CLOSET_EMPTY);
        }
    }

    private static void addMyClosetValidate(MyClosetDto.addRequest request) {
        if (request.getPname() == null) {
            throw new CustomException(MY_CLOSET_PNAME_IS_NULL);
        }

        if (request.getPkind() == null) {
            throw new CustomException(MY_CLOSET_PKIND_IS_NULL);
        }
    }

    private static void updateValidate(MyClosetDto.readResponse request) {
        if (request.getId() == null) {
            throw new CustomException(MY_CLOSET_PNAME_IS_NULL);
        }

        if (request.getPname() == null) {
            throw new CustomException(MY_CLOSET_PNAME_IS_NULL);
        }

        if (request.getPkind() == null) {
            throw new CustomException(MY_CLOSET_PKIND_IS_NULL);
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

    private void tokenCredEntialsValidate(HttpServletRequest request) {
        tokenRepository
                .findById(request.getHeader(AUTHORIZATION_HEADER))
                .orElseThrow(
                        () -> new CustomException(JWT_CREDENTIALS_STATUS_FALSE)
                );
    }

    // Service

    @Transactional
    public List<MyClosetDto.readResponse> readMyCloset(HttpServletRequest headerRequest) {
        tokenCredEntialsValidate(headerRequest);

        List<MyClosetDto.readResponse> list = myClosetRepository
                .findByUid(getTokenInfo().getId())
                .stream()
                .map(MyClosetDto.readResponse::Response).toList();

        readMyClosetValidate(list);

        return list;
    }

    @Transactional
    public List<Object> addMyCloset(MyClosetDto.addRequest request, HttpServletRequest headerRequest) {
        tokenCredEntialsValidate(headerRequest);

        addMyClosetValidate(request);

        myClosetRepository.save(
                MyCloset.builder()
                        .uid(getTokenInfo().getId())
                        .pkind(request.getPkind())
                        .pname(request.getPname())
                        .pnotes(request.getPnotes())
                        .build()
        );

        MyCloset myCloset = myClosetRepository.findByPname(request.getPname()).orElseThrow(
                NullPointerException::new
        );

        HashMap<String, Long> response = new HashMap<>();
        response.put("id", myCloset.getId());

        List<Object> list = new ArrayList<>();
        list.add(response);
        list.add(MY_CLOSET_ADD_STATUS_TRUE);

        return list;
    }

    @Transactional
    public Constable deleteCloset(MyClosetDto.deleteRequest request, HttpServletRequest headerRequest) {
        tokenCredEntialsValidate(headerRequest);

        if (request.getId() == null) {
            throw new CustomException(MY_CLOSET_ID_IS_NULL);
        }

        myClosetRepository.deleteById(request.getId());

        return MY_CLOSET_DELETE_STATUS_TRUE;
    }

    @Transactional
    public Constable updateCloset(MyClosetDto.readResponse request, HttpServletRequest headerRequest) {

        updateValidate(request);

        myClosetRepository.save(
                MyCloset.builder()
                        .id(request.getId())
                        .uid(getTokenInfo().getId())
                        .pkind(request.getPkind())
                        .pnotes(request.getPnotes())
                        .pname(request.getPname())
                        .closetImagePath(getTokenInfo().getProfileImagePath())
                        .build()
        );

        return MY_CLOSET_UPDATE_STATUS_TRUE;
    }

    public ResponseEntity<Object> updateImage(MultipartFile multipartFile, Long id) {
        String ext = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        Path copyOfLocation = Paths.get(uploadDir + File.separator + getTokenInfo().getName() + getTokenInfo().getCreateAt() + ext);

        MyCloset myCloset = myClosetRepository.findById(id).orElseThrow(
                NullPointerException::new
        );

        myClosetRepository.save(
                MyCloset.builder()
                        .id(id)
                        .uid(myCloset.getUid())
                        .pkind(myCloset.getPkind())
                        .pnotes(myCloset.getPnotes())
                        .pname(myCloset.getPname())
                        .closetImagePath(copyOfLocation.toString())
                        .build()
        );

        try {
            Files.copy(multipartFile.getInputStream(), copyOfLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new CustomException(IMAGE_UPLOAD_FAIL);
        }
        return new ResponseEntity<>(MY_CLOSET_IMAGE_UPLOAD_TRUE, HttpStatus.OK);
    }
}
