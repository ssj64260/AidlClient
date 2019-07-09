package android.com.aidlclient;

import android.com.aidlserver.IPayInterface;
import android.com.aidlserver.IPayStatusInterface;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String PAYMENT_AMOUNT = "1024.00";
    private static final String PAYMENT_NAME = "7月份伙食费";

    private TextView tvContent;
    private Button btnToPay;

    private IPayInterface mBinder;
    private boolean mPaySuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        tvContent = findViewById(R.id.tv_content);
        btnToPay = findViewById(R.id.btn_to_pay);

        final String content = PAYMENT_NAME + "￥" + PAYMENT_AMOUNT + "\n待支付";
        tvContent.setText(content);

        btnToPay.setOnClickListener(mClick);
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setPackage("android.com.aidlserver");
        intent.setAction("android.com.aidlserver.action");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void startPay() {
        try {
            mBinder.setPayStatusListener(mPayListener);
            mBinder.startPay(PAYMENT_NAME, PAYMENT_AMOUNT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBinder != null) {
            unbindService(mConnection);
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = IPayInterface.Stub.asInterface(service);
            if (mBinder != null) {
                startPay();
            } else {
                Toast.makeText(MainActivity.this, "支付服务绑定失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private final View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_to_pay:
                    if (mPaySuccess) {
                        finish();
                        return;
                    }

                    if (mBinder != null) {
                        startPay();
                    } else {
                        bindService();
                    }
                    break;
            }
        }
    };

    private final IPayStatusInterface.Stub mPayListener = new IPayStatusInterface.Stub() {
        @Override
        public void onPaySuccess() throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPaySuccess = true;

                    final String content = PAYMENT_NAME + "￥" + PAYMENT_AMOUNT + "\n已支付";
                    tvContent.setText(content);
                    btnToPay.setText("确认");

                    Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onPayCancel() throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "支付已取消", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
