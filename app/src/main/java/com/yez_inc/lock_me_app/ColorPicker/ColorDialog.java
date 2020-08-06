package com.yez_inc.lock_me_app.ColorPicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.yez_inc.lock_me_app.R;

public class ColorDialog {
	public interface OnColorPickerListener {
		void onCancel(ColorDialog dialog);
		void onOk(ColorDialog dialog, int color);
	}
	private final AlertDialog dialog;
	private final boolean supportsAlpha;
	private final OnColorPickerListener listener;
	private final View viewHue;
	private final ColorSquare viewSatVal;
	private final ImageView viewCursor;
	private final ImageView viewAlphaCursor;
	private final View viewNewColor;
	private final View viewAlphaOverlay;
	private final ImageView viewTarget;
	private final ImageView viewAlphaChecker;
	private final ViewGroup viewContainer;
	private final float[] currentColorHsv = new float[3];
	private int alpha;
	public ColorDialog(final Context context, int color, OnColorPickerListener listener) {
		this(context, color, false, listener);
	}
	public ColorDialog(final Context context, int color, boolean supportsAlpha, OnColorPickerListener listener) {
		this.supportsAlpha = supportsAlpha;
		this.listener = listener;
		if (!supportsAlpha) { // remove alpha if not supported
			color = color | 0xff000000;
		}
		Color.colorToHSV(color, currentColorHsv);
		alpha = Color.alpha(color);
		final View view = LayoutInflater.from(context).inflate(R.layout.color_picker_dialog, null);
		viewHue = view.findViewById(R.id.viewHue);
		viewSatVal = (ColorSquare) view.findViewById(R.id.viewSatBri);
		viewCursor = (ImageView) view.findViewById(R.id.ColorPicker_cursor);
		viewNewColor = view.findViewById(R.id.newColor);
		viewTarget = (ImageView) view.findViewById(R.id.target);
		viewContainer = (ViewGroup) view.findViewById(R.id.viewContainer);
		viewAlphaOverlay = view.findViewById(R.id.overlay);
		viewAlphaCursor = (ImageView) view.findViewById(R.id.alphaCursor);
		viewAlphaChecker = (ImageView) view.findViewById(R.id.alphaChecker);

		{ // hide/show alpha
			viewAlphaOverlay.setVisibility(supportsAlpha? View.VISIBLE: View.GONE);
			viewAlphaCursor.setVisibility(supportsAlpha? View.VISIBLE: View.GONE);
			viewAlphaChecker.setVisibility(supportsAlpha? View.VISIBLE: View.GONE);
		}

		viewSatVal.setHue(getHue());
		view.findViewById(R.id.oldColor).setBackgroundColor(color);
		viewNewColor.setBackgroundColor(color);

		viewHue.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
				|| event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_UP) {
					float y = event.getY();
					if (y < 0.f) y = 0.f;
					if (y > viewHue.getMeasuredHeight()) {
						y = viewHue.getMeasuredHeight() - 0.001f; // to avoid jumping the cursor from bottom to top.
					}
					float hue = 360.f - 360.f / viewHue.getMeasuredHeight() * y;
					if (hue == 360.f) hue = 0.f;
					setHue(hue);
					// update view
					viewSatVal.setHue(getHue());
					moveCursor();
					viewNewColor.setBackgroundColor(getColor());
					updateAlphaView();
					return true;
				}
				return false;
			}
		});
		if (supportsAlpha) viewAlphaChecker.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if ((event.getAction() == MotionEvent.ACTION_MOVE)
				|| (event.getAction() == MotionEvent.ACTION_DOWN)
				|| (event.getAction() == MotionEvent.ACTION_UP)) {
					float y = event.getY();
					if (y < 0.f) {
						y = 0.f;
					}
					if (y > viewAlphaChecker.getMeasuredHeight()) {
						y = viewAlphaChecker.getMeasuredHeight() - 0.001f; // to avoid jumping the cursor from bottom to top.
					}
					final int a = Math.round(255.f - ((255.f / viewAlphaChecker.getMeasuredHeight()) * y));
					ColorDialog.this.setAlpha(a);
					// update view
					moveAlphaCursor();
					int col = ColorDialog.this.getColor();
					int c = a << 24 | col & 0x00ffffff;
					viewNewColor.setBackgroundColor(c);
					return true;
				}
				return false;
			}
		});
		viewSatVal.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
				|| event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_UP) {
					float x = event.getX(); // touch event are in dp units.
					float y = event.getY();
					if (x < 0.f) x = 0.f;
					if (x > viewSatVal.getMeasuredWidth()) x = viewSatVal.getMeasuredWidth();
					if (y < 0.f) y = 0.f;
					if (y > viewSatVal.getMeasuredHeight()) y = viewSatVal.getMeasuredHeight();
					setSat(1.f / viewSatVal.getMeasuredWidth() * x);
					setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));
					// update view
					moveTarget();
					viewNewColor.setBackgroundColor(getColor());
					return true;
				}
				return false;
			}
		});
		dialog = new AlertDialog.Builder(context)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (ColorDialog.this.listener != null) {
					ColorDialog.this.listener.onOk(ColorDialog.this, getColor());
				}
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (ColorDialog.this.listener != null) {
					ColorDialog.this.listener.onCancel(ColorDialog.this);
				}
			}
		})
		.setOnCancelListener(new OnCancelListener() {
			// if back button is used, call back our listener.
			@Override
			public void onCancel(DialogInterface paramDialogInterface) {
				if (ColorDialog.this.listener != null) {
					ColorDialog.this.listener.onCancel(ColorDialog.this);
				}

			}
		})
		.create();
		// kill all padding from the dialog window
		dialog.setView(view, 0, 0, 0, 0);

		// move cursor & target on first draw
		ViewTreeObserver vto = view.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				moveCursor();
				if (ColorDialog.this.supportsAlpha) moveAlphaCursor();
				moveTarget();
				if (ColorDialog.this.supportsAlpha) updateAlphaView();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }else //noinspection deprecation
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
		});
	}

	private void moveCursor() {
		float y = viewHue.getMeasuredHeight() - (getHue() * viewHue.getMeasuredHeight() / 360.f);
		if (y == viewHue.getMeasuredHeight()) y = 0.f;
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursor.getLayoutParams();
		layoutParams.leftMargin = (int) (viewHue.getLeft() - Math.floor(viewCursor.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
		layoutParams.topMargin = (int) (viewHue.getTop() + y - Math.floor(viewCursor.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
		viewCursor.setLayoutParams(layoutParams);
	}

	private void moveTarget() {
		float x = getSat() * viewSatVal.getMeasuredWidth();
		float y = (1.f - getVal()) * viewSatVal.getMeasuredHeight();
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
		layoutParams.leftMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
		layoutParams.topMargin = (int) (viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
		viewTarget.setLayoutParams(layoutParams);
	}

	private void moveAlphaCursor() {
		final int measuredHeight = this.viewAlphaChecker.getMeasuredHeight();
		float y = measuredHeight - ((this.getAlpha() * measuredHeight) / 255.f);
		final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.viewAlphaCursor.getLayoutParams();
		layoutParams.leftMargin = (int) (this.viewAlphaChecker.getLeft() - Math.floor(this.viewAlphaCursor.getMeasuredWidth() / 2) - this.viewContainer.getPaddingLeft());
		layoutParams.topMargin = (int) ((this.viewAlphaChecker.getTop() + y) - Math.floor(this.viewAlphaCursor.getMeasuredHeight() / 2) - this.viewContainer.getPaddingTop());

		this.viewAlphaCursor.setLayoutParams(layoutParams);
	}

	private int getColor() {
		final int argb = Color.HSVToColor(currentColorHsv);
		return alpha << 24 | (argb & 0x00ffffff);
	}

	private float getHue() {
		return currentColorHsv[0];
	}

	private float getAlpha() {
		return this.alpha;
	}

	private float getSat() {
		return currentColorHsv[1];
	}

	private float getVal() {
		return currentColorHsv[2];
	}

	private void setHue(float hue) {
		currentColorHsv[0] = hue;
	}

	private void setSat(float sat) {
		currentColorHsv[1] = sat;
	}

	private void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	private void setVal(float val) {
		currentColorHsv[2] = val;
	}

	public void show() {
		dialog.show();
	}
	private void updateAlphaView() {
		final GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {
		Color.HSVToColor(currentColorHsv), 0x0
		});
        setLockBackground(gd);
	}
	private void setLockBackground(Drawable b) {
		try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                viewAlphaOverlay.setBackground(b);
            else//noinspection deprecation
                viewAlphaOverlay.setBackgroundDrawable(b);
		}catch (Exception e){e.printStackTrace();}
	}
}
