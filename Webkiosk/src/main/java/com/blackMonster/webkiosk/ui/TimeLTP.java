package com.blackMonster.webkiosk.ui;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.blackMonster.webkioskApp.R;

/**
 * Draws circular clock(having "L","T","P") present in timetable activity.
 */
public class TimeLTP extends View {

		public TimeLTP(Context context, AttributeSet attrs) {
			super(context, attrs);
			setFocusable(false);
			
		}


	Resources r = getResources();
	float dp = (float) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
			1, r.getDisplayMetrics());

	float cx, cy;
	float perc;
	int time_1, time_2;
	float cr1 = 34 * dp;
	float cr2 = 25 * dp;
	float cr22 =28 * dp;
	float cr3 = 22 * dp;
	
	float cr4 = 15 *dp;
	float cr5 = 13 *dp;
	float q = (float) 3.6;
	float pie = q * perc;
	float t1, t2;
	float a1,a2,a3,a4;
	Paint pgen = new Paint();
	RectF rectF;
	RectF rectF1;
	String lto;
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		cx = (float) (MeasureSpec.getSize(widthMeasureSpec)/ 2.0);
		cy = (float) (MeasureSpec.getSize(heightMeasureSpec) / 2.0);
	}
	
	public void setParams(int time1,
			int time2,char ltoo) {
			
		time_1 = time1;
		time_2 = time2;
		lto=ltoo + "";

	}
	
	
	int col = getResources().getColor(R.color.theme);

	@Override
	public void onDraw(Canvas canvas) {
	
		t1 = (((time_1 - 3) * 30));
		
		
				if (time_2 - time_1 == 2 || time_2 - time_1 == -10)
						t2 = 60;
				else
						t2 = 30;
		 a1 = (float) (cx + ((cr2 + (1 * dp)) * Math.sin((time_1 % 12) * (3.14 / 6))));
		 a2 = (float) (cy - ((cr2 + (1 * dp)) * Math.cos((time_1 % 12) * (3.14 / 6))));
		 a3 = (float) (cx + ((cr1 + (1 * dp)) * Math.sin((time_2 % 12) * (3.14 / 6))));
		 a4 = (float) (cy - ((cr1 + (1 * dp)) * Math.cos((time_2 % 12) * (3.14 / 6))));
		

	
		

		rectF = new RectF((cx -(cr1)), (cy - (cr1)), (cx + (cr1)), (cy + (cr1)));
		rectF1 = new RectF((cx - (cr2)), (cy - (cr2)), (cx + (cr2)), (cy + (cr2)));
		pgen.setColor(col);
		pgen.setStyle(Paint.Style.FILL_AND_STROKE);
		pgen.setAntiAlias(true);
		canvas.drawArc(rectF, t1, t2, true, pgen);
	
		pgen.setColor(col);//outter circle
		pgen.setStyle(Paint.Style.STROKE);
		pgen.setStrokeWidth(1f);
		canvas.drawCircle(cx, cy, cr1, pgen);
		pgen.setARGB(255,248, 248, 248);//inner circle
		pgen.setStyle(Paint.Style.FILL_AND_STROKE);
		pgen.setStrokeWidth(5f);
		
		canvas.drawCircle(cx, cy, cr2, pgen);
		
		
		pgen.setStyle(Paint.Style.STROKE);
		pgen.setStrokeWidth(1f);
		pgen.setColor(col);
		
		
		canvas.drawCircle(cx, cy, cr2+4, pgen);
		
		pgen.setColor(col);
		pgen.setStyle(Paint.Style.FILL);
		pgen.setTextAlign(Align.CENTER);
		pgen.setTextSize(28*dp);
		//pgen.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		
		canvas.drawText(lto, cx, (cy + (dp * 10)), pgen);
		
		pgen.setTextSize(20*dp);
		pgen.setColor(col);
		
		
		  for(int ii=1; ii<=12;ii++) 
		  { 
			  	if(ii==12||ii==3||ii==6||ii==9)
			  	{
			  			a1= (float) (cx + ((cr22+(1*dp))* Math.sin((ii%12)*(3.14/6)))); 
		  		 		a2 =(float)(cy-((cr22+(1*dp))*Math.cos((ii%12)*(3.14/6)))); 
		  		 		a3=(float) (cx + ((cr3+(1*dp))* Math.sin((ii%12)*(3.14/6))));
		  		 		a4= (float)(cy-((cr3+(1*dp))*Math.cos((ii%12)*(3.14/6)))); 
		  		 		canvas.drawLine(a1, a2, a3, a4, pgen); 
			  	}	
			}
		  	
		  
		  
		  	a1= (float) (cx + ((cr5+(1*dp))* Math.sin((time_1%12)*(3.14/6)))); 
			a2 =(float)(cy-((cr5+(1*dp))*Math.cos((time_1%12)*(3.14/6)))); 
			a3=(float) (cx + ((cr5+(1*dp))* Math.sin((time_2%12)*(3.14/6))));
			a4= (float)(cy-((cr5+(1*dp))*Math.cos((time_2%12)*(3.14/6)))); 
			pgen.setTextSize(7*dp);
			//canvas.drawText(time_1+"", a1 , a2 , pgen);
		//	canvas.drawText(time_2+"", a3 , a4 , pgen);
		  
		  	
	}
}

