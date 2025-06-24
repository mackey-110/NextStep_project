package web.mvc.domain;

import lombok.Getter;

/**
 * 구독 타입 열거형
 */
@Getter
public enum SubscriptionType {

    FREE("무료", 0),
    PREMIUM("프리미엄", 19900),
    PRO("프로", 39900);

    private final String description;
    private final int monthlyPrice;

    SubscriptionType(String description, int monthlyPrice) {
        this.description = description;
        this.monthlyPrice = monthlyPrice;
    }
}
