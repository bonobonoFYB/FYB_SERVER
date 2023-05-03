package school.bonobono.fyb.domain.user.Entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class FybUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "pw")
    private String pw;

    @Column(name = "name", length = 15)
    private String name;

    @Column(name = "gender")
    private Character gender;

    @Column(name = "height")
    private Integer height;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "age")
    private Integer age;

    private String userData;
    /*
     [체형 유형 설명]
     WA1111 = abcdef 라고 할 때
     a) W/M : 여성/남성
     b) A-E : BMI 구간 (저체중A, 정상B, 과체중C, 비만D, 고도비만E)
     c) 체형 유형 : 상체비만1, 하체비만2, 정상체형3
     d) 골반 사이즈 : 중상1, 중하2
     e) 어깨 사이즈 : 상1, 중2, 하3
     f) 다리 길이 : 중상1, 중하2

     --> 상=높거나 넓음 / 하=낮거나 좁음

     BMI 구간 :
     저체중A = ~18.5
     정상B = 18.5~22.9
     과체중C = 23~24.9
     비만D = 25~29.9
     고도비만E = 30~

     [의류 설명]
     OP1: 주름 있는 숏패딩 / OP2: 주름 없는 숏패딩
     OC1: 6버튼 코트 / OC2: 논버튼 코트 그레이 / OC3: 논버튼 코트 네이비
     OJ1: 자켓
     Oj1: 점퍼
     OK1: 가디건
     TK1: 스트라이프 니트 / TK2: 브이넥 니트 / TK3: 니트조끼
     TS1: 패턴 셔츠 / TS2: 루즈핏 셔츠 / TS3: 슬림핏 셔츠
     TT1: 폴로형 긴팔티셔츠 / TT2: 폴로형 반팔티셔츠 / TT3: 목폴라 긴팔 티셔츠 / TT4: 기본 맨투맨 그레이
     BJ1: 일자핏 청바지 / BJ2: 슬림핏 청바지 / BJ3: 와이드핏 청바지
     BS1: 일자핏 슬랙스 / BS2: 슬립핏 슬랙스 / BS3: 와이드핏 슬랙스
     BH1: 반바지 그린 / BH2: 반바지 블랙
     //W붙은 건 여성 전용 의류
     WTC1: 크롭 셔츠 반팔 / WTC2: 크롭 맨투맨 그레이 / WTC3: 브라탑
     WBB1: 부츠컷 슬랙스 / WBB2: 부츠컷 청바지
     WBH1: 반바지 블랙
     WBS1: A라인 스커트 / WBS2: H라인 스커트 / WBS3: 롱스커트
     */

    @Column(name = "profileImagePath")
    private String profileImagePath;

    @CreatedDate
    @Column(name = "createAt")
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(name = "updateAt")
    private LocalDateTime updateAt;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    public void uploadProfileImage(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public void updateUserInfo(String name, Character gender, Integer height, Integer weight, Integer age) {
        this.name = name;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.age = age;
    }

    public void updatePassword(String password) {
        this.pw = password;
    }
}
