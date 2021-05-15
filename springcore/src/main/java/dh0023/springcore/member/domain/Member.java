package dh0023.springcore.member.domain;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Member {

    private Long id;
    private String name;
    private Grade grade;

}
