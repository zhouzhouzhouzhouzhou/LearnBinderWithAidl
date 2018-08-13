package com.example.admin.learnbinderwithaidl;

import android.os.RemoteException;

/**
 * @author zhou.jn
 * @creator_at 2018/8/11 11:00
 */
public class ISecurityCenterImpl extends ISecurityCenter.Stub {
    private final static char SERCET_CHAR = '^';

    @Override
    public String encrypt(String content) throws RemoteException {
        char [] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i ++){
            chars[i] ^=SERCET_CHAR;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
