package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;
import ru.hogwarts.school.repositories.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.Pageable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;


@Service
@Transactional
public class AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        logger.info("Invoked uploadAvatar for studentId = {}", studentId);
        Student student = studentRepository.getReferenceById(studentId);

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(avatarFile.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        logger.debug("Saving avatar to file path: {}", filePath);
        try (
                InputStream is = avatarFile.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
            logger.info("Avatar file saved successfully for studentId = {}", studentId);
        }

        Avatar avatar = findAvatar(studentId);
        avatar.setStudent(student);
        avatar.setFilepath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(generateDataForDB(filePath));
        logger.debug("Saving avatar entity to database for studentId = {}", studentId);
        avatarRepository.save(avatar);
    }

    public Avatar findAvatar(Long studentId) {
        logger.debug("Invoked findAvatar for studentId = {}", studentId);
        return avatarRepository.findByStudentId(studentId)
                .orElseGet(() -> {
                    logger.warn("No avatar found for studentId = {} — returning new Avatar instance", studentId);
                    return new Avatar();
                });
    }

    private byte[] generateDataForDB(Path filePath) throws IOException {
        logger.debug("Generating preview image for DB from file: {}", filePath.getFileName());
        try (
                InputStream is = Files.newInputStream(filePath);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            BufferedImage image = ImageIO.read(bis);
            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics2D = preview.createGraphics();
            graphics2D.drawImage(image, 0, 0, 100, height, null);
            graphics2D.dispose(); // Very important to dispose graphics

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            logger.debug("Preview image generated and converted to byte array");
            return baos.toByteArray();
        }
    }

    private String getExtension(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        logger.debug("Extracted file extension: {}", ext);
        return ext;
    }

    public List<Avatar> getAvatarsPaginated(int page, int size) {
        logger.info("Invoked getAvatarsPaginated with page = {}, size = {}", page, size);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return avatarRepository.findAll(pageRequest).getContent();
    }
}
