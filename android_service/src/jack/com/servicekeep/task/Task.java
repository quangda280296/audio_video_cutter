package jack.com.servicekeep.task;

/**
 * Created by jacky on 1/12/18.
 */
public abstract class Task<T> {
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }


    public Task(T t) {
        setT(t);
    }

    public Task() {
    }

    public abstract T preIO();

    public abstract void updateUI(T t);
}