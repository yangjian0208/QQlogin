package cn.yangjian.qqlogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements IUiListener{

    private Tencent mTencent;
    private String mAppid = "222222";//分配的key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSdk();
    }
	//初始化
    private void initSdk() {
        mTencent = Tencent.createInstance(mAppid, this);
    }

    public void login(View view){
        Toast.makeText(this, "登陆", Toast.LENGTH_SHORT).show();
        //登录的核心代码,all代表授权范围，
        mTencent.login(this, "all", MainActivity.this);
    }
    //登陆  核心代码
    //登陆成功
    @Override
    public void onComplete(Object jsonObject) {
        showLog(jsonObject.toString());
        showToast(jsonObject.toString());
        //获取用户信息
        //1.存储token
        try {
            JSONObject json = new JSONObject(jsonObject.toString());
            String token = json.optString(Constants.PARAM_ACCESS_TOKEN);//正式开放不建议使用getString,因为如果没有key，空指针，
            String expires = json.optString(Constants.PARAM_EXPIRES_IN);//建议使用optString,如果没有key返回默认值0
            String openId = json.optString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
            //2.读取用户信息
            UserInfo mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    showLog("获取用户信息成功："+o.toString());
                }

                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    //登陆错误
    @Override
    public void onError(UiError uiError) {
        showLog("登陆错误");

    }
    //取消登陆
    @Override
    public void onCancel() {
        showToast("取消登陆");






    }
    //回调账户的头像等数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode,resultCode,data,MainActivity.this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showLog(String msg){
        Log.i("result", ""+msg);
    }

    private void showToast(String msg){
        Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
    }
}
