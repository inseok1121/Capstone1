package com.android.jakchang.cameraapp;

import android.app.Activity;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;


public class KakaoSDKAdapter extends KakaoAdapter {

    /**
     * 로그인을 위해 Session을 생성하기 위해 필요한 옵션을 얻기위한 abstract class.
     * 기본 설정은 KakaoAdapter에 정의되어있으며, 설정 변경이 필요한 경우 상속해서 사용
     */
    @Override
    public ISessionConfig getSessionConfig() {
        return new ISessionConfig() {
            @Override
            public AuthType[] getAuthTypes() {
                return new AuthType[] {AuthType.KAKAO_TALK};
            }
            //1.KAKAO_TALK :  kakaotalk으로 login을 하고 싶을때 지정.
            //2.KAKAO_STORY : kakaostory으로 login을 하고 싶을때 지정.
            //3.KAKAO_ACCOUNT :  웹뷰 Dialog를 통해 카카오 계정연결을 제공하고 싶을경우 지정.
            //4.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN : 카카오톡으로만 로그인을 유도하고 싶으면서 계정이 없을때 계정생성을 위한
            //버튼도 같이 제공을 하고 싶다면 지정.KAKAO_TALK과 중복 지정불가.
            //5.KAKAO_LOGIN_ALL : 모든 로그인방식을 사용하고 싶을때 지정.

            @Override
            public boolean isUsingWebviewTimer() {
                return false;
            }


            public boolean isSecureMode() {
                return false;
            }

            @Override
            public ApprovalType getApprovalType() {
                return ApprovalType.INDIVIDUAL;
            }

            @Override
            public boolean isSaveFormData() {
                return true;
            }

        };
    }

    //Application이 가지고있는 정보를 얻기위한 interface.
    @Override
    public IApplicationConfig getApplicationConfig() {
        return new IApplicationConfig() {
            //@Override
            public Activity getTopActivity() {
                return GlobalApplication.getCurrentActivity();
            }


            @Override
            public Context getApplicationContext() {
                return GlobalApplication.getGlobalApplicationContext();
            }
        };
    }
}



