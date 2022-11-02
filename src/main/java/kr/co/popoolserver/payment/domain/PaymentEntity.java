package kr.co.popoolserver.payment.domain;

import kr.co.popoolserver.common.domain.BaseEntity;
import kr.co.popoolserver.common.domain.enums.ProductType;
import kr.co.popoolserver.corporate.domain.CorporateEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_payment")
@Getter
@AttributeOverride(name = "id", column = @Column(name = "payment_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEntity extends BaseEntity {

    @Column(name = "price")
    private int price;

    @Column(name = "payment_date")
    private LocalDateTime localDateTime;

    @Column(name = "product_type")
    @Enumerated(value = EnumType.STRING)
    private ProductType productType;

    @OneToOne
    @JoinColumn(name = "corporate_id")
    private CorporateEntity corporateEntity;
}
