package kr.co.popoolserver.consumer.service.user;

import kr.co.popoolserver.consumer.security.CorporateThreadLocal;
import kr.co.popoolserver.entity.user.CorporateEntity;
import kr.co.popoolserver.entity.user.dto.CorporateDto;
import kr.co.popoolserver.entity.user.dto.UserCommonDto;
import kr.co.popoolserver.entity.user.model.PhoneNumber;
import kr.co.popoolserver.enums.UserServiceName;
import kr.co.popoolserver.error.exception.BadRequestException;
import kr.co.popoolserver.error.exception.BusinessLogicException;
import kr.co.popoolserver.error.exception.DuplicatedException;
import kr.co.popoolserver.error.model.ErrorCode;
import kr.co.popoolserver.provider.JwtProvider;
import kr.co.popoolserver.repository.user.CorporateRepository;
import kr.co.popoolserver.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CorporateService implements UserCommonService {

    private final CorporateRepository corporateRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    private final long REFRESH_EXPIRE = 1000*60*60*24*7;

    /**
     * login service
     * @param login : ID, PW
     * @return AccessToken, RefreshToken
     */
    @Override
    public UserCommonDto.TOKEN login(UserCommonDto.LOGIN login) {
        CorporateEntity corporateEntity = corporateRepository.findByIdentity(login.getIdentity())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.WRONG_IDENTITY));
        checkEncodePassword(login.getPassword(), corporateEntity.getPassword());
        checkDelete(corporateEntity.getDeyYN());

        String[] tokens = generateToken(corporateEntity);
        redisService.createData(corporateEntity.getIdentity(), tokens[1], REFRESH_EXPIRE);

        return UserCommonDto.TOKEN.builder()
                .accessToken(tokens[0])
                .refreshToken(tokens[1])
                .build();
    }

    /**
     * Token Create
     * @param corporateEntity
     * @return
     */
    private String[] generateToken(CorporateEntity corporateEntity){
        String accessToken = jwtProvider.createAccessToken(corporateEntity.getIdentity(), corporateEntity.getUserRole(), corporateEntity.getName());
        String refreshToken = jwtProvider.createRefreshToken(corporateEntity.getIdentity(), corporateEntity.getUserRole(), corporateEntity.getName());
        return new String[]{accessToken, refreshToken};
    }

    /**
     * signUp Service
     * @param create : corporate info
     * @exception DuplicatedException : ID, Phone Duplicated
     * @exception BusinessLogicException : PW Check
     */
    @Transactional
    public void signUp(CorporateDto.CREATE create) {
        checkIdentity(create.getIdentity());
        checkPassword(create.getPassword(), create.getCheckPassword());
        checkPhoneNumber(create.getBusinessPhoneNumber());

        final CorporateEntity corporateEntity = CorporateEntity.of(create, passwordEncoder);
        corporateRepository.save(corporateEntity);
    }

    /**
     * ?????? ?????? ?????? ?????? (??????, ????????? ??????, ????????? ???, ?????? ???)
     * @param update
     */
    @Transactional
    public void updateCorporate(CorporateDto.UPDATE update) {
        CorporateEntity corporateEntity = CorporateThreadLocal.get();
        checkDelete(corporateEntity.getDeyYN());
        corporateEntity.updateInfo(update);
        corporateRepository.save(corporateEntity);
    }

    /**
     * ?????? ???????????? ??????
     * @param password
     */
    @Override
    @Transactional
    public void updatePassword(UserCommonDto.UPDATE_PASSWORD password) {
        CorporateEntity corporateEntity = CorporateThreadLocal.get();
        checkDelete(corporateEntity.getDeyYN());
        checkEncodePassword(password.getOriginalPassword(), corporateEntity.getPassword());
        checkPassword(password.getNewPassword(), password.getNewCheckPassword());
        corporateEntity.updatePassword(passwordEncoder.encode(password.getNewPassword()));
        corporateRepository.save(corporateEntity);
    }

    /**
     * Email update service
     * @param email
     */
    @Override
    @Transactional
    public void updateEmail(UserCommonDto.UPDATE_EMAIL email) {
        CorporateEntity corporateEntity = CorporateThreadLocal.get();
        checkDelete(corporateEntity.getDeyYN());
        checkEncodePassword(email.getOriginalPassword(), corporateEntity.getPassword());
        checkEmail(email.getEmail());
        corporateEntity.updateEmail(email.getEmail());
        corporateRepository.save(corporateEntity);
    }

    /**
     * Phone update service
     * @param phone
     */
    @Override
    @Transactional
    public void updatePhone(UserCommonDto.UPDATE_PHONE phone) {
        CorporateEntity corporateEntity = CorporateThreadLocal.get();
        checkDelete(corporateEntity.getDeyYN());
        checkEncodePassword(phone.getOriginalPassword(), corporateEntity.getPassword());
        checkPhoneNumber(phone.getNewPhoneNumber());
        corporateEntity.updatePhone(new PhoneNumber(phone.getNewPhoneNumber()));
        corporateRepository.save(corporateEntity);
    }

    /**
     * Address update service
     * @param address
     */
    @Override
    @Transactional
    public void updateAddress(UserCommonDto.UPDATE_ADDRESS address) {
        CorporateEntity corporateEntity = CorporateThreadLocal.get();
        checkDelete(corporateEntity.getDeyYN());
        checkEncodePassword(address.getOriginalPassword(), corporateEntity.getPassword());
        corporateEntity.updateAddress(address);
        corporateRepository.save(corporateEntity);
    }

    /**
     * ?????? ?????? ?????? ??????
     * @return : UserGetDto.READ
     */
    public CorporateDto.READ getCorporate() {
        CorporateEntity corporateEntity = CorporateThreadLocal.get();
        checkDelete(corporateEntity.getDeyYN());
        return CorporateEntity.of(corporateEntity);
    }

    /**
     * ?????? ?????? ??????
     * @return READ_ADDRESS
     */
    @Override
    public UserCommonDto.READ_ADDRESS getAddress() {
        CorporateEntity corporateEntity = CorporateThreadLocal.get();
        checkDelete(corporateEntity.getDeyYN());
        return UserCommonDto.READ_ADDRESS.builder()
                .address(corporateEntity.getBusinessAddress())
                .build();
    }

    /**
     * ?????? ?????? ??????
     * @return READ_EMAIL
     */
    @Override
    public UserCommonDto.READ_EMAIL getEmail() {
        CorporateEntity corporateEntity = CorporateThreadLocal.get();
        checkDelete(corporateEntity.getDeyYN());
        return UserCommonDto.READ_EMAIL.builder()
                .email(corporateEntity.getBusinessEmail())
                .build();
    }

    /**
     * ?????? ?????? ??????
     * @return READ_PHONE
     */
    @Override
    public UserCommonDto.READ_PHONE getPhone() {
        CorporateEntity corporateEntity = CorporateThreadLocal.get();
        checkDelete(corporateEntity.getDeyYN());
        return UserCommonDto.READ_PHONE.builder()
                .phoneNumber(corporateEntity.getBusinessPhoneNumber())
                .build();
    }

    /**
     * ?????? ??????
     * @param delete
     */
    @Transactional
    public void deleteCorporate(CorporateDto.DELETE delete) {
        CorporateEntity corporateEntity = CorporateThreadLocal.get();
        checkDelete(corporateEntity.getDeyYN());
        checkEncodePassword(delete.getOriginalPassword(), corporateEntity.getPassword());
        corporateEntity.deleted();
        corporateRepository.save(corporateEntity);
    }

    /**
     * ?????? ?????? ??????
     * @param reCreate
     */
    @Transactional
    public void reCreateCorporate(CorporateDto.RE_CREATE reCreate) {
        CorporateEntity corporateEntity = corporateRepository.findByIdentity(reCreate.getIdentity())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.WRONG_IDENTITY));
        checkReCreate(corporateEntity.getDeyYN());
        checkEncodePassword(reCreate.getOriginalPassword(), corporateEntity.getPassword());
        corporateEntity.reCreated();
        corporateRepository.save(corporateEntity);
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
        if(corporateRepository.existsByIdentity(identity)) throw new DuplicatedException(ErrorCode.DUPLICATED_ID);
    }

    /**
     * Phone duplicated check
     * @param phoneNumber
     */
    @Override
    public void checkPhoneNumber(String phoneNumber) {
        if(corporateRepository.existsByBusinessPhoneNumber(new PhoneNumber(phoneNumber))) throw new DuplicatedException(ErrorCode.DUPLICATED_PHONE);
    }

    /**
     * Email duplicated check
     * @param email
     */
    @Override
    public void checkEmail(String email) {
        if(corporateRepository.existsByBusinessEmail(email)) throw new DuplicatedException(ErrorCode.DUPLICATED_EMAIL);
    }

    /**
     * PW check
     * @param password : use pw
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
        if(delYN.equals("N")) throw new BadRequestException("???????????? ?????? ?????? ?????? ?????????.");
    }

    @Override
    public Boolean canHandle(UserServiceName userServiceName) {
        return userServiceName.equals(UserServiceName.CORPORATE);
    }
}
