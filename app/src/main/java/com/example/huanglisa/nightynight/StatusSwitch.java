package com.example.huanglisa.nightynight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

/**
 * Created by huanglisa on 11/16/16.
 */
public class StatusSwitch extends Switch{

    private boolean needUpdate = true;

    public StatusSwitch(Context context) {
        super(context);
    }

    public StatusSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatusSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setChecked(boolean isChekced, boolean needUpdate){
        //System.out.format("non default setChecked %n");
        this.needUpdate = needUpdate;
        super.setChecked(isChekced);
    }

    @Override
    public void setChecked(boolean isChekced){
        //System.out.format("default setChecked %n");
        this.needUpdate = true;
        super.setChecked(isChekced);

    }

    public boolean checkNeedUpdate(){
        return this.needUpdate;
    }

}




