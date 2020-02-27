package jack.com.servicekeep.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class DisposableManager {

    private static HashMap<String, CompositeDisposable> hashMapRequest = new HashMap<>();

    public static void add(String keyRequest, Disposable disposable) {
        getCompositeDisposable(keyRequest).add(disposable);
    }

    public static void dispose(String keyRequest) {
        getCompositeDisposable(keyRequest).dispose();
    }

    public static void disposeAll() {
        synchronized (hashMapRequest) {
            List<String> keys = new ArrayList<>(hashMapRequest.keySet());
            for (int i = 0; i < keys.size(); i++) {
                if (hashMapRequest.get(keys.get(i)) != null) {
                    dispose(keys.get(i));
                    hashMapRequest.remove(keys.get(i));
                }
            }
        }
    }

    public static int size() {
        return hashMapRequest.size();
    }

    private static CompositeDisposable getCompositeDisposable(String keyRequest) {
        synchronized (hashMapRequest) {
            if (!hashMapRequest.containsKey(keyRequest)) {
                CompositeDisposable compositeDisposable = new CompositeDisposable();
                hashMapRequest.put(keyRequest, compositeDisposable);
                return compositeDisposable;
            }
            return hashMapRequest.get(keyRequest);
        }
    }

    private DisposableManager() {
    }
}