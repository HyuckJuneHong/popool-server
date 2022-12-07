package kr.co.popoolserver.career.domain.service;

import kr.co.popoolserver.career.domain.dto.CareerDto;
import kr.co.popoolserver.career.domain.entity.CareerEntity;
import kr.co.popoolserver.career.repository.CareerRepository;
import kr.co.popoolserver.common.infra.error.exception.BusinessLogicException;
import kr.co.popoolserver.common.infra.error.model.ErrorCode;
import kr.co.popoolserver.common.infra.interceptor.UserThreadLocal;
import kr.co.popoolserver.user.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerService {

    private final CareerRepository careerRepository;

    /**
     * 이력서 생성
     * @param create
     */
    @Transactional
    public void createCareer(CareerDto.CREATE create){
        UserEntity userEntity = UserThreadLocal.get();
        CareerEntity careerEntity = CareerEntity.of(create, userEntity);
        careerRepository.save(careerEntity);
    }

    /**
     * 본인의 모든 이력서 조회
     * @return
     */
    public List<CareerDto.READ> getAllCareers(){
        UserEntity userEntity = UserThreadLocal.get();
        List<CareerEntity> careerEntities = careerRepository.findByUserEntity(userEntity);
        return CareerEntity.of(careerEntities);
    }

    /**
     * 본인 이력서 중 하나 조회
     * @return
     */
    public CareerDto.READ getCareer(Long id){
        UserEntity userEntity = UserThreadLocal.get();
        CareerEntity careerEntity = careerRepository.findByIdAndUserEntity(id, userEntity)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.WRONG_CAREER));
        return CareerEntity.of(careerEntity);
    }

    /**
     * 이력서 정보 변경 서비스
     * @param update
     */
    @Transactional
    public void updateCareer(CareerDto.UPDATE update){
        UserEntity userEntity = UserThreadLocal.get();
        CareerEntity careerEntity = careerRepository.findByIdAndUserEntity(update.getId(), userEntity)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.WRONG_CAREER));

        careerEntity.updateCareer(update);
    }
}
