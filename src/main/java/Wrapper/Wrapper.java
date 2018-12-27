package Wrapper;





import com.sun.istack.internal.Nullable;

import java.io.Serializable;
import java.util.ArrayList;


public class Wrapper implements Serializable {
    private int stage;
    private ArrayList<Object> data;
    private final Object object;

    public int getStage() {
        return stage;
    }

    public ArrayList<Object> getData() {
        return data;
    }

    public Object getObject() {
        return object;
    }

    public Wrapper(int stage, @Nullable ArrayList<Object> data, @Nullable Object object ){
        this.stage=stage;
        this.data=data;
        this.object=object;
    }



}
