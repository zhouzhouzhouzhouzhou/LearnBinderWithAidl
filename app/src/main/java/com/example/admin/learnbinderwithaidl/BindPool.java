package com.example.admin.learnbinderwithaidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

import static android.content.ContentValues.TAG;

/**
 * @author zhou.jn
 * @creator_at 2018/8/11 11:20
 */
public class BindPool {
    private Context mContext;
    private IBinderPool mBinderPool;
    private static volatile BindPool sBindInstance;
    private CountDownLatch mConnectBinderPoolCountLatch;
    public final static int BINDER_SECURITY_CENTER = 1;
    public final static int BINDER_COMPUTE = 2;

    public BindPool(Context context) {
        mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    public static BindPool getInstance(Context context) {
        if (sBindInstance == null) {
            synchronized (BindPool.class) {
                if (sBindInstance == null) {
                    sBindInstance = new BindPool(context);
                }
            }
        }
        return sBindInstance;
    }

    private synchronized void connectBinderPoolService() {
        mConnectBinderPoolCountLatch = new CountDownLatch(1);
        Intent service = new Intent(mContext, BindPoolService.class);
        mContext.bindService(service, mConnection, Context.BIND_AUTO_CREATE);
        try {
            mConnectBinderPoolCountLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinderPool = IBinderPool.Stub.asInterface(iBinder);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectBinderPoolCountLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.i(TAG, "binder Died: ");
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            mBinderPool = null;
            connectBinderPoolService();
        }
    };

    public static class IBinderPoolImpl extends IBinderPool.Stub {

        public IBinderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_SECURITY_CENTER: {
                    binder = new ISecurityCenterImpl();
                    break;
                }
                case BINDER_COMPUTE: {
                    binder = new IComputeImpl();
                    break;
                }
                default: {
                    break;
                }
            }
            return binder;
        }
    }

    public IBinder queryBinder(int bindCode) {
        IBinder binder = null;
        if (mBinderPool != null) {
            try {
                binder = mBinderPool.queryBinder(bindCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return binder;
    }

    public void unBindService() {
        mContext.unbindService(mConnection);
        mConnection = null;
    }
}

