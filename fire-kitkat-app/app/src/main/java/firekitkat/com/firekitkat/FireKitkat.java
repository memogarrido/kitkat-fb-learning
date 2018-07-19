package firekitkat.com.firekitkat;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FireKitkat extends Application {

    static FireKitkat fireKitkatInstance;

    FirebaseUser user;


    public FireKitkat() {
        fireKitkatInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setUser(FirebaseAuth.getInstance().getCurrentUser());
    }

    public static FireKitkat getInstance() {
        return fireKitkatInstance;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }


}
