package journeybuddy.spring.service.community.post;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3ImageService {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        String fileName = dirName + "/" + generateRandomFilename(multipartFile);
        File uploadFile = multipartFileToFile(multipartFile);
        try {
            amazonS3Client.putObject(bucket, fileName, uploadFile);
            String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();
            log.info("File uploaded to S3: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            throw new IllegalArgumentException("파일 업로드에 실패했습니다.", e);
        } finally {
            if (uploadFile.exists()) {
                uploadFile.delete();
                log.info("Temporary file deleted: {}", uploadFile.getAbsolutePath());
            }
        }
    }

    private File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
    }

    public void delete(String imageUrl) {
        String fileName = extractFileName(imageUrl);
        amazonS3Client.deleteObject(bucket, fileName);
        log.info("File deleted from S3: {}", imageUrl);
    }

    private String extractFileName(String imageUrl) {
        int index = imageUrl.indexOf(".com/");
        if (index == -1) {
            throw new IllegalArgumentException("잘못된 S3 URL 형식입니다.");
        }
        return imageUrl.substring(index + 5);
    }


    // 파일 확장자 체크
    private String validateFileExtension(String originalFilename) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "gif", "jpeg");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException("이미지 형식의 파일이 아닙니다.");
        }
        return fileExtension;
    }

    // 랜덤파일명 생성 (파일명 중복 방지)
    private String generateRandomFilename(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = validateFileExtension(originalFilename);
        String randomFilename = UUID.randomUUID() + "." + fileExtension;
        return randomFilename;
    }

}