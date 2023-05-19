package school.bonobono.fyb.global.aws.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.global.exception.CustomException;
import school.bonobono.fyb.global.model.Result;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadProfileImage(MultipartFile multipartFile, String email) {
        String profileImageName = "profile/" + email;
        ObjectMetadata objMeta = new ObjectMetadata();
        try {
            objMeta.setContentLength(multipartFile.getInputStream().available());
            amazonS3Client.putObject(bucket, profileImageName, multipartFile.getInputStream(), objMeta);
        } catch (IOException e) {
            throw new CustomException(Result.FAIL);
        }
        return amazonS3Client.getUrl(bucket, profileImageName).toString();
    }

    public String uploadClosetImage(MultipartFile multipartFile) {
        UUID uuid = UUID.randomUUID();
        String closetImageName = "closet" + uuid;
        ObjectMetadata objMeta = new ObjectMetadata();
        try {
            objMeta.setContentLength(multipartFile.getInputStream().available());
            amazonS3Client.putObject(bucket, closetImageName, multipartFile.getInputStream(), objMeta);
        } catch (IOException e) {
            throw new CustomException(Result.IMAGE_UPLOAD_FAIL);
        }
        return amazonS3Client.getUrl(bucket, closetImageName).toString();
    }
}
