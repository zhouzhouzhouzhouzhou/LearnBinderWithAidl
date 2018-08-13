package com.example.admin.learnbinderwithaidl;

import android.os.RemoteException;

/**
 * @author zhou.jn
 * @creator_at 2018/8/11 10:59
 */
public class IComputeImpl extends ICompute.Stub {
    @Override
    public int add(int i, int j) throws RemoteException {
        return i + j;
    }
}
