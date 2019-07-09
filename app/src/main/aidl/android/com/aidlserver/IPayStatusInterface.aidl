// IPayStatusInterface.aidl
package android.com.aidlserver;

// Declare any non-default types here with import statements

interface IPayStatusInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onPaySuccess();
    void onPayCancel();
}
