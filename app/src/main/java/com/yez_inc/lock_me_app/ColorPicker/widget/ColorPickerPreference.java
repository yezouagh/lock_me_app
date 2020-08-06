package com.yez_inc.lock_me_app.ColorPicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.yez_inc.lock_me_app.ColorPicker.ColorDialog;
import com.yez_inc.lock_me_app.R;

public class ColorPickerPreference extends Preference {
	private final boolean supportsAlpha;
	private int value;
	public ColorPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerPreference);
		supportsAlpha = ta.getBoolean(R.styleable.ColorPickerPreference_supportsAlpha, false);
		setWidgetLayoutResource(R.layout.color_picker_pref_widget);
		ta.recycle();
	}
	@Override protected void onBindView(View view) {
		super.onBindView(view);
		// Set our custom views inside the layout
		final View box = view.findViewById(R.id.pref_widget_box);
		if (box != null) {
			box.setBackgroundColor(value);
		}
	}
	@Override protected void onClick() {
		new ColorDialog(getContext(), value, supportsAlpha, new ColorDialog.OnColorPickerListener() {
			@Override public void onOk(ColorDialog dialog, int color) {
				if (!callChangeListener(color)) return; // They don't want the value to be set
				value = color;
				persistInt(value);
				notifyChanged();
			}
			@Override public void onCancel(ColorDialog dialog) {
				// nothing to do
			}
		}).show();
	}
	@Override protected Object onGetDefaultValue(TypedArray a, int index) {
		// This preference type's value type is Integer, so we read the default value from the attributes as an Integer.
		return a.getInteger(index, 0);
	}
	@Override protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		if (restoreValue) { // Restore state
			value = getPersistedInt(value);
		} else { // Set state
			int value = (Integer) defaultValue;
			this.value = value;
			persistInt(value);
		}
	}
	@Override protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) return superState; // No need to save instance state since it's persistent
		final SavedState myState = new SavedState(superState);
		myState.value = value;
		return myState;
	}
	@Override protected void onRestoreInstanceState(Parcelable state) {
		if (!state.getClass().equals(SavedState.class)) {
			// Didn't save state for us in onSaveInstanceState
			super.onRestoreInstanceState(state);
			return;
		}
		// Restore the instance state
		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		this.value = myState.value;
		notifyChanged();
	}
	private static class SavedState extends BaseSavedState {
		int value;
		SavedState(Parcel source) {
			super(source);
			value = source.readInt();
		}
		@Override public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(value);
		}
        SavedState(Parcelable superState) {
			super(superState);
		}

		@SuppressWarnings("unused") public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
