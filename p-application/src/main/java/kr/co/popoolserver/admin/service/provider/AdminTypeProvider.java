package kr.co.popoolserver.admin.service.provider;

import kr.co.popoolserver.admin.service.AdminCommonService;
import kr.co.popoolserver.enums.AdminType;
import kr.co.popoolserver.error.exception.BusinessLogicException;
import kr.co.popoolserver.error.model.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTypeProvider {

    private final List<AdminCommonService> adminCommonServiceList;

    public AdminCommonService getAdminService(AdminType adminType){
        AdminCommonService adminCommonService = adminCommonServiceList.stream()
                .filter(service -> service.canHandle(adminType))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.NOT_SERVICE));
        return adminCommonService;
    }
}
