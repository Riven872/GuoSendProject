package com.Guo.GuoSend.controller;

import com.Guo.GuoSend.common.R;
import com.Guo.GuoSend.entity.User;
import com.Guo.GuoSend.service.UserService;
import com.Guo.GuoSend.utils.SMSUtils;
import com.Guo.GuoSend.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 发送手机短信验证码
     *
     * @param user    页面提交过来的用户信息
     * @param session 页面的session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取用户手机号
        String phone = user.getPhone();

        //手机号不为空时才发送验证码
        if (StringUtils.isNotEmpty(phone)) {
            //生成长度为6位的纯数字验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            //调用腾讯云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("郭郭鸡的小窝公众号", "1572763", phone, code);

            //需要将生成的验证码保存到session中，以备之后的校验
            session.setAttribute(phone, code);
            log.info("验证码为: " + code);

            return R.success("手机验证码发送成功！");
        }

        return R.error("短信发送失败！");
    }

    /**
     * 手机端登录
     *
     * @param map     手机号和验证码key、value形式
     * @param session 页面的session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //session中的验证码
        String codeInSession = session.getAttribute(phone).toString();

        //region 登录成功
        //页面提交的验证码和Session中发送的验证码进行比对
        if (code != null && code.equals(codeInSession)) {
            //如果能够比对成功，则说明登录成功
            //判断当前手机号对应的用户是否为新用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            //如果为新用户则自动完成注册
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        //endregion

        return R.error("登录失败！");
    }

    /**
     * 手机端退出登录
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest httpServletRequest){
        httpServletRequest.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
