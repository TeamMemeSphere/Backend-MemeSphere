package com.memesphere.domain.user.converter;

import com.memesphere.domain.user.dto.request.SignUpRequest;
import com.memesphere.domain.user.dto.response.EmailResponse;
import com.memesphere.domain.user.dto.response.GoogleUserInfoResponse;
import com.memesphere.domain.user.entity.SocialType;
import com.memesphere.domain.user.entity.User;
import com.memesphere.domain.user.dto.response.KakaoUserInfoResponse;
import com.memesphere.domain.user.entity.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class UserConverter {

    // 카카오 로그인 유저
    public static User toKakaoUser(KakaoUserInfoResponse kakaoUserInfoResponse) {
        return User.builder()
                .loginId(kakaoUserInfoResponse.getId())
                .nickname(kakaoUserInfoResponse.getKakaoAccount().getProfile().nickName)
                .email(kakaoUserInfoResponse.getKakaoAccount().email)
                .profileImage(kakaoUserInfoResponse.getKakaoAccount().getProfile().profileImageUrl)
                .socialType(SocialType.KAKAO)
                .userRole(UserRole.USER)
                .build();
    }

    // 구글 로그인 유저
    public static User toGoogleUser(GoogleUserInfoResponse googleUserInfoResponse) {
        return User.builder()
                .loginId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                .nickname(googleUserInfoResponse.getName())
                .email(googleUserInfoResponse.getEmail())
                .profileImage(googleUserInfoResponse.getPicture())
                .socialType(SocialType.GOOGLE)
                .userRole(UserRole.USER)
                .build();
    }

    // 일반 로그인 유저
    public static User toAuthUser(SignUpRequest signUpRequest, PasswordEncoder passwordEncoder) {
        return User.builder()
                .loginId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                .nickname(signUpRequest.getNickname())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .socialType(SocialType.GENERAL)
                .userRole(UserRole.USER)
                .profileImage(signUpRequest.getProfileImage())
                .build();
    }

    // 비밀번호 찾기 이메일
    public static EmailResponse toEmailResponse(String tmpPassword, String memberEmail, String title, String fromAddress) {
        return EmailResponse.builder()
                .toAddress(memberEmail)
                .title(title)
                .message(tmpPassword)
                .fromAddress(fromAddress)
                .build();
    }
}

