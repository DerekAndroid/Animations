package com.example.android.animationsdemo.baidu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.ADPushManager;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushSettings;
import com.example.android.animationsdemo.DKLog;
import com.example.android.animationsdemo.R;
import com.example.android.animationsdemo.Trace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 云推送Demo主Activity。
 * 代码中，注释以Push标注开头的，表示接下来的代码块是Push接口调用示例
 */
public class PushDemoActivity extends Activity implements View.OnClickListener {

    private static final String TAG = PushDemoActivity.class.getSimpleName();
    RelativeLayout mainLayout = null;
    int akBtnId = 0;
    int initBtnId = 0;
    int unbindBtnId = 0;
    int clearLogBtnId = 0;
    private Context mContext;
    //推广
    int ad1BtnId = 0;
    int ad2BtnId = 0;
    int ad3BtnId = 0;
    int ad4BtnId = 0;
    int ad5BtnId = 0;
    int adAllBtnId = 0;
    int feedbackBtnId = 0;
    int adSwitchOnBtnId = 0;
    int adSwitchOffBtnId = 0;
  //  int adLimitBtnId = 0;
    int adMaxCountId = 0;
    private int adStyle = 0;   //推广样式标志位，0表示推送5条推广消息，1~5表示分别推送一条对应的消息
    
    Button initButton = null;
    Button initWithApiKey = null;
    Button unbind = null;
    Button clearLog = null;
    //推广
    Button ad1Btn = null;
    Button ad2Btn = null;
    Button ad3Btn = null;
    Button ad4Btn = null;
    Button ad5Btn = null;
    Button adAllBtn = null;
    Button feedbackBtn = null;
    Button adSwitchOnBtn = null;
    Button adSwitchOffBtn = null;
   // Button adLimitBtn = null;
    EditText adCountText = null;
    private int adCount = 0;
    
    TextView logText = null;
    ScrollView scrollView = null;
    public static int initialCnt = 0;
    private boolean isLogin = false;
    //推广消息推送脚本（单播）
    //push.php脚本是百度图片和链接
    private static String AdSinglePushUrl = "http://115.239.211.213:8005/push_adv/push_api/push.php";
    //实际推送url "http://115.239.211.213:8005/push_adv/push_api/push.php?advertise_style=6&channel_id=3527934059102952224";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushSettings.enableDebugMode(getApplicationContext(), true);
        Utils.logStringCache = Utils.getLogText(getApplicationContext());
        mContext = this;
        
        Resources resource = this.getResources();
        String pkgName = this.getPackageName();

        setContentView(resource.getIdentifier("baidu_demo_main", "layout", pkgName));
        akBtnId = resource.getIdentifier("btn_initAK", "id", pkgName);
        unbindBtnId = resource.getIdentifier("btn_unbindTags", "id", pkgName);
        clearLogBtnId = resource.getIdentifier("btn_clear_log", "id", pkgName);
        //推广推送按钮       
        ad1BtnId = resource.getIdentifier("btn_ad_style_1", "id", pkgName);
        ad2BtnId = resource.getIdentifier("btn_ad_style_2", "id", pkgName);
        ad3BtnId = resource.getIdentifier("btn_ad_style_3", "id", pkgName);
        ad4BtnId = resource.getIdentifier("btn_ad_style_4", "id", pkgName);
        ad5BtnId = resource.getIdentifier("btn_ad_style_5", "id", pkgName); 
        adAllBtnId = resource.getIdentifier("btn_ad_style_all", "id", pkgName); 
        adSwitchOnBtnId = resource.getIdentifier("btn_ad_switch_on", "id", pkgName);
        adSwitchOffBtnId = resource.getIdentifier("btn_ad_switch_off", "id", pkgName);
        //adLimitBtnId = resource.getIdentifier("btn_ad_number_limit", "id", pkgName);
        adMaxCountId = resource.getIdentifier("text_ad_count", "id", pkgName);
        
        initWithApiKey = (Button) findViewById(akBtnId);
        initButton = (Button) findViewById(initBtnId);
        unbind = (Button) findViewById(unbindBtnId);
        clearLog = (Button) findViewById(clearLogBtnId);
        ad1Btn = (Button) findViewById(ad1BtnId);
        ad2Btn = (Button) findViewById(ad2BtnId);
        ad3Btn = (Button) findViewById(ad3BtnId);
        ad4Btn = (Button) findViewById(ad4BtnId);
        ad5Btn = (Button) findViewById(ad5BtnId);
        adAllBtn = (Button) findViewById(adAllBtnId);
        adSwitchOnBtn = (Button) findViewById(adSwitchOnBtnId);
        adSwitchOffBtn = (Button) findViewById(adSwitchOffBtnId);
      //  adLimitBtn = (Button) findViewById(adLimitBtnId);
        adCountText = (EditText) findViewById(adMaxCountId);

        logText = (TextView) findViewById(resource.getIdentifier("text_log",
                "id", pkgName));
        scrollView = (ScrollView) findViewById(resource.getIdentifier(
                "stroll_text", "id", pkgName));

        initWithApiKey.setOnClickListener(this);
        clearLog.setOnClickListener(this);
        unbind.setOnClickListener(this);
        ad1Btn.setOnClickListener(this);
        ad2Btn.setOnClickListener(this);
        ad3Btn.setOnClickListener(this);
        ad4Btn.setOnClickListener(this);
        ad5Btn.setOnClickListener(this);  
        adAllBtn.setOnClickListener(this);  
        adSwitchOnBtn.setOnClickListener(this);
        adSwitchOffBtn.setOnClickListener(this);
      //  adLimitBtn.setOnClickListener(this);

        // Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
        // 这里把apikey存放于manifest文件中，只是一种存放方式，
        // 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,
        // "api_key")
        //！！ 请将AndroidManifest.xml 134行 api_key 字段值修改为自己的 api_key 方可使用 ！！(注：只使用该demo测试请不要更换api_key)
        // ！！ ATTENTION：You need to modify the value of api_key to your own at row 134 in AndroidManifest.xml to use this Demo !!
        // 通过share preference实现的绑定标志开关，如果已经成功绑定，就取消这次绑定
        if (!Utils.hasBind(getApplicationContext())) {
           DKLog.d(TAG, Trace.getCurrentMethod() + "binding..");
           ADPushManager.startWorkForAD(getApplicationContext(),
                   PushConstants.LOGIN_TYPE_API_KEY,
                   Utils.getMetaValue(PushDemoActivity.this, "api_key"));
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == akBtnId) {
            initWithApiKey();
        } else if (v.getId() == clearLogBtnId) {
            Utils.logStringCache = "";
            Utils.setLogText(getApplicationContext(), Utils.logStringCache);
            updateDisplay();
        } else if (v.getId() == unbindBtnId) {
            unBindForApp();
        } else if (v.getId() == ad1BtnId) {        		       	
         	toast("推送一条样式1推广消息!");
            pushAdMessage(1);
        } else if (v.getId() == ad2BtnId) {
        	toast("推送一条样式2推广消息!");
            pushAdMessage(2);
        } else if (v.getId() == ad3BtnId) {
        	toast("推送一条样式3推广消息!");
            pushAdMessage(3);
        } else if (v.getId() == ad4BtnId) {
        	toast("推送一条样式4推广消息!");
            pushAdMessage(4);
        } else if (v.getId() == ad5BtnId) {
        	toast("推送一条样式5推广消息!");
            pushAdMessage(5);
        } else if (v.getId() == adAllBtnId) {
        	toast("分别推送样式1~5的推广消息!");
            pushAdMessage(6);
        }else if (v.getId() == adSwitchOnBtnId) {
            ADPushManager.setPushADMsgEnable(getApplicationContext(), true);
            Toast.makeText(getApplicationContext(), "打开推广功能！", Toast.LENGTH_SHORT).show();
            
        }else if (v.getId() == adSwitchOffBtnId) {
            ADPushManager.setPushADMsgEnable(getApplicationContext(), false);
            Toast.makeText(getApplicationContext(), "关闭推广功能！", Toast.LENGTH_SHORT).show();
        }
    }
    
    /*** 
     * 判断 String 是否是 int 
     *  
     * @param input 
     * @return 
     */  
    public boolean isInteger(String input){
        Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(input);
        return mer.find();  
    }  

 
    private void pushAdMessage(int flag) {
    	//向后台发起请求，请求推送push推广消息  
    	adStyle = flag;    	
    	new Thread(getPushAdMsgThread).start();
    }
    
    protected void toast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    
  //***************************//
  	 private Runnable getPushAdMsgThread = new Runnable() {
  			@Override
  			public void run() {
  		        String api_key = Utils.getMetaValue(PushDemoActivity.this,
  		                "api_key");
  		        //推单播消息必须要channel_id
  		        String channel_id = Utils.getChannelId(getApplicationContext());
  		        String user_id = Utils.getUserId(getApplicationContext());
  		      /*final String strRequestStr = "apikey=" + api_key
                      + "&secretkey=" + secret_key + "&user_id="
                      + user_id + "&channel_id=" + channel_id + "advertise_style"+adStyle;*/
  		      System.out.println("advertise_style="+adStyle+"&channel_id="+channel_id);
  	          String strRequestRet = Utils.sendGet(AdSinglePushUrl,"advertise_style="+adStyle+"&channel_id="+channel_id);
	          System.out.println("---strRequestRet---"+strRequestRet);
	          Log.e(TAG, "requestRet = " + strRequestRet);
	          //解析返回字段，如果有error_code字段则表示消息推送失败，没有则表示推送成功
	          boolean bRet = strRequestRet.contains("error_code");
	          
	          if (strRequestRet != "") {
    	          if (!strRequestRet.contains("error_code")) {
    	              toast("推广消息推送成功！");
    	          } else {
    	              toast("推广消息推送失败，请稍后重试！");  
    	          }
	          } else {
	              toast("推广消息推送失败，请稍后重试！"); 
	          } 	                        
  			}
  		};	
  		
  	//***************************//
    
    // 以apikey的方式绑定
    private void initWithApiKey() {
        DKLog.d(TAG, Trace.getCurrentMethod());
        // Push: 无账号初始化，用api key绑定
        //checkApikey();
        ADPushManager.startWorkForAD(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(PushDemoActivity.this, "api_key"));
    }

    // 解绑
    private void unBindForApp() {
        DKLog.d(TAG, Trace.getCurrentMethod());
        // Push: 解绑
        PushManager.stopWork(getApplicationContext());
    }    
   
    public boolean onCreateOptionsMenu(Menu menu) {
        DKLog.d(TAG, Trace.getCurrentMethod());
    	 menu.add(Menu.NONE, Menu.FIRST + 1, 1, "关于").setIcon(
                 android.R.drawable.ic_menu_info_details);  
    	 
    	 menu.add(Menu.NONE, Menu.FIRST + 2, 2, "帮助").setIcon(
                 android.R.drawable.ic_menu_help);  
    	
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        DKLog.d(TAG, Trace.getCurrentMethod());
    	if(Menu.FIRST + 1 == item.getItemId()){
    		showAbout();
    		return true;
    	}
    	if(Menu.FIRST + 2 == item.getItemId()) {
    		showHelp();
    		return true;
    	}
    	
        return false;
    }
    
    public void onOptionsMenuClosed(Menu menu){
    	
    }
    
    public boolean onPrepareOptionsMenu(Menu menu){
    	
        return true;
    }
    
    // 关于
    private void showAbout() {
        Dialog alertDialog = new AlertDialog.Builder(PushDemoActivity.this)
            .setTitle("关于")
            .setMessage(R.string.text_about)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                }
                
            }).create();
        alertDialog.show();
    }
    
    // 帮助
    private void showHelp() {
        Dialog alertDialog = new AlertDialog.Builder(PushDemoActivity.this)
        .setTitle("帮助")
        .setMessage(R.string.text_help)
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
            }
            
        }).create();
        alertDialog.show();
    }
    
    @Override
    public void onStart() {
        DKLog.d(TAG, Trace.getCurrentMethod());
        super.onStart();
    }

    @Override
    public void onResume() {
        DKLog.d(TAG, Trace.getCurrentMethod());
        super.onResume();

        updateDisplay();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        DKLog.d(TAG, Trace.getCurrentMethod());
    }

    @Override
    public void onStop() {
        DKLog.d(TAG, Trace.getCurrentMethod());
        super.onStop();
    }

    @Override
    public void onDestroy() {
        DKLog.d(TAG, Trace.getCurrentMethod());
        Utils.setLogText(getApplicationContext(), Utils.logStringCache);
        super.onDestroy();
    }

    // 更新界面显示内容
    private void updateDisplay() {
        DKLog.d(TAG, Trace.getCurrentMethod() + Utils.logStringCache);

        if (logText != null) {
            logText.setText(Utils.logStringCache);
        }
        if (scrollView != null) {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}
