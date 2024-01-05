package myschoolapp.com.gsnedutech.activity.fee;

import com.razorpay.PaymentData;

public interface PaymentResultWithDataListener {
    void onPaymentSuccess(String var1, PaymentData var2);

    void onPaymentError(int var1, String var2, PaymentData var3);
}
