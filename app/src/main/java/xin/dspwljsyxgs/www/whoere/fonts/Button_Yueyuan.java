package xin.dspwljsyxgs.www.whoere.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


/**
 * Created by Cola_Mentos on 2018/5/3.
 */

public class Button_Yueyuan extends android.support.v7.widget.AppCompatButton{
    public Button_Yueyuan(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    @Override
    public void setTypeface(Typeface tf){
        tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/悦圆字体.otf");
        super.setTypeface(tf);
    }

}
