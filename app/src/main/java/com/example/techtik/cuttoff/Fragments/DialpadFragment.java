package com.example.techtik.cuttoff.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.techtik.cuttoff.Activity.widgets.DialpadView;
import com.example.techtik.cuttoff.Activity.widgets.DigitsEditText;
import com.example.techtik.cuttoff.Fragments.base.AbsBaseFragment;
import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.Util.CallManager;
import com.example.techtik.cuttoff.Util.PreferenceUtils;
import com.example.techtik.cuttoff.Util.Utilities;
import com.example.techtik.cuttoff.listener.AllPurposeTouchListener;
import com.example.techtik.cuttoff.viewmodel.SharedDialViewModel;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;


public class DialpadFragment extends AbsBaseFragment {
    private static final String TAG = DialpadFragment.class.getSimpleName();
    public static final String ARG_DIALER = "dialer";

    // Text
    private OnKeyDownListener mOnKeyDownListener = null;
    private SharedDialViewModel mViewModel;
    private String mNumberText = "";
    private PhoneNumberFormattingTextWatcher mPhoneNumberFormattingTextWatcher;

    // Booleans
    private boolean mIsDialer = true;
    private boolean mIsSilent = true;
    private boolean mIsNotVibrate = true;

    // Tone
    private ToneGenerator mToneGenerator;
    private final Object mToneGeneratorLock = new Object();
    // The length of DTMF tones in milliseconds
    private static final int TONE_LENGTH_MS = 150;
    private static final int TONE_LENGTH_INFINITE = -1;
    // The DTMF tone volume relative to other sounds in the stream
    private static final int TONE_RELATIVE_VOLUME = 80;
    // Stream type used to play the DTMF tones off call, and mapped to the volume control keys
    private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_DTMF;

    private final HashSet<View> mPressedDialpadKeys = new HashSet<View>(12);


    // Layouts
     TableLayout mNumbersTable;
     DialpadView mDialpadView;

    // Edit Texts
    DigitsEditText mDigits;

    // Buttons
    ImageView mCallButton;
    ImageView mDelButton;


    public static DialpadFragment newInstance(boolean isDialer) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_DIALER, isDialer);
        DialpadFragment fragment = new DialpadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceUtils.getInstance(getContext());
        Utilities.setUpLocale(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.dialpad_fragment, container, false);
        fragmentView.buildLayer();
        init(fragmentView);
        return fragmentView;
    }


    private void init(View view){
        mDigits=view.findViewById(R.id.digits_edit_text);
        mCallButton=view.findViewById(R.id.button_call);
        mDelButton=view.findViewById(R.id.button_delete);
        mNumbersTable=view.findViewById(R.id.dialpad);
        mDialpadView=view.findViewById(R.id.dialpad_view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onFragmentReady() {
        Bundle args = getArguments();
        if (args == null)
            throw new IllegalArgumentException("You must create this fragment with newInstance()");
        mIsDialer = args.getBoolean(ARG_DIALER);

        if (!mIsDialer) {
            mCallButton.setVisibility(View.GONE);
            mDelButton.setVisibility(View.GONE);
        } else {
            AllPurposeTouchListener swipeToDelListener = new AllPurposeTouchListener(getContext()) {
                @Override
                public void onSwipeLeft() {
                    delNum(mDelButton);
                }

                @Override
                public boolean onSingleTapUp(View v) {
//                    ((MainActivity) DialpadFragment.this.getActivity()).expandDialer(true);
                    return true;
                }
            };
            // TODO wtf should we do with all the swipes shit
//            mDigits.setOnTouchListener(swipeToDelListener);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(SharedDialViewModel.class);
        mViewModel.getNumber().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.equals(mNumberText)) {
//                setNumber(s);
                }
            }
        });

        mPhoneNumberFormattingTextWatcher = new PhoneNumberFormattingTextWatcher(Utilities.sLocale.getCountry());
        mDigits.addTextChangedListener(mPhoneNumberFormattingTextWatcher);
        mDigits.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setNumber(mDigits.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // if the mToneGenerator creation fails, just continue without it.  It is
        // a local audio signal, and is not as important as the dtmf tone itself.
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                try {
                    mToneGenerator = new ToneGenerator(DIAL_TONE_STREAM_TYPE, TONE_RELATIVE_VOLUME);
                } catch (RuntimeException e) {
                    mToneGenerator = null;
                }
            }
        }
        mPressedDialpadKeys.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTone();
        mPressedDialpadKeys.clear();
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator != null) {
                mToneGenerator.release();
                mToneGenerator = null;
            }
        }
    }

    // -- On Clicks -- //

    /**
     * Dialer buttons click handler
     *
     * @param view is the button number
     */
    @OnClick({R.id.key_0, R.id.key_1, R.id.key_2, R.id.key_3, R.id.key_4, R.id.key_5,
            R.id.key_6, R.id.key_7, R.id.key_8, R.id.key_9, R.id.key_star, R.id.key_hex})
    public void addChar(View view) {
        switch (view.getId()) {
            case R.id.key_1: {
                keyPressed(KeyEvent.KEYCODE_1);
                break;
            }
            case R.id.key_2: {
                keyPressed(KeyEvent.KEYCODE_2);
                break;
            }
            case R.id.key_3: {
                keyPressed(KeyEvent.KEYCODE_3);
                break;
            }
            case R.id.key_4: {
                keyPressed(KeyEvent.KEYCODE_4);
                break;
            }
            case R.id.key_5: {
                keyPressed(KeyEvent.KEYCODE_5);
                break;
            }
            case R.id.key_6: {
                keyPressed(KeyEvent.KEYCODE_6);
                break;
            }
            case R.id.key_7: {
                keyPressed(KeyEvent.KEYCODE_7);
                break;
            }
            case R.id.key_8: {
                keyPressed(KeyEvent.KEYCODE_8);
                break;
            }
            case R.id.key_9: {
                keyPressed(KeyEvent.KEYCODE_9);
                break;
            }
            case R.id.key_0: {
                keyPressed(KeyEvent.KEYCODE_0);
                break;
            }
            case R.id.key_hex: {
                keyPressed(KeyEvent.KEYCODE_POUND);
                break;
            }
            case R.id.key_star: {
                keyPressed(KeyEvent.KEYCODE_STAR);
                break;
            }
            default: {
                break;
            }
        }
        mPressedDialpadKeys.add(view);
    }

    /**
     * Deletes a number from the keypad's input when the delete button is clicked
     */
    @OnClick(R.id.button_delete)
    public void delNum(View view) {
        keyPressed(KeyEvent.KEYCODE_DEL);
    }

    /**
     * Calls the number in the keypad's input
     */
    @OnClick(R.id.button_call)
    public void call(View view) {
        CallManager.call(this.getContext(), mNumberText);
    }

    @OnClick(R.id.digits_edit_text)
    public void onDigitsClick(View view) {
        if (!isDigitsEmpty()) {
            mDigits.setCursorVisible(true);
        }
    }

    /**
     * Deletes the whole keypad's input when the delete button is long clicked
     */
    @OnLongClick(R.id.button_delete)
    public boolean delAllNum(View view) {
        setNumber("");
        return true;
    }

    /**
     * Starts a call to voice mail when the 1 button is long clicked
     */
//    @OnLongClick(R.id.key_1)
//    public boolean startVoiceMail(View view) {
//        if (!mIsDialer) return false;
//        return CallManager.callVoicemail(this.getContext());
//    }

    @OnLongClick(R.id.key_0)
    public boolean addPlus(View view) {
        keyPressed(KeyEvent.KEYCODE_PLUS);
        return mIsDialer;
    }


    /**
     * Act when a key is pressed (Plays tune and sets the text)
     *
     * @param keyCode
     */
    private void keyPressed(int keyCode) {
        updatePreferences();
        if (getView().getTranslationY() != 0) {
            return;
        }
        if (!mIsSilent) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_1:
                    playTone(ToneGenerator.TONE_DTMF_1);
                    break;
                case KeyEvent.KEYCODE_2:
                    playTone(ToneGenerator.TONE_DTMF_2);
                    break;
                case KeyEvent.KEYCODE_3:
                    playTone(ToneGenerator.TONE_DTMF_3);
                    break;
                case KeyEvent.KEYCODE_4:
                    playTone(ToneGenerator.TONE_DTMF_4);
                    break;
                case KeyEvent.KEYCODE_5:
                    playTone(ToneGenerator.TONE_DTMF_5);
                    break;
                case KeyEvent.KEYCODE_6:
                    playTone(ToneGenerator.TONE_DTMF_6);
                    break;
                case KeyEvent.KEYCODE_7:
                    playTone(ToneGenerator.TONE_DTMF_7);
                    break;
                case KeyEvent.KEYCODE_8:
                    playTone(ToneGenerator.TONE_DTMF_8);
                    break;
                case KeyEvent.KEYCODE_9:
                    playTone(ToneGenerator.TONE_DTMF_9);
                    break;
                case KeyEvent.KEYCODE_0:
                    playTone(ToneGenerator.TONE_DTMF_0);
                    break;
                case KeyEvent.KEYCODE_POUND:
                    playTone(ToneGenerator.TONE_DTMF_P);
                    break;
                case KeyEvent.KEYCODE_STAR:
                    playTone(ToneGenerator.TONE_DTMF_S);
                    break;
                default:
                    break;
            }
        }
        vibrate();
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        // Add the digit to mDigits
        mDigits.onKeyDown(keyCode, event);
        if (mOnKeyDownListener != null) mOnKeyDownListener.onKeyPressed(keyCode, event);

        // If the cursor is at the end of the text we hide it.
        final int length = mDigits.length();
        if (length == mDigits.getSelectionStart() && length == mDigits.getSelectionEnd()) {
            mDigits.setCursorVisible(false);
        }
    }

    // -- Setters -- //

    /**
     * Sets the input number to a given number
     * This function for now will be called due to list item click (contact)
     *
     * @param number
     */
    public void setNumber(String number) {
        mNumberText = number;
        mDigits.setText(number);
        mViewModel.setNumber(mNumberText);
    }

    // -- Utils -- //

    /**
     * Play the specified tone for the specified milliseconds
     * <p>
     * The tone is played locally, using the audio stream for phone calls.
     * Tones are played only if the "Audible touch tones" user preference
     * is checked, and are NOT played if the device is in silent mode.
     * <p>
     * The tone length can be -1, meaning "keep playing the tone." If the caller does so, it should
     * call stopTone() afterward.
     *
     * @param tone       a tone code from {@link ToneGenerator}
     * @param durationMs tone length.
     */
    private void playTone(int tone, int durationMs) {
        // Also do nothing if the phone is in silent mode.
        // We need to re-check the ringer mode for *every* playTone()
        // call, rather than keeping a local flag that's updated in
        // onResume(), since it's possible to toggle silent mode without
        // leaving the current activity (via the ENDCALL-longpress menu.)
        AudioManager audioManager =
                (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
                || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
            return;
        }
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {

                return;
            }
            // Start the new tone (will stop any playing tone)
            mToneGenerator.startTone(tone, durationMs);
        }
    }

    /**
     * Plays the specified tone for TONE_LENGTH_MS milliseconds.
     */
    private void playTone(int tone) {
        playTone(tone, TONE_LENGTH_MS);
    }

    /**
     * Stop the tone if it is played.
     */
    private void stopTone() {
        // if local tone playback is disabled, just return.
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                return;
            }
            mToneGenerator.stopTone();
        }
    }

    /**
     * Makes the phone vibrate
     */
    private void vibrate() {
        updatePreferences();
        if (!mIsNotVibrate)
            Utilities.vibrate(getContext(), Utilities.SHORT_VIBRATE_LENGTH);
    }

    /**
     * @return true if the widget with the phone number digits is empty.
     */
    private boolean isDigitsEmpty() {
        return mDigits.length() == 0;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) mDigits.requestFocus();
    }

    /**
     * Updates the preferences from the settings page
     */
    private void updatePreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        // TODO remember those preferences
//        mIsSilent = sp.getBoolean(getString(R.string.pref_is_silent_key), false);
//        mIsNotVibrate = sp.getBoolean(getString(R.string.pref_is_no_vibrate_key), false);
    }
    public void setDigitsCanBeEdited(final boolean canBeEdited) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.v("cd","bala");
                mDialpadView.setDigitsCanBeEdited(canBeEdited);
            }
        }, 2000);
    }

    public void setShowVoicemailButton(final boolean show) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDialpadView.setShowVoicemailButton(show);
            }
        }, 2000);
    }

    public void setOnKeyDownListener(OnKeyDownListener onKeyDownListener) {
        mOnKeyDownListener = onKeyDownListener;
    }
    public interface OnKeyDownListener {
        void onKeyPressed(int keyCode, KeyEvent event);
    }
}
