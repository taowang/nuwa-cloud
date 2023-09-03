package com.study.oauth2.controller;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * @author 公众号：码猿技术专栏
 * @URL：www.java-family.cn
 * 确认授权的页面自定义
 * 参照 WhitelabelApprovalEndpoint 改写，跳转到自定义的视图
 * `@SessionAttributes("authorizationRequest")`:这个一定要标注
 */
@Controller
@SessionAttributes("authorizationRequest")
public class ApprovalController {
    @RequestMapping("/oauth/confirm_access")
    public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
        //从session中获取对应的请求信息
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
        ModelAndView view = new ModelAndView();
        view.setViewName("oauth-grant");
        view.addObject("clientId", authorizationRequest.getClientId());
        view.addObject("scopes", authorizationRequest.getScope());
        return view;
    }
}