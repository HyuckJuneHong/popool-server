package kr.co.popoolserver.persistence.repository;

import kr.co.popoolserver.persistence.entity.CareerEntity;
import kr.co.popoolserver.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerRepository extends JpaRepository<CareerEntity, Long> {

    //find others
    Optional<CareerEntity> findById(Long id);

    //find me
    Optional<CareerEntity> findByIdAndUserEntity(Long id, UserEntity userEntity);
    List<CareerEntity> findByUserEntity(UserEntity userEntity);
}
