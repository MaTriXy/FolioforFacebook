package com.creativtrendz.folio.preferences;

import com.creativetrends.folio.app.R;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;




public class SwitchPreferenceCompat extends CheckBoxPreference {

	public SwitchPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public SwitchPreferenceCompat(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SwitchPreferenceCompat(Context context) {
		super(context);
		init();
	}

	private void init() {
		setWidgetLayoutResource(R.layout.pref_widget_switch);
	}

}