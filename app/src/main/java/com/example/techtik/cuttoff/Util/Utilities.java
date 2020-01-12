package com.example.techtik.cuttoff.Util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;


import java.util.Locale;

public class Utilities {
    public static Locale sLocale;

    // Constants
    public static final long LONG_VIBRATE_LENGTH = 500;
    public static final long SHORT_VIBRATE_LENGTH = 20;
    public static final long DEFAULT_VIBRATE_LENGTH = 100;

    /**
     * Checks for granted permission but by a single string (single permission)
     *
     * @param permission
     * @return boolean
     */
    public static boolean checkPermissionsGranted(Context context, String permission) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(
                context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check for permissions by a given list
     *
     * @param permissions
     * @return boolean
     */
    public static boolean checkPermissionsGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (!checkPermissionsGranted(context, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check for permissions by a given list
     *
     * @param grantResults
     * @return boolean
     */
    public static boolean checkPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    }



    public static void setUpLocale(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Utilities.sLocale = context.getResources().getSystem().getConfiguration().getLocales().get(0);
        } else {
            Utilities.sLocale = context.getResources().getSystem().getConfiguration().locale;
        }
    }

    /**
     * Vibrate the phone for {@code DEFAULT_VIBRATE_LENGTH} milliseconds
     */
    public static void vibrate(Context context) {
        vibrate(context, DEFAULT_VIBRATE_LENGTH);
    }

    /**
     * Vibrate the phone
     *
     * @param millis the amount of milliseconds to vibrate the phone for.
     */
    public static void vibrate(Context context, long millis) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(millis);
        }
    }

    /**
     * Format a given phone number to a readable string for the user
     *
     * @param phoneNumber the number to format
     * @return the formatted number
     */
    public static String formatPhoneNumber(String phoneNumber) {

        if (phoneNumber == null) return null;
        phoneNumber = normalizePhoneNumber(phoneNumber);

        Phonenumber.PhoneNumber formattedNumber = null;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            formattedNumber = phoneUtil.parse(phoneNumber, sLocale.getCountry());
        } catch (NumberParseException e) {

        }

        // return the number
        if (formattedNumber == null) return phoneNumber;
        else {
            PhoneNumberUtil.PhoneNumberFormat format;
            if (phoneUtil.getRegionCodeForCountryCode(formattedNumber.getCountryCode()).equals(sLocale.getCountry()))
                format = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;
            else
                format = PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;

            return phoneUtil.format(formattedNumber, format);
        }
    }

    public static String normalizePhoneNumber(String phoneNumber) {
        return PhoneNumberUtil.normalizeDiallableCharsOnly(phoneNumber);
    }

}
