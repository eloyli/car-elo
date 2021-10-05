package com.shashank.sony.fancydialoglib;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.masfha.id.*;

/**
 * Created by Shashank Singhal on 03/01/2018.
 */

public class FancyAlertDialog {

    private Context activity;
    private Animation animation;
     private int pBtnColor,nBtnColor;
    private boolean cancel;



    private FancyAlertDialog(Builder builder){
         this.activity=builder.activity;
        this.animation=builder.animation;
          this.pBtnColor=builder.pBtnColor;
        this.nBtnColor=builder.nBtnColor;
        this.cancel=builder.cancel;
    }


    public static class Builder{
        private Context activity;
        
        private Animation animation;
        private int pBtnColor,nBtnColor;
        private boolean cancel;
		private TextView message1,title1;
		private ImageView iconImg;
		private Button pBtn,nBtn,neBtn;
		private LinearLayout view;
	    private Dialog dialog;

		
        public Builder(Activity activity){
            this.activity=activity;
        }
		
		public Builder(Context context){
			this.activity = context;
		}
		
        public Builder setTitle(String title){
            title1.setText(title);
            return this;
        }

        public Builder setBackgroundColor(int bgColor){
            if(bgColor!=0)
				view.setBackgroundColor(bgColor);
            return this;
        }

        public Builder setMessage(String message){
            message1.setText(message);
            return this;
        }

		public Builder setMessage(Spanned message){
            message1.setText(message);
            return this;
        }
        public Builder setPositiveBtnBackground(int pBtnColor){
            this.pBtnColor=pBtnColor;
            return this;
        }

       /* public Builder setNegativeBtnText(String negativeBtnText){
            this.negativeBtnText=negativeBtnText;
            return this;
        }

        public Builder setNegativeBtnBackground(int nBtnColor){
            this.nBtnColor=nBtnColor;
            return this;
        }*/


        //setIcon
        public Builder setIcon(int icon, Icon visibility){
            iconImg.setImageResource(icon);
            if(visibility==Icon.Visible)
				iconImg.setVisibility(View.VISIBLE);
            else
				iconImg.setVisibility(View.GONE);
            return this;
        }
		
		

        public Builder setAnimation(Animation animation){
            this.animation=animation;
            return this;
        }

        //set Positive listener
        public Builder setPositiveButton(String text, final FancyAlertDialogListener pListener){
			pBtn.setVisibility(View.VISIBLE);
			if(text!=null)
				pBtn.setText(text.toUpperCase());
			
			if(pListener!=null) {
				pBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							pListener.OnClick();
							dialog.dismiss();
						}
					});
            }else{
				pBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							dialog.dismiss();
						}
					});
			}
			return this;
        }

        //set Negative listener
        public Builder setNegativeButton(String text, final FancyAlertDialogListener nListener){
            if(text!=null)
				nBtn.setText(text.toUpperCase());
			nBtn.setVisibility(View.VISIBLE);
			
			if(nListener!=null){
				nBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							nListener.OnClick();
							dialog.dismiss();
						}
					});
            }else{
				nBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							dialog.dismiss();
						}
					});
			}
			return this;
        }
		
		public Builder setNeutralButton(String text, final FancyAlertDialogListener nListener){
            if(text!=null)
				neBtn.setText(text.toUpperCase());
			neBtn.setVisibility(View.VISIBLE);

			if(nListener!=null){
				neBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							nListener.OnClick();
							dialog.dismiss();
						}
					});
            }else{
				neBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							dialog.dismiss();
						}
					});
			}
			return this;
        }

        public Builder isCancellable(boolean cancel){
            this.cancel=cancel;
            return this;
        }

        public Builder build(){
            if(animation==Animation.POP)
            dialog=new Dialog(activity,R.style.PopTheme);
            else if(animation==Animation.SIDE)
            dialog=new Dialog(activity,R.style.SideTheme);
            else if(animation==Animation.SLIDE)
            dialog=new Dialog(activity,R.style.SlideTheme);
            else
            dialog=new Dialog(activity,R.style.FadeTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(cancel);
            dialog.setContentView(R.layout.fancyalertdialog);

            //getting resources
            view=(LinearLayout)dialog.findViewById(R.id.background);
            title1= (TextView) dialog.findViewById(R.id.title);
            message1=(TextView)dialog.findViewById(R.id.message);
            iconImg=(ImageView)dialog.findViewById(R.id.icon);
			
            pBtn=(Button)dialog.findViewById(R.id.positiveBtn);
			nBtn=(Button)dialog.findViewById(R.id.negativeBtn);
			neBtn=(Button)dialog.findViewById(R.id.neutralBtn);
            /*if(pBtnColor!=0)
            { GradientDrawable bgShape = (GradientDrawable)pBtn.getBackground();
              bgShape.setColor(pBtnColor);
            }*/
           /* if(nBtnColor!=0)
            { GradientDrawable bgShape = (GradientDrawable)nBtn.getBackground();
              bgShape.setColor(nBtnColor);
            }*/
         //   if(negativeBtnText!=null)
          //  nBtn.setText(negativeBtnText);
            
            

            return this;
        }
		
		public FancyAlertDialog show() {
			dialog.show();
			return new FancyAlertDialog(this);
		}
    }

}
