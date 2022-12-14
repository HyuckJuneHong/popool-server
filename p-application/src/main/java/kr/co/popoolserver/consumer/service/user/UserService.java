package kr.co.popoolserver.consumer.service.user;

import kr.co.popoolserver.consumer.security.UserThreadLocal;
import kr.co.popoolserver.entity.user.UserEntity;
import kr.co.popoolserver.entity.user.dto.UserCommonDto;
import kr.co.popoolserver.entity.user.dto.UserDto;
import kr.co.popoolserver.entity.user.model.PhoneNumber;
import kr.co.popoolserver.enums.UserServiceName;
import kr.co.popoolserver.error.exception.BadRequestException;
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
     * login service
     * @param login : ID, PW
     * @return AccessToken, RefreshToken
     */
    @Override
    public UserCommonDto.TOKEN login(UserCommonDto.LOGIN login) {
        UserEntity userEntity = userRepository.findByIdentity(login.getIdentity())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.WRONG_IDENTITY));
        checkEncodePassword(login.getPassword(), userEntity.getPassword());
        checkDelete(userEntity.getDeyYN());

        String[] tokens = generateToken(userEntity);
        redisService.createData(userEntity.getIdentity(), tokens[1], REFRESH_EXPIRE);

        return UserCommonDto.TOKEN.builder()
                .accessToken(tokens[0])
                .refreshToken(tokens[1])
                .build();
    }

    /**
     * Token Create
     * @param userEntity
     * @return
     */
    private String[] generateToken(UserEntity userEntity){
        String accessToken = jwtProvider.createAccessToken(userEntity.getIdentity(), userEntity.getUserRole(), userEntity.getName());
        String refreshToken = jwtProvider.createRefreshToken(userEntity.getIdentity(), userEntity.getUserRole(), userEntity.getName());
        return new String[]{accessToken, refreshToken};
    }

    /**
     * signUp Service
     * @param create : user info
     * @exception DuplicatedException : ID, Phone Duplicated
     * @exception BusinessLogicException : PW Check
     */
    @Transactional
    public void signUp(UserDto.CREATE create) {
        checkIdentity(create.getIdentity());
        checkPassword(create.getPassword(), create.getCheckPassword());
        checkPhoneNumber(create.getPhone());

        final UserEntity userEntity = UserEntity.of(create, passwordEncoder);
        userRepository.save(userEntity);
    }

    /**
     * ?????? ?????? ?????? ?????? (??????, ??????, ????????????)
     * @param update
     */
    @Transactional
    public void updateUser(UserDto.UPDATE update) {
        UserEntity userEntity = UserThreadLocal.get();
        checkDelete(userEntity.getDeyYN());
        userEntity.updateInfo(update);
        userRepository.save(userEntity);
    }

    /**
     * ?????? ???????????? ??????
     * @param password
     */
    @Override
    @Transactional
    public void updatePassword(UserCommonDto.UPDATE_PASSWORD password) {
        UserEntity userEntity = UserThreadLocal.get();
        checkDelete(userEntity.getDeyYN());
        checkEncodePassword(password.getOriginalPassword(), userEntity.getPassword());
        checkPassword(password.getNewPassword(), password.getNewCheckPassword());
        userEntity.updatePassword(passwordEncoder.encode(password.getNewPassword()));
        userRepository.save(userEntity);
    }

    /**
     * Email update service
     * @param email
     */
    @Override
    @Transactional
    public void updateEmail(UserCommonDto.UPDATE_EMAIL email) {
        UserEntity userEntity = UserThreadLocal.get();
        checkDelete(userEntity.getDeyYN());
        checkEncodePassword(email.getOriginalPassword(), userEntity.getPassword());
        checkEmail(email.getEmail());
        userEntity.updateEmail(email.getEmail());
        userRepository.save(userEntity);
    }

    /**
     * Phone update service
     * @param phone
     */
    @Override
    @Transactional
    public void updatePhone(UserCommonDto.UPDATE_PHONE phone) {
        UserEntity userEntity = UserThreadLocal.get();
        checkDelete(userEntity.getDeyYN());
        checkEncodePassword(phone.getOriginalPassword(), userEntity.getPassword());
        checkPhoneNumber(phone.getNewPhoneNumber());
        userEntity.updatePhone(new PhoneNumber(phone.getNewPhoneNumber()));
        userRepository.save(userEntity);
    }

    /**
     * Address update service
     * @param address
     */
    @Override
    @Transactional
    public void updateAddress(UserCommonDto.UPDATE_ADDRESS address) {
        UserEntity userEntity = UserThreadLocal.get();
        checkDelete(userEntity.getDeyYN());
        checkEncodePassword(address.getOriginalPassword(), userEntity.getPassword());
        userEntity.updateAddress(address);
        userRepository.save(userEntity);
    }

    /**
     * ?????? ?????? ?????? ??????
     * @return : UserGetDto.READ
     */
    public UserDto.READ getUser() {
        UserEntity userEntity = UserThreadLocal.get();
        checkDelete(userEntity.getDeyYN());
        return UserEntity.of(userEntity);
    }

    /**
     * ?????? ?????? ??????
     * @return READ_ADDRESS
     */
    @Override
    public UserCommonDto.READ_ADDRESS getAddress() {
        UserEntity userEntity = UserThreadLocal.get();
        checkDelete(userEntity.getDeyYN());
        return UserCommonDto.READ_ADDRESS.builder()
                .address(userEntity.getAddress())
                .build();
    }

    /**
     * ?????? ?????? ??????
     * @return READ_EMAIL
     */
    @Override
    public UserCommonDto.READ_EMAIL getEmail() {
        UserEntity userEntity = UserThreadLocal.get();
        checkDelete(userEntity.getDeyYN());
        return UserCommonDto.READ_EMAIL.builder()
                .email(userEntity.getEmail())
                .build();
    }

    /**
     * ?????? ?????? ??????
     * @return READ_PHONE
     */
    @Override
    public UserCommonDto.READ_PHONE getPhone() {
        UserEntity userEntity = UserThreadLocal.get();
        checkDelete(userEntity.getDeyYN());
        return UserCommonDto.READ_PHONE.builder()
                .phoneNumber(userEntity.getPhoneNumber())
                .build();
    }

    /**
     * ?????? ??????
     * @param delete
     */
    @Transactional
    public void deleteUser(UserDto.DELETE delete) {
        UserEntity userEntity = UserThreadLocal.get();
        checkDelete(userEntity.getDeyYN());
        checkEncodePassword(delete.getOriginalPassword(), userEntity.getPassword());
        userEntity.deleted();
        userRepository.save(userEntity);
    }

    /**
     * ?????? ?????? ??????
     * @param reCreate
     */
    @Transactional
    public void reCreateUser(UserDto.RE_CREATE reCreate) {
        UserEntity userEntity = userRepository.findByIdentity(reCreate.getIdentity())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.WRONG_IDENTITY));
        checkReCreate(userEntity.getDeyYN());
        checkEncodePassword(reCreate.getOriginalPassword(), userEntity.getPassword());
        userEntity.reCreated();
        userRepository.save(userEntity);
    }

    /**
     * Redis??? ????????? RefreshToken ??????
     * @param identity
     */
    @Override
    public void deleteRefreshToken(String identity){
        redisService.deleteData(identity);
    }

    /**
     * ID duplicated check
     * @param identity
     */
    @Override
    public void checkIdentity(String identity) {
        if(userRepository.existsByIdentity(identity)) throw new DuplicatedException(ErrorCode.DUPLICATED_ID);
    }

    /**
     * Phone duplicated check
     * @param phoneNumber
     */
    @Override
    public void checkPhoneNumber(String phoneNumber) {
        if(userRepository.existsByPhoneNumber(new PhoneNumber(phoneNumber))) throw new DuplicatedException(ErrorCode.DUPLICATED_PHONE);
    }

    /**
     * Email duplicated check
     * @param email
     */
    @Override
    public void checkEmail(String email) {
        if(userRepository.existsByEmail(email)) throw new DuplicatedException(ErrorCode.DUPLICATED_EMAIL);
    }

    /**
     * PW check
     * @param password : user pw
     * @param checkPassword : check pw
     */
    @Override
    public void checkPassword(String password,
                              String checkPassword) {
        if(!password.equals(checkPassword)) throw new BusinessLogicException(ErrorCode.WRONG_PASSWORD);
    }

    /**
     * Login PW Check
     * @param password
     * @param encodePassword
     */
    @Override
    public void checkEncodePassword(String password,
                                    String encodePassword) {
        if(!passwordEncoder.matches(password, encodePassword)) throw new BusinessLogicException(ErrorCode.WRONG_PASSWORD);
    }

    /**
     * delete Check
     * @param delYN
     */
    @Override
    public void checkDelete(String delYN) {
        if(delYN.equals("Y")) throw new BusinessLogicException(ErrorCode.DELETED_USER);
    }

    /**
     * reCreate Check Service
     * @param delYN
     */
    @Override
    public void checkReCreate(String delYN) {
        if(delYN.equals("N")) throw new BadRequestException("???????????? ?????? ???????????????.");
    }

    @Override
    public Boolean canHandle(UserServiceName userServiceName) {
        return userServiceName.equals(UserServiceName.USER);
    }
}
