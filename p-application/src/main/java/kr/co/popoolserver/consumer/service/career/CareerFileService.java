package kr.co.popoolserver.consumer.service.career;

import kr.co.popoolserver.entity.career.CareerFileEntity;
import kr.co.popoolserver.repository.career.CareerFileRepository;
import kr.co.popoolserver.dto.S3Dto;
import kr.co.popoolserver.error.exception.BusinessLogicException;
import kr.co.popoolserver.error.model.ErrorCode;
import kr.co.popoolserver.consumer.security.UserThreadLocal;
import kr.co.popoolserver.service.S3Service;
import kr.co.popoolserver.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerFileService {

    private final CareerFileRepository careerFileRepository;
    private final S3Service s3Service;

    /**
     * S3 Image File 저장 & DB Image Meta Data 저장 Service
     * @param multipartFile
     */
    @Transactional
    public void createCareerFile(MultipartFile multipartFile){
        UserEntity userEntity = UserThreadLocal.get();
        checkFile(userEntity);

        S3Dto.CONVERT convert = s3Service.uploadS3(multipartFile, "image");
        careerFileRepository.save(CareerFileEntity.of(convert, userEntity));
    }

    /**
     * Career File S3 Image 다운로드
     * @return
     */
    public S3Dto.DOWNLOAD getCareerFileDownload(){
        UserEntity userEntity = UserThreadLocal.get();
        CareerFileEntity careerFileEntity = careerFileRepository.findByUserEntity(userEntity)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.FAIL_FILE_EMPTY));

        return s3Service.downloadS3(careerFileEntity.getFileName());
    }

    /**
     * S3 Image File 삭제 & DB Image Meta Data 삭제 Service
     */
    @Transactional
    public void deleteCareerFile(){
        UserEntity userEntity = UserThreadLocal.get();
        CareerFileEntity careerFileEntity = careerFileRepository.findByUserEntity(userEntity)
                        .orElseThrow(() -> new BusinessLogicException(ErrorCode.FAIL_FILE_EMPTY));

        s3Service.deleteS3(careerFileEntity.getFileName());
        careerFileRepository.delete(careerFileEntity);
    }

    /**
     * 기존 이미지 데이터가 있다면 삭제 후 재업로드
     * @param userEntity
     */
    private void checkFile(UserEntity userEntity){
        if(careerFileRepository.existsByUserEntity(userEntity)) deleteCareerFile();
    }
}
