package z.houbin.webtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 通过 Service 添加 WebView 控件并模拟点击，界面不可见，用户无感知
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickWeb(View view) {
        Toast.makeText(getApplicationContext(), "开启服务", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), WebService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
    }

    public void test(View view) {
        TextView t = findViewById(R.id.label);
        t.setText(String.valueOf(System.currentTimeMillis()));
    }
}
