package com.lewis.uiinteractive;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private TextView textView;
    private Button button;

    /**
     * Called when the activity is first created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("RootyInfo","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView1);
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个用于显示前三种后台线程和UI线程交互的线程
                new TestThread(MainActivity.this).start();
                //创建一个用于显示AsyncTask实现交互的TestAsyncTask
                new TestAsyncTask().execute("Test", "AsyncTask");
            }
        });
    }

    class TestAsyncTask extends AsyncTask<String,Integer,String>{
        //TestAsyncTask被后台线程履行后，被UI线程调用，一般用于初始化界面控件，如进度条
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //doInBackground履行完后由UI线程滴啊用，用于更新界面操作
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textView.setText(s);
        }

        //在onPreExecute履行后被启动AsyncTask的后台线程调用，将成果返回给UI线程
        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            for (String string : params){
                sb.append(string);
            }
            return sb.toString();
        }
    }

    /**
     * 用于线程间通信的Handler
     */
    class TestHandler extends Handler{
        public TestHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println("123");
            textView.setText((String)msg.getData().get("tag"));
            super.handleMessage(msg);
        }
    }

    /**
     * 后台线程
     */
    class TestThread extends Thread{
        Activity activity;
        public TestThread(Activity activity){
            this.activity = activity;
        }

        @Override
        public void run() {
            super.run();
            //下面代码用来演示Activity.runOnUiThread(Runnable)办法的实现
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Test runOnUiThread");
                }
            });
            //下面代码用来演示View.post(Runnable)办法的实现
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Test View.post(Runnable)");
                }
            });
            //下面代码用来演示View.postDelayed(Runnable)办法的实现
            textView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Test View.postDelayed(Runnable,long)");
                }
            },1000);
            //下面的代码用来演示Handler办法实现
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("tag","Test Handler");
            msg.setData(bundle);
            new TestHandler(Looper.getMainLooper()).sendMessage(msg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
