package kr.co.popoolserver.consumer.service.user;

import kr.co.popoolserver.consumer.security.UserThreadLocal;
import kr.co.popoolserver.dtos.request.CreateUsers;
import kr.co.popoolserver.dtos.response.ResponseUsers;
import kr.co.popoolserver.entity.user.UserEntity;
import kr.co.popoolserver.entity.user.model.PhoneNumber;
import kr.co.popoolserver.enums.UserType;
import kr.co.popoolserver.error.exception.BusinessLogicException;
import kr.co.popoolserver.error.exception.DuplicatedException;
import kr.co.popoolserver.error.model.ErrorCode;
import kr.co.popoolserver.provider.JwtProvider;
import kr.co.popoolserver.repository.user.UserRepository;
import kr.co.popoolserver.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserCommonService {

    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    private final RedisService redisService;

    private final long REFRESH_EXPIRE = 1000*60*60*24*7;

    /**
     * create user
     * @param createUser : create user info
     * @exception DuplicatedException : ID, Phone, Email Duplicated
     * @exception BusinessLogicException : PW Check
     */
    @Transactional
    public void createUser(CreateUsers.CREATE_USER createUser) {
        isIdentity(createUser.getIdentity());
        isPhoneNumber(createUser.getPhoneNumber());
        isEmail(createUser.getEmail());

        checkPassword(createUser.getPassword(), createUser.getCheckPassword());

        final UserEntity userEntity = UserEntity.of(createUser, passwordEncoder);
        userRepository.save(userEntity);
    }

    /**
     * login
     * @param login : ID, PW
     * @return AccessToken, RefreshToken
     * @exception BusinessLogicException
     */
    @Override
    public ResponseUsers.TOKEN login(CreateUsers.LOGIN login) {
        final UserEntity userEntity = userRepository.findByIdentity(login.getIdentity())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.WRONG_IDENTITY));

        checkEncodePassword(login.getPassword(), userEntity.getPassword(), passwordEncoder);
        checkDelete(userEntity.getDeyYN());

        String[] tokens = generateToken(userEntity);
        redisService.createData(userEntity.getIdentity(), tokens[1], REFRESH_EXPIRE);

        return ResponseUsers.TOKEN.builder()
                .accessToken(tokens[0])
                .refreshToken(tokens[1])
                .build();
    }

    /**
     * Token Create
     * @param userEntity : login user
     * @return : accessToken, refreshToken
     */
    private String[] generateToken(UserEntity userEntity){
        String accessToken = jwtProvider.createAccessToken(userEntity.getIdentity(), userEntity.getUserRole(), userEntity.getName());
        String refreshToken = jwtProvider.createRefreshToken(userEntity.getIdentity(), userEntity.getUserRole(), userEntity.getName());

        return new String[]{accessToken, refreshToken};
    }

    /**
     * 본인 회원 정보 조회
     * @return ResponseUsers.READ_USER :  user info
     */
    public ResponseUsers.READ_USER getUser() {
        final UserEntity userEntity = UserThreadLocal.get();
        checkDelete(userEntity.getDeyYN());

        return UserEntity.of(userEntity);
    }

    /**
     * 본인 세부정보 조회
     * @return ResponseUsers.READ_DETAIL : address, phoneNumber, email
     */
    @Override
    public ResponseUsers.READ_DETAIL getUserDetail() {
        UserEntity userEntity = UserThreadLocal.get();
        checkDelete(userEntity.getDeyYN());

        return ResponseUsers.READ_DETAIL.builder()
                .address(userEntity.getAddress())
                .phoneNumber(userEntity.getPhoneNumber())
                .email(userEntity.getEmail())
                .build();
    }
//
//    /**
//     * 본인 기본 정보 수정 (이름, 성별, 생년월일)
//     * @param update
//     */
//    @Transactional
//    public void updateUser(UserDto.UPDATE update) {
//        UserEntity userEntity = UserThreadLocal.get();
//        checkDelete(userEntity.getDeyYN());
//        userEntity.updateInfo(update);
//        userRepository.save(userEntity);
//    }
//
//    /**
//     * 본인 비밀번호 수정
//     * @param password
//     */
//    @Override
//    @Transactional
//    public void updatePassword(UserCommonDto.UPDATE_PASSWORD password) {
//        UserEntity userEntity = UserThreadLocal.get();
//        checkDelete(userEntity.getDeyYN());
//        checkEncodePassword(password.getOriginalPassword(), userEntity.getPassword());
//        checkPassword(password.getNewPassword(), password.getNewCheckPassword());
//        userEntity.updatePassword(passwordEncoder.encode(password.getNewPassword()));
//        userRepository.save(userEntity);
//    }
//ㅇ
//    /**
//     * Email update service
//     * @param email
//     */
//    @Override
//    @Transactional
//    public void updateEmail(UserCommonDto.UPDATE_EMAIL email) {
//        UserEntity userEntity = UserThreadLocal.get();
//        checkDelete(userEntity.getDeyYN());
//        checkEncodePassword(email.getOriginalPassword(), userEntity.getPassword());
//        checkEmail(email.getEmail());
//        userEntity.updateEmail(email.getEmail());
//        userRepository.save(userEntity);
//    }
//
//    /**
//     * Phone update service
//     * @param phone
//     */
//    @Override
//    @Transactional
//    public void updatePhone(UserCommonDto.UPDATE_PHONE phone) {
//        UserEntity userEntity = UserThreadLocal.get();
//        checkDelete(userEntity.getDeyYN());
//        checkEncodePassword(phone.getOriginalPassword(), userEntity.getPassword());
//        checkPhoneNumber(phone.getNewPhoneNumber());
//        userEntity.updatePhone(new PhoneNumber(phone.getNewPhoneNumber()));
//        userRepository.save(userEntity);
//    }
//
//    /**
//     * Address update service
//     * @param address
//     */
//    @Override
//    @Transactional
//    public void updateAddress(UserCommonDto.UPDATE_ADDRESS address) {
//        UserEntity userEntity = UserThreadLocal.get();
//        checkDelete(userEntity.getDeyYN());
//        checkEncodePassword(address.getOriginalPassword(), userEntity.getPassword());
//        userEntity.updateAddress(address);
//        userRepository.save(userEntity);
//    }
//

//    /**
//     * 회원 탈퇴
//     * @param delete
//     */
//    @Transactional
//    public void deleteUser(UserDto.DELETE delete) {
//        UserEntity userEntity = UserThreadLocal.get();
//        checkDelete(userEntity.getDeyYN());
//        checkEncodePassword(delete.getOriginalPassword(), userEntity.getPassword());
//        userEntity.deleted();
//        userRepository.save(userEntity);
//    }
//
//    /**
//     * 탈퇴 회원 복구
//     * @param reCreate
//     */
//    @Transactional
//    public void reCreateUser(UserDto.RE_CREATE reCreate) {
//        UserEntity userEntity = userRepository.findByIdentity(reCreate.getIdentity())
//                .orElseThrow(() -> new BusinessLogicException(ErrorCode.WRONG_IDENTITY));
//        checkReCreate(userEntity.getDeyYN());
//        checkEncodePassword(reCreate.getOriginalPassword(), userEntity.getPassword());
//        userEntity.reCreated();
//        userRepository.save(userEntity);
//    }
//
//    /**
//     * Redis에 저장된 RefreshToken 삭제
//     * @param identity
//     */
//    @Override
//    public void deleteRefreshToken(String identity){
//        redisService.deleteData(identity);
//    }


//
//    /**
//     * reCreate Check Service
//     * @param delYN
//     */
//    @Override
//    public void checkReCreate(String delYN) {
//        if(delYN.equals("N")) throw new BadRequestException("탈퇴되지 않은 회원입니다.");
//    }

    /**
     * ID duplicated check
     * @param identity : user id
     */
    @Override
    public void isIdentity(String identity) {
        if(userRepository.existsByIdentity(identity)) {
            throw new DuplicatedException(ErrorCode.DUPLICATED_ID);
        }
    }

    /**
     * Phone duplicated check
     * @param phoneNumber : user phone number
     */
    @Override
    public void isPhoneNumber(String phoneNumber) {
        if(userRepository.existsByPhoneNumber(new PhoneNumber(phoneNumber))) {
            throw new DuplicatedException(ErrorCode.DUPLICATED_PHONE);
        }
    }

    /**
     * Email duplicated check
     * @param email : user email
     */
    @Override
    public void isEmail(String email) {
        if(userRepository.existsByEmail(email)) {
            throw new DuplicatedException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    @Override
    public Boolean canHandle(UserType userType) {
        return userType.equals(UserType.USER);
    }
}
