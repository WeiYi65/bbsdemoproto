//package com.coderbbs.bbsdemo.config;
//
//import com.coderbbs.bbsdemo.util.CommunityConstant;
//import com.coderbbs.bbsdemo.util.CommunityUtil;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.access.AccessDeniedHandler;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//
//
//@Configuration
//public class SecurityConfig implements CommunityConstant {
//
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer(){
//        return (web)->web.ignoring().requestMatchers("/resources/**");
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//        http.authorizeHttpRequests((authz) ->
//                authz.requestMatchers("/user/setting", "/user/upload", "/discuss/add",
//                "/comment/add/**", "/letter/**", "/notice/**", "/like", "/follow", "/unfollow")
//                        .hasRole(AUTHORITY_USER)
//                        .anyRequest().permitAll())
//                .exceptionHandling((exceptionHandling) ->
//                        exceptionHandling.authenticationEntryPoint(new AuthenticationEntryPoint() {
//                            @Override
//                            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//                                String requestWith = request.getHeader("x-requested-with");
//                                if ("XMLHttpRequest".equals(requestWith)){
//                                    //未登录
//                                    response.setContentType("application/plain;charset=utf-8");
//                                    PrintWriter writer = response.getWriter();
//                                    writer.write(CommunityUtil.getJSONString(403, "You have to login first. "));
//                                }else {
//                                    response.sendRedirect(request.getContextPath()+"/login");
//                                }
//                            }
//                        }).accessDeniedHandler(new AccessDeniedHandler() {
//                            @Override
//                            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//                                //权限不足
//                                String requestWith = request.getHeader("x-requested-with");
//                                if ("XMLHttpRequest".equals(requestWith)){
//                                    response.setContentType("application/plain;charset=utf-8");
//                                    PrintWriter writer = response.getWriter();
//                                    writer.write(CommunityUtil.getJSONString(403, "You need higher authority to access this page."));
//                                }else {
//                                    response.sendRedirect(request.getContextPath()+"/denied");
//                                }
//                            }
//                        })
//                );

//        http.authorizeRequests()
//                .requestMatchers("/user/setting",
//                        "/user/upload",
//                        "/discuss/add",
//                        "/comment/add/**",
//                        "/letter/**",
//                        "/notice/**",
//                        "/like",
//                        "/follow",
//                        "/unfollow")
//                .hasAnyAuthority(AUTHORITY_USER,
//                        AUTHORITY_ADMIN,
//                        AUTHORITY_MODERATOR)
//                .requestMatchers("/discuss/top",
//                        "/discuss/wonderful")
//                .hasAnyAuthority(AUTHORITY_MODERATOR)
//                .requestMatchers("/discuss/delete")
//                .hasAnyAuthority(AUTHORITY_ADMIN)
//                .anyRequest().permitAll();

        //权限不够的处理办法
//        http.exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
//            //未登录
//            @Override
//            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//                String requestWith = request.getHeader("x-requested-with");
//                if ("XMLHttpRequest".equals(requestWith)){
//                    response.setContentType("application/plain;charset=utf-8");
//                    PrintWriter writer = response.getWriter();
//                    writer.write(CommunityUtil.getJSONString(403, "You have to login first. "));
//                }else {
//                    response.sendRedirect(request.getContextPath()+"/login");
//                }
//            }
//        }).accessDeniedHandler(new AccessDeniedHandler() {
//            //权限不足
//            @Override
//            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//                String requestWith = request.getHeader("x-requested-with");
//                if ("XMLHttpRequest".equals(requestWith)){
//                    response.setContentType("application/plain;charset=utf-8");
//                    PrintWriter writer = response.getWriter();
//                    writer.write(CommunityUtil.getJSONString(403, "You need higher authority to access this page."));
//                    writer.write(CommunityUtil.getJSONString(0));
//                }else {
//                    response.sendRedirect(request.getContextPath()+"/denied");
//                }
//            }
//        });
//
//        //阻止security底层自带的logout拦截，执行自己的退出代码
//        http.logout((logout)->logout.logoutUrl("/securitylogout"));
//
//        return http.build();
//
//    }
//
//
//}
