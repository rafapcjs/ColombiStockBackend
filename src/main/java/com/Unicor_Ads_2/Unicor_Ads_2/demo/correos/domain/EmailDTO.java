package com.Unicor_Ads_2.Unicor_Ads_2.demo.correos.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailDTO {

    private String[] toUser;
    private String subject;
    private String message;
}
