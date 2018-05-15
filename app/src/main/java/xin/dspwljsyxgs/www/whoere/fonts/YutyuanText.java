package xin.dspwljsyxgs.www.whoere.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.widget.TextView;

/**
 * Created by root on 18-4-21.
 */

public class YutyuanText extends android.support.v7.widget.AppCompatTextView {
    public YutyuanText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//重写设置字体方法
    @Override
    public void setTypeface(Typeface tf) {
        tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/悦圆字体.otf");
        super.setTypeface(tf);
    }
}

