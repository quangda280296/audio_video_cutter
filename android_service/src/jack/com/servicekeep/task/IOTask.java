package jack.com.servicekeep.task;

/**
 * Created by jacky on 1/12/18.
 */

public abstract class IOTask<T> {
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public IOTask() {
    }

    public abstract void doInIOThread();
}