package jack.com.servicekeep.task;

/**
 * Created by jacky on 1/12/18.
 */

public abstract class PoolTask<T> {

    public abstract boolean execute();


    public PoolTask() {

    }

    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

}