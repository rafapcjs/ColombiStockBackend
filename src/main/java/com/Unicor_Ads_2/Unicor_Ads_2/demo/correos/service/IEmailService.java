package com.Unicor_Ads_2.Unicor_Ads_2.demo.correos.service;

import java.io.File;

public interface IEmailService {
    void sendEmail(String []toUser, String subject, String message);

    void sendEmailWithFile(String []toUser, String subject, String message, File file);
}
