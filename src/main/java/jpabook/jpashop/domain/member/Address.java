package jpabook.jpashop.domain.member;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter // Setter 사용X
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    /**
     * 생성자에서 값을 모두 초기화하여 변경 불가능한 클래스를 만들어야 함
     */
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
