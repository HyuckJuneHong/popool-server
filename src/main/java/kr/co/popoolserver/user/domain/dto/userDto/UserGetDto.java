package kr.co.popoolserver.user.domain.dto.userDto;

import io.swagger.annotations.ApiModelProperty;
import kr.co.popoolserver.common.domain.Address;
import kr.co.popoolserver.common.domain.PhoneNumber;
import kr.co.popoolserver.common.domain.enums.Gender;
import kr.co.popoolserver.common.domain.enums.UserRole;
import kr.co.popoolserver.user.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class UserGetDto {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class READ{
        @ApiModelProperty(example = "홍길동")
        private String name;

        @ApiModelProperty(example = "YYmmDD")
        private String birth;

        @ApiModelProperty(example = "010-xxxx-xxxx")
        private PhoneNumber phoneNumber;

        @ApiModelProperty(example = "MALE or FEMALE")
        private Gender gender;

        @ApiModelProperty(example = "ROLE_USER")
        private UserRole userRole;

        @ApiModelProperty(example = "2022-01-01")
        private LocalDateTime createAt;

        public static UserGetDto.READ of(UserEntity userEntity){
            return READ.builder()
                    .name(userEntity.getName())
                    .birth(userEntity.getBirth())
                    .phoneNumber(userEntity.getPhoneNumber())
                    .gender(userEntity.getGender())
                    .userRole(userEntity.getUserRole())
                    .createAt(userEntity.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ADDRESS{
        private Address address;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class EMAIL{
        private String email;
    }

}
