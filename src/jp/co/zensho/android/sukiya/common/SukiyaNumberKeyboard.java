
package jp.co.zensho.android.sukiya.common;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class SukiyaNumberKeyboard {
    /** A link to the KeyboardView that is used to render this CustomKeyboard. */
    private KeyboardView mKeyboardView;
    /** A link to the activity that hosts the {@link #mKeyboardView}. */
    private Activity mHostActivity;
    
    private boolean mPartCodeMode;


    /** The key (code) handler. */
    private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            // Get the EditText and its Editable
            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
            if (focusCurrent == null || focusCurrent.getClass() != EditText.class)
                return;
            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();
            // Apply the key to the edittext
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                hideCustomKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DONE) {
                hideCustomKeyboard();
            } else if (primaryCode == -7) {
                editable.clear();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
                if (start > 0) {
                	if (mPartCodeMode) {
                		editable.delete(start - 1, start);
                	} else {
	                    StringBuilder builder = new StringBuilder(editable);
	                    builder.delete(start - 1, start);
	                    if (acceptNumber(builder.toString(), start)) {
	                        editable.delete(start - 1, start);
	                    }
                	}
                }
            } else { // insert character
            	if (mPartCodeMode && primaryCode != 46) {
	                editable.insert(start, Character.toString((char) primaryCode));
            	} else {
	                StringBuilder builder = new StringBuilder(editable);
	                builder.insert(start, Character.toString((char) primaryCode));
	                if (acceptNumber(builder.toString(), start)) {
	                    editable.insert(start, Character.toString((char) primaryCode));
	                }
            	}
            }
        }

        @Override
        public void onPress(int arg0) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeUp() {
        }
    };

    /**
     * Create a custom keyboard, that uses the KeyboardView (with resource id
     * <var>viewid</var>) of the <var>host</var> activity, and load the keyboard
     * layout from xml file <var>layoutid</var> (see {@link Keyboard} for
     * description). Note that the <var>host</var> activity must have a
     * <var>KeyboardView</var> in its layout (typically aligned with the bottom
     * of the activity). Note that the keyboard layout xml file may include key
     * codes for navigation; see the constants in this class for their values.
     * Note that to enable EditText's to use this custom keyboard, call the
     * {@link #registerEditText(int)}.
     * 
     * @param host The hosting activity.
     * @param viewid The id of the KeyboardView.
     * @param layoutid The id of the xml file containing the keyboard layout.
     */
    public SukiyaNumberKeyboard(Activity host, int viewid, int layoutid) {
        mHostActivity = host;
        mKeyboardView = (KeyboardView) mHostActivity.findViewById(viewid);
        mKeyboardView.setKeyboard(new Keyboard(mHostActivity, layoutid));
        mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview
                                                // balloons
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // Hide the standard keyboard initially
        mHostActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        mPartCodeMode = false;
    }

    /** Returns whether the CustomKeyboard is visible. */
    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * Make the CustomKeyboard visible, and hide the system keyboard for view v.
     */
    public void showCustomKeyboard(View v) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if (v != null)
            ((InputMethodManager) mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /** Make the CustomKeyboard invisible. */
    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    /**
     * Register <var>EditText<var> with resource id <var>resid</var> (on the
     * hosting activity) for using this custom keyboard.
     * 
     * @param resid The resource id of the EditText that registers to the custom
     *            keyboard.
     */
    public void registerEditText(int resid) {
        // Find the EditText 'resid'
        EditText edittext = (EditText) mHostActivity.findViewById(resid);
        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom
            // keyboard when the edit box gets focus, but also hide it when the
            // edit box loses focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showCustomKeyboard(v);
                else
                    hideCustomKeyboard();
            }
        });
        edittext.setOnClickListener(new OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom
            // keyboard again, by tapping on an edit box that already had focus
            // (but that had the keyboard hidden).
            @Override
            public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way:
        // 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a
        // cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        edittext.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                if (!(isCustomKeyboardVisible())) {
                    showCustomKeyboard(v);
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Layout layout = ((EditText) v).getLayout();
                        float x = event.getX() + edittext.getScrollX();
                        int offset = layout.getOffsetForHorizontal(0, x);
                        if (offset > 0) {
                            if (x > layout.getLineMax(0)) {
                                edittext.setSelection(offset);     // touch was at the end of the text
                            } else {
                                edittext.setSelection(offset - 1);
                            }
                        } else {
                            edittext.setSelection(0);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        layout = ((EditText) v).getLayout();
                        x = event.getX() + edittext.getScrollX();
                        offset = layout.getOffsetForHorizontal(0, x);
                        if (offset > 0) {
                            if (x > layout.getLineMax(0)) {
                                edittext.setSelection(offset);     // Touchpoint was at the end of the text
                            } else {
                                edittext.setSelection(offset - 1);
                            }
                        } else {
                            edittext.setSelection(0);
                        }
                        break;
 
                }
                
                int inType = edittext.getInputType(); // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard
                                                            // keyboard
                edittext.onTouchEvent(event); // Call native handler
                edittext.setInputType(inType); // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }
    
    private boolean acceptNumber(String strVal, int index) {
        if (StringUtils.isEmpty(strVal)) {
            return true;
        }
        try {
            Double dblVal = Double.parseDouble(strVal);
            if (dblVal < 1000) {
                final char chrDot = 46;
                int dotIndex = strVal.indexOf(chrDot);
                if (dotIndex > -1 && index > dotIndex && (strVal.length() - dotIndex) > 3) {
                    return false;
                }
                if (strVal.length() > 3 && dotIndex == -1) {
                    return false;
                } else if (dotIndex > 3) {
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

	public boolean ismPartCodeMode() {
		return mPartCodeMode;
	}

	public void setmPartCodeMode(boolean mPartCodeMode) {
		this.mPartCodeMode = mPartCodeMode;
	}
}
