package mph.trunksku.apps.myssh.view;

import android.annotation.*;
import android.content.*;
import android.content.res.*;
import android.graphics.drawable.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import com.masfha.id.*;

public class MaterialButton extends Button {

    private StateListDrawable mNormalDrawable;
    private CharSequence mNormalText;
    private float cornerRadius;

    public MaterialButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public MaterialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaterialButton(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        mNormalDrawable = new StateListDrawable();
        if (attrs != null) {
            initAttributes(context, attrs);
        }
        mNormalText = getText().toString();
        setBackgroundCompat(mNormalDrawable);
    }

    private void initAttributes(Context context, AttributeSet attributeSet) {
        TypedArray attr = getTypedArray(context, attributeSet, R.styleable.MaterialButton);
        if (attr == null) {
            return;
        }

        try {

            float defValue = getDimension(R.dimen.corner_radius);
            cornerRadius = attr.getDimension(R.styleable.MaterialButton_dmb_cornerRadius, defValue);

            mNormalDrawable.addState(new int[]{android.R.attr.state_pressed},
                    createPressedDrawable(attr));
            mNormalDrawable.addState(new int[]{android.R.attr.state_focused},
                    createPressedDrawable(attr));
            mNormalDrawable.addState(new int[]{android.R.attr.state_selected},
                    createPressedDrawable(attr));
            mNormalDrawable.addState(new int[]{}, createNormalDrawable(attr));

        } finally {
            attr.recycle();
        }
    }

    private LayerDrawable createNormalDrawable(TypedArray attr) {
        LayerDrawable drawableNormal =
                (LayerDrawable) getDrawable(R.drawable.rect_normal).mutate();

        GradientDrawable drawableTop =
                (GradientDrawable) drawableNormal.getDrawable(0).mutate();
        drawableTop.setCornerRadius(getCornerRadius());

        int blueDark = getColor(R.color.blue_pressed);
        int colorPressed = attr.getColor(R.styleable.MaterialButton_dmb_colorPressed, blueDark);
        drawableTop.setColor(colorPressed);

        GradientDrawable drawableBottom =
                (GradientDrawable) drawableNormal.getDrawable(1).mutate();
        drawableBottom.setCornerRadius(getCornerRadius());

        int blueNormal = getColor(R.color.blue_normal);
        int colorNormal = attr.getColor(R.styleable.MaterialButton_dmb_colorNormal, blueNormal);
        drawableBottom.setColor(colorNormal);
        return drawableNormal;
    }

    private Drawable createPressedDrawable(TypedArray attr) {
        GradientDrawable drawablePressed =
                (GradientDrawable) getDrawable(R.drawable.rect_pressed).mutate();
        drawablePressed.setCornerRadius(getCornerRadius());

        int blueDark = getColor(R.color.blue_pressed);
        int colorPressed = attr.getColor(R.styleable.MaterialButton_dmb_colorPressed, blueDark);
        drawablePressed.setColor(colorPressed);

        return drawablePressed;
    }

    protected Drawable getDrawable(int id) {
        return getResources().getDrawable(id);
    }

    protected float getDimension(int id) {
        return getResources().getDimension(id);
    }

    protected int getColor(int id) {
        return getResources().getColor(id);
    }

    protected TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public StateListDrawable getNormalDrawable() {
        return mNormalDrawable;
    }

    public CharSequence getNormalText() {
        return mNormalText;
    }

    public void setNormalText(CharSequence normalText) {
        mNormalText = normalText;
    }

    /**
     * Set the View's background. Masks the API changes made in Jelly Bean.
     *
     * @param drawable
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void setBackgroundCompat(Drawable drawable) {
        int pL = getPaddingLeft();
        int pT = getPaddingTop();
        int pR = getPaddingRight();
        int pB = getPaddingBottom();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
        setPadding(pL, pT, pR, pB);
    }
}


