package com.Unicor_Ads_2.Unicor_Ads_2.demo.correos.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailFileDTO {

    private String[] toUser;
    private String subject;
    private String message;
    private MultipartFile file;
}