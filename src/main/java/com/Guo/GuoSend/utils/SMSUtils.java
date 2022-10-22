package com.Guo.GuoSend.utils;


import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 短信发送工具类
 */
@Service
public class SMSUtils {
    //region 阿里云短信接口实例
    /**
     // * 发送短信
     // *
     // * @param signName     签名
     // * @param templateCode 模板
     // * @param phoneNumbers 手机号
     // * @param param        参数
     // */
    //@SuppressWarnings({"all"})
    //public static void sendMessageByaliyun(String signName, String templateCode, String phoneNumbers, String param) {
    //    DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "", "");
    //    IAcsClient client = new DefaultAcsClient(profile);
    //
    //    SendSmsRequest request = new SendSmsRequest();
    //    request.setSysRegionId("cn-hangzhou");
    //    request.setPhoneNumbers(phoneNumbers);
    //    request.setSignName(signName);
    //    request.setTemplateCode(templateCode);
    //    request.setTemplateParam("{\"code\":\"" + param + "\"}");
    //    try {
    //        SendSmsResponse response = client.getAcsResponse(request);
    //        System.out.println("短信发送成功");
    //    } catch (ClientException e) {
    //        e.printStackTrace();
    //    }
    //}
    //endregion

    /**
     * 腾讯短信发送
     *
     * @param signName     签名
     * @param templateCode 模板
     * @param phoneNumbers 手机号
     * @param param        参数
     */
    public static void sendMessage(String signName, String templateCode, String phoneNumbers, String param) {
        try {
            Credential cred = new Credential("AKID998zc097ygASCnhQ3Vt68zdEdpnaARZV", "lKj9QZbbUEqHUVHo7B9tPsSuBHE2vlV5");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = {phoneNumbers};
            req.setPhoneNumberSet(phoneNumberSet1);

            req.setSmsSdkAppId("1400750303");
            req.setSignName(signName);
            req.setTemplateId(templateCode);

            String[] templateParamSet1 = {param};
            req.setTemplateParamSet(templateParamSet1);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }
}
