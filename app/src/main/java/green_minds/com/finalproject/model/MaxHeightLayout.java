package green_minds.com.finalproject.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MaxHeightLayout extends LinearLayout {

    public MaxHeightLayout(Context context) {
        super(context);
    }

    public MaxHeightLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxHeightLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MaxHeightLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = MeasureSpec.makeMeasureSpec(600, MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }
}
