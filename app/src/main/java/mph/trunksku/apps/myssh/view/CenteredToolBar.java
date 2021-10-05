package mph.trunksku.apps.myssh.view;

import android.content.*;
import android.graphics.*;
import android.support.annotation.*;
import android.support.v7.appcompat.*;
import android.support.v7.widget.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import android.support.v7.widget.Toolbar;

public class CenteredToolBar extends Toolbar {

    private TextView centeredTitleTextView;

    public CenteredToolBar(Context context) {
        super(context);
    }

    public CenteredToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CenteredToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTitle(@StringRes int resId) {
        String s = getResources().getString(resId);
        setTitle(s);
    }

    @Override
    public void setTitle(CharSequence title) {
        getCenteredTitleTextView().setText(title);
    }
	
	@Override
    public void setTitleTextColor(int color) {
        getCenteredTitleTextView().setTextColor(color);
    }

    @Override
    public CharSequence getTitle() {
        return getCenteredTitleTextView().getText().toString();
    }

    public void setTypeface(Typeface font) {
        getCenteredTitleTextView().setTypeface(font);
    }

    private TextView getCenteredTitleTextView() {
        if (centeredTitleTextView == null) {
            centeredTitleTextView = new TextView(getContext());
			//  centeredTitleTextView.setTypeface(...);
            centeredTitleTextView.setSingleLine();
            centeredTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            centeredTitleTextView.setGravity(Gravity.CENTER);
            centeredTitleTextView.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);

            Toolbar.LayoutParams lp = new Toolbar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            centeredTitleTextView.setLayoutParams(lp);

            addView(centeredTitleTextView);
        }
        return centeredTitleTextView;
    }
}


