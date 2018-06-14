package com.android.jakchang.cameraapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class LoginActivity extends AppCompatActivity {
    SessionCallback callback;
    public static final String NICKNAME = "nick";
    public static final String USER_ID = "id";
    public static final String PROFILE_IMG = "img";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                //로그아웃 성공 후 하고싶은 내용 코딩
            }
        });

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //간편로그인시 호출 ,없으면 간편로그인시 로그인 성공화면으로 넘어가지 않음
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    //SessionCallback 클래스 구현
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        finish();
                    } else {
                        //재 로그인 구현 필요
                        //redirectLoginActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인 성공 시 로그인한 사용자의 일련번호, 닉네임, 이미지url 리턴
                    //사용자 캐시 정보 업데이트
                    if (userProfile != null) {
                        userProfile.saveUserToCache();
                    }
                    Logger.e("succeeded to update user profile",userProfile,"\n");
                    //////////////////

                    final String nickName = userProfile.getNickname();//닉네임
                    final long userID = userProfile.getId();//사용자 고유번호
                    final String pImage = userProfile.getProfileImagePath();//사용자 프로필 경로
                    Log.e("UserProfile", userProfile.toString());//전체 정보 출력

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(NICKNAME,nickName);
                    intent.putExtra(USER_ID, String.valueOf(userID));
                    intent.putExtra(PROFILE_IMG,pImage);
                    startActivity(intent);
                    finish();

                }
            });

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }
}

