package ar.uba.fi.talker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import ar.uba.fi.talker.component.Component;
import ar.uba.fi.talker.component.ComponentFactory;
import ar.uba.fi.talker.component.ComponentType;
import ar.uba.fi.talker.component.Image;
import ar.uba.fi.talker.component.Text;
import ar.uba.fi.talker.component.command.ActivityCommand;
import ar.uba.fi.talker.paint.PaintManager;
import ar.uba.fi.talker.paint.PaintType;

public class Scenario extends FrameLayout {

	private ComponentType activeComponentType;

	private Component activeComponent;
	
	private Bitmap mImage = null;

	public Scenario(Context context) {
		super(context);
		this.init();
	}

	public Scenario(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}

	public Scenario(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init();
	}

	private void init() {
		this.setActiveComponentType(ComponentType.PENCIL);
	}

	@Override
	public boolean performClick() {
		return super.performClick() || true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean foundElement = false;
		int eventX = (int) event.getAxisValue(MotionEvent.AXIS_X);
		int eventY = (int) event.getAxisValue(MotionEvent.AXIS_Y);
		Point point = new Point(eventX, eventY);
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			for (int i = 0; i < this.getChildCount(); i++) {
				Component child = (Component) this.getChildAt(i);
				
				foundElement = child.isPointInnerBounds(point);
				if (foundElement) {
					activeComponent = child;
					activeComponent.toggleActive();
				}
			}
			if (!foundElement) {
				activeComponent = ComponentFactory.createComponent(
						activeComponentType, getContext());
				android.view.ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
				
				layoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
				layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
				
				this.addView(activeComponent, layoutParams);
			}
			

		}
		activeComponent.onTouchEvent(event);
		return this.performClick();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (mImage != null) {
			int width = this.getWidth();
			int height = this.getHeight();
			Bitmap resized = Bitmap.createScaledBitmap(mImage, width , height, true);
			BitmapShader fillBMPshader = new BitmapShader(
					resized, 
					Shader.TileMode.REPEAT, 
					Shader.TileMode.REPEAT
					);  
			
			Paint fillPaint = PaintManager.getPaint(PaintType.ERASE);
			fillPaint.setShader(fillBMPshader);
			mImage = null;
		}
		
		super.onDraw(canvas);
	}
	
	public void setBackgroundImage(Bitmap image) {
		mImage = image;
		
        BitmapDrawable background = new BitmapDrawable(getResources(), image);
		this.setBackground(background);
	}

	public void clear() {
		this.removeAllViews();
	}

	public void setActiveComponentType(ComponentType type) {
		this.setActiveComponentType(type, null);
	}
	
	public void setActiveComponentType(ComponentType type, ActivityCommand command) {
		this.activeComponentType = type;
	}

	public void setText(Editable text) {
		Component alterComponent = ComponentFactory.createComponent(ComponentType.TEXT, getContext());
	
		android.view.ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
		layoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		
		this.addView(alterComponent, layoutParams);
		((Text) alterComponent).setValue(text);
		alterComponent.toggleActive();
	}

	public void addImage(ImageView image) {
		Component alterComponent = ComponentFactory.createComponent(ComponentType.IMAGE, getContext());
	
		android.view.ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
		layoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		
		this.addView(alterComponent, layoutParams);
		
		((Image) alterComponent).setContent(image);
	}

}
