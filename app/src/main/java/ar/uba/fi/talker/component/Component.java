package ar.uba.fi.talker.component;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;

public abstract class Component extends View {

	private boolean active = true;
	private Rect bounds;

	public Component(Context context) {
		super(context);
		bounds = new Rect();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		return active && super.dispatchTouchEvent(event);
	}

	protected Rect getBounds() {
		return bounds;
	}

	public boolean isPointInnerBounds(Point point) {
		return false;
	}

	public boolean isPointInnerEraseBounds(Point point) {
		return false;
	}
	
	@Override
	public boolean performClick() {
		return active && super.performClick();
	}

	public void toggleActive() {
		active = !active;
		this.bringToFront();
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
	    
		Bundle bundle = new Bundle();
	    bundle.putParcelable("instanceState", super.onSaveInstanceState());
	    bundle.putParcelable("bounds", this.getBounds());
	    return bundle;
		
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		 if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			this.bounds = bundle.getParcelable("bounds");

			state = bundle.getParcelable("instanceState");
		}
		super.onRestoreInstanceState(state);
	}

	public Parcelable getParcelable() {
		return this.onSaveInstanceState();
	}

	public void restoreFromParcelable(Parcelable data) {
		this.onRestoreInstanceState(data);
	}
}
