集成QQ第三方登录
1.导入open_sdk_r5509.jar包到app/libs中
2.在AndroidManifest中添加权限
3.在AndroidManifest中添加
        
    <activity   //显示授权登录界面
    android:name="com.tencent.tauth.AuthActivity"
    android:launchMode="singleTask"
    android:noHistory="true" >
    <intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="tencent222222" />
    <!-- 100380359 100381104 222222 -->
    </intent-filter>
    </activity>
    


     <!--//辅助显示登陆界面-->
        <activity
            android:name="com.tencent.connect.common.AssistActivity"
            android:configChanges="orientation|keyboardHidden"
            android:screenOrientation="behind"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" />
两个配置都必须有,**
#细节#**如果只添加了AuthActivity没有添加AssistActivity  将不能完成登录


implements IUiListener  实现接口


    private Tencent mTencent;
    private String mAppid = "222222";//QQ分配的keyprivate 
	//初始化
    private void initSdk() {
        mTencent = Tencent.createInstance(mAppid, this);
    }

    public void login(View view){
        Toast.makeText(this, "登陆", Toast.LENGTH_SHORT).show();
        //登录的核心代码,all代表授权范围，
        mTencent.login(this, "all", MainActivity.this);
    }



重写 方法

**1. public void onComplete(Object jsonObject) {}登陆成功
2. public void onError(UiError uiError) {}登陆失败
3. public void onCancel() {}取消登录**
		
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