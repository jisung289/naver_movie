package com.s_code.naver_mo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.http.RequestQueue;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler,RewardedVideoAdListener {


    private ImageView go_naver;
    private ImageView point_cha;
    private ImageView point_free;
    private EditText url_text;
    private ImageView go_url;
    private TextView user_point_t;

    private BillingProcessor bp;
    private String user_point;
    private RequestQueue queue;
    private Button view_ad;
    private RewardedVideoAd mRewardedVideoAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layer);

        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgqkKEFuUGOofj3lDOU8zIK7dWwRhkR1vgtQ9dLCFobwfBQhOc7yRaFOqa4skZQt6q+nqgBpX3Sty77sFk+Fgy/a3ssa+zu16KkANaP1gBPt+0JaWnlznmqXisCYUciTm6nUqTPxX4H3wGeXEIK0Wm00JHjrl9712I3m3bFPlOwKlNBSS8KHX9JcCd44k/1P6e73JT6lBm5VlHJQXnrZq+5YmZ8gMkgGhbIYPgnvpha8XL56RnozBIabXTuhvtpRIcMVliSQWH0HzYQURzZAaeP07P5WV2pbbBHo/8fHivzNZ4phqqSKGJKlXOIjyvs2Fqm2+KdVu45uCoRzS5hG4HQIDAQAB", this);
        bp.initialize();





        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        user_point=preferences.getString("user_point", "");
        Log.d("user_pointuser_point", "err:"+user_point);

        if(user_point==""){

            user_point="0";
            int ia=Integer.parseInt(user_point);
            ia=ia+30;
            user_point=String.valueOf(ia);
            SharedPreferences pref =getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("user_point", user_point);
            editor.commit();
        }


        go_naver=findViewById(R.id.imageView3);
        point_cha=findViewById(R.id.imageView4);
        point_free=findViewById(R.id.imageView5);
        user_point_t=findViewById(R.id.user_point_t);

        user_point_t.setText(user_point);
        url_text=findViewById(R.id.url_text);
        go_url=findViewById(R.id.go_url);

        go_url.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String m_url=url_text.getText().toString();

                            if(m_url.startsWith("http")) {

                            }else{
                                m_url="http://"+m_url;
                            }

                        Intent intent = new Intent(
                                getBaseContext(), // 현재화면의 제어권자
                                main.class); // 다음넘어갈 화면
                        intent.putExtra("get_u", m_url);

                        startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

                    }
                }
        );

        go_naver.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(
                                getBaseContext(), // 현재화면의 제어권자
                                main.class); // 다음넘어갈 화면
                        intent.putExtra("get_u", "https://m.naver.com");

                        startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

                    }
                }
        );


        MobileAds.initialize(this, "ca-app-pub-3604815056468599~6718426831");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        point_free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }else{
                    Toast.makeText(getBaseContext(), "현재 수령 가능한 보상이 없습니다. 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        point_cha.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        bp.purchase(MainActivity.this, "3000p");


                    }
                }
        );





    }



    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3604815056468599/4356590016",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem reward) {

        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        user_point=preferences.getString("user_point", "");
        int ia=Integer.parseInt(user_point);
        ia=ia+100;
        user_point=String.valueOf(ia);
        SharedPreferences pref =getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_point", user_point);
        editor.commit();

        user_point_t.setText(user_point);


        Toast.makeText(getBaseContext(), "광고포인트가 적립되었습니다.", Toast.LENGTH_SHORT).show();
        loadRewardedVideoAd();

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
      //  Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
     //   Log.d("ads_test", "err:"+errorCode);
      //  Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onRewardedVideoAdLoaded() {
      //  Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
     //   Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
     //   Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
      //  Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void  onResume() {
        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        user_point=preferences.getString("user_point", "");

        user_point_t.setText(user_point);
        super.onResume();



    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        // * 구매 완료시 호출
        // productId: 구매한 sku (ex) no_ads)
        // details: 결제 관련 정보

        if (productId.equals("3000p")) {
            // TODO: 구매 해 주셔서 감사합니다! 메세지 보내기

            SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
            user_point=preferences.getString("user_point", "");
            int ia=Integer.parseInt(user_point);
            ia=ia+3000;
            user_point=String.valueOf(ia);
            SharedPreferences pref =getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("user_point", user_point);
            editor.commit();

            user_point_t.setText(user_point);


            Toast.makeText(getBaseContext(), "3,000포인트가 충전되었습니다.", Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onPurchaseHistoryRestored() {
        // * 구매 정보가 복원되었을때 호출
        // bp.loadOwnedPurchasesFromGoogle() 하면 호출 가능
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        // * 구매 오류시 호출
        // errorCode == Constants.BILLING_RESPONSE_RESULT_USER_CANCELED 일때는
        // 사용자가 단순히 구매 창을 닫은것임으로 이것 제외하고 핸들링하기.


        Log.d("bill", "err:"+error);
    }

    @Override
    public void onBillingInitialized() {
        // * 처음에 초기화됬을때.
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }




}
