// IPayInterface.aidl
package android.com.aidlserver;

// Declare any non-default types here with import statements
import android.os.IBinder;

interface IPayInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void startPay(String name, String amount);

    void setPayStatusListener(IBinder listener);
}
