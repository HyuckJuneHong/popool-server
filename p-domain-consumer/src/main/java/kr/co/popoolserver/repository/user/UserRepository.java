package kr.co.popoolserver.repository.user;

import kr.co.popoolserver.entity.user.UserEntity;
import kr.co.popoolserver.entity.user.model.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{

    //find
    Optional<UserEntity> findByIdentity(String identity);

    //exists
    boolean existsByIdentity(String identity);
    boolean existsByPhoneNumber(PhoneNumber phoneNumber);
    boolean existsByEmail(String email);
}