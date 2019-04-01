package com.example.com314.zxing;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.net.*;
import java.io.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    String ISBN="";
    Button buttonScan;
    TextView mTitleView,mAuthorView,mPriceView,mPublishView;

    private IntentIntegrator qrScan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qrScan=new IntentIntegrator(this);

        buttonScan = (Button) findViewById(R.id.ScanBtn);
        mTitleView = (TextView) findViewById(R.id.TitleText);
        mAuthorView = (TextView) findViewById(R.id.AuthirText);
        mPriceView = (TextView)  findViewById(R.id.PriceText);
        mPublishView=(TextView)findViewById(R.id.PublishText);
        buttonScan.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ScanBtn:
                qrScan.setPrompt("스캔중입니다...");
                qrScan.initiateScan();
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
               ISBN=result.getContents();
                NetworkTask tast=new NetworkTask();
                tast.execute();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public class NetworkTask extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            String clientId = "OehvCXckhMJzlsPYh9KP";//애플리케이션 클라이언트 아이디값";
            String clientSecret = "6sjVvaOf7p";//애플리케이션 클라이언트 시크릿값";
            try {
                String text = URLEncoder.encode(ISBN, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/search/book.json?query="+ text+"&display=10&start=1"; // json 결과
                //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream(),"utf-8"));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine+"\n");
                }

                Log.d("seo-test",response.toString());

                br.close();
                return response.toString();
            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("seo-test : s",s);
            try {
                JSONObject object=new JSONObject(s);
                JSONArray array=object.getJSONArray("items");
                Log.d("seo-test",array+"뀨뀨까까");
                int index=array.length();
                if(index==0){
                    Toast.makeText(getApplicationContext(),"죄송합니다. 이 책은 검색결과가 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                }
                    String title = null,author="",price="",publisher="";
                    for(int i=0;i<index;i++){
                        JSONObject jsonObject=array.getJSONObject(i);
                        title=jsonObject.getString("title");
                        author=jsonObject.getString("author");
                        price=jsonObject.getString("price");
                        publisher=jsonObject.getString("publisher");
                        Log.d("seo-test",title+author+price+publisher+"뀨뀨까까");
                    }
                    mTitleView.setText(title);
                    mAuthorView.setText(author);
                    mPriceView.setText(price);
                    mPublishView.setText(publisher);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
