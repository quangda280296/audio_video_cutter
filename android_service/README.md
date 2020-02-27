# 1. VMApplication.

 - Ở application kế thừa application từ thư viện .

Example:
```java
public class  XXXApp extent VMApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        initInfoDevice(AppConstant.CODE_APP, BuildConfig.VERSION_NAME);

        .....
        //to do code init app
    }
}
```

# 2. Extend : BaseVMActivity.java  -> theme old && BaseVMAppCompatActivity.java -> theme new


     Kế thừa tất cả Activity trong app từ base BaseVMActivity trong service <br>
     mục đích để biết minh đang ở trong app chính . Nếu app mình đã có 1 base <br>
     Activty khác mình chỉ cần cho base đó kế thừa lại BaseVMActivity .<br>