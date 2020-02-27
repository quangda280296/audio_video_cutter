package jack.com.servicekeep.task;

/**
 * Created by jacky on 1/12/18.
 */

public abstract class FLowPoolTask<T> {
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }


    public FLowPoolTask(T t) {
        setT(t);
    }

    public FLowPoolTask() {
    }

    public abstract boolean preIO();

    public abstract void updateUI();
}