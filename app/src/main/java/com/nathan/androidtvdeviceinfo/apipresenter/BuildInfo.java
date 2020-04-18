package com.nathan.androidtvdeviceinfo.apipresenter;

import android.os.Build;

public final class BuildInfo {

    /** Value used for when a build property is unknown. */
    public static final String UNKNOWN = "unknown";

    /** Either a changelist number, or a label like "M4-rc20". */
    public static final String ID = Build.ID;

    /** A build ID string meant for displaying to the user */
    public static final String DISPLAY = Build.DISPLAY;

    /** The name of the overall product. */
    public static final String PRODUCT = Build.PRODUCT;

    /** The name of the industrial design. */
    public static final String DEVICE = Build.DEVICE;

    /** The name of the underlying board, like "goldfish". 品牌*/
    public static final String BOARD = Build.BOARD;

    /** The manufacturer of the product/hardware. */
    public static final String MANUFACTURER = Build.MANUFACTURER;

    /** The consumer-visible brand with which the product/hardware will be associated, if any.品牌 */
    public static final String BRAND = Build.BRAND;

    /** The end-user-visible name for the end product.型号 */
    public static final String MODEL = Build.MODEL;

    /** The system bootloader version number. */
    public static final String BOOTLOADER = Build.BOOTLOADER;

    /** The name of the hardware (from the kernel command line or /proc). */
    public static final String HARDWARE = Build.HARDWARE;

    /**
     * An ordered list of ABIs supported by this device. The most preferred ABI is the first
     * element in the list.
     *
     * See {@link #SUPPORTED_32_BIT_ABIS} and {@link #SUPPORTED_64_BIT_ABIS}.
     */
    public static final String[] SUPPORTED_ABIS = Build.SUPPORTED_ABIS;

    /**
     * An ordered list of <b>32 bit</b> ABIs supported by this device. The most preferred ABI
     * is the first element in the list.
     *
     * See {@link #SUPPORTED_ABIS} and {@link #SUPPORTED_64_BIT_ABIS}.
     */
    public static final String[] SUPPORTED_32_BIT_ABIS = Build.SUPPORTED_32_BIT_ABIS;

    /**
     * An ordered list of <b>64 bit</b> ABIs supported by this device. The most preferred ABI
     * is the first element in the list.
     *
     * See {@link #SUPPORTED_ABIS} and {@link #SUPPORTED_32_BIT_ABIS}.
     */
    public static final String[] SUPPORTED_64_BIT_ABIS = Build.SUPPORTED_64_BIT_ABIS;

    /** Various version strings. */
    public static class VERSION {
        /**
         * The internal value used by the underlying source control to
         * represent this build.  E.g., a perforce changelist number
         * or a git hash.
         */
        public static final String INCREMENTAL = Build.VERSION.INCREMENTAL;

        /**
         * The user-visible version string.  E.g., "1.0" or "3.4b5" or "bananas".
         *
         * This field is an opaque string. Do not assume that its value
         * has any particular structure or that values of RELEASE from
         * different releases can be somehow ordered.
         * 系统版本
         */
        public static final String RELEASE = Build.VERSION.RELEASE;

        /**
         * The base OS build the product is based on.
         */
        public static final String BASE_OS = Build.VERSION.BASE_OS;

        /**
         * The user-visible security patch level.
         */
        public static final String SECURITY_PATCH = Build.VERSION.SECURITY_PATCH;



        /**
         * The SDK version of the software currently running on this hardware
         * device. This value never changes while a device is booted, but it may
         * increase when the hardware manufacturer provides an OTA update.
         * <p>
         * Possible values are defined in {@link Build.VERSION_CODES}.
         */
        public static final int SDK_INT = Build.VERSION.SDK_INT;



        /**
         * The developer preview revision of a prerelease SDK. This value will always
         * be <code>0</code> on production platform builds/devices.
         *
         * <p>When this value is nonzero, any new API added since the last
         * officially published {@link #SDK_INT API level} is only guaranteed to be present
         * on that specific preview revision. For example, an API <code>Activity.fooBar()</code>
         * might be present in preview revision 1 but renamed or removed entirely in
         * preview revision 2, which may cause an app attempting to call it to crash
         * at runtime.</p>
         *
         * <p>Experimental apps targeting preview APIs should check this value for
         * equality (<code>==</code>) with the preview SDK revision they were built for
         * before using any prerelease platform APIs. Apps that detect a preview SDK revision
         * other than the specific one they expect should fall back to using APIs from
         * the previously published API level only to avoid unwanted runtime exceptions.
         * </p>
         */
        public static final int PREVIEW_SDK_INT = Build.VERSION.PREVIEW_SDK_INT;


        /**
         * The current development codename, or the string "REL" if this is
         * a release build.
         */
        public static final String CODENAME = Build.VERSION.CODENAME;

    }

}
