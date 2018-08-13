package com.example.admin.learnbinderwithaidl;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * @author admin
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BindPool bindPool;
    private ICompute mCompute;
    private ISecurityCenter mSecurityCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: MainActivity");
        Thread workThread = new WorkThread();
        workThread.start();
    }

    public class WorkThread extends Thread {
        @Override
        public void run() {
            bindPool = BindPool.getInstance(MainActivity.this);
            IBinder securityBinder = bindPool.queryBinder(BindPool.BINDER_SECURITY_CENTER);
            IBinder computeBinder = bindPool.queryBinder(BindPool.BINDER_COMPUTE);
            String content = "hello";
            try {
                mSecurityCenter  = ISecurityCenterImpl.asInterface(securityBinder);
                mCompute = IComputeImpl.asInterface(computeBinder);
                Log.i(TAG, "doWork: encrypt " + mSecurityCenter.encrypt(content));
                Log.i(TAG, "doWork: decrypt " + mSecurityCenter.decrypt(content));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                mCompute = IComputeImpl.asInterface(computeBinder);
                Log.i(TAG, "doWork: "+mCompute.add(3,3));
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bindPool.unBindService();
    }
}
