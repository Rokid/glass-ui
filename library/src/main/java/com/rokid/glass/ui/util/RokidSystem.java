package com.rokid.glass.ui.util;

import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.Matrix;
import android.text.TextUtils;


/**
 * @author jian.yang
 * @date 2019/3/14
 */

public class RokidSystem {
    private final static String ALIGNMENT_LEFT = "ro.rokid.alignment.left";
    private final static String ALIGNMENT_TOP = "ro.rokid.alignment.top";
    private final static String ALIGNMENT_RIGHT = "ro.rokid.alignment.right";
    private final static String ALIGNMENT_BOTTOM = "ro.rokid.alignment.bottom";

    private final static String ALIGNMENT_LEFT_HD = "ro.rokid.fhdalignment.left";
    private final static String ALIGNMENT_TOP_HD = "ro.rokid.fhdalignment.top";
    private final static String ALIGNMENT_RIGHT_HD = "ro.rokid.fhdalignment.right";
    private final static String ALIGNMENT_BOTTOM_HD = "ro.rokid.fhdalignment.bottom";

    private final static String ALIGNMENT_LEFT_2K = "ro.rokid.2kalignment.left";
    private final static String ALIGNMENT_TOP_2K = "ro.rokid.2kalignment.top";
    private final static String ALIGNMENT_RIGHT_2K = "ro.rokid.2kalignment.right";
    private final static String ALIGNMENT_BOTTOM_2K = "ro.rokid.2kalignment.bottom";

    private final static String HARDWARE_VERSION = "ro.rokid.hardware.version";
    private final static int BASE_WIDTH = 1280;
    private final static int BASE_HEIGHT = 720;

    private final static int BASE_WIDTH_720P = 1280;
    private final static int BASE_HEIGHT_720P = 720;

    private final static int BASE_WIDTH_HD = 1920;
    private final static int BASE_HEIGHT_HD = 1080;

//    private final static int BASE_WIDTH_2K = 2048;
//    private final static int BASE_HEIGHT_2K = 1536;

    /**
     * use getWindowRect method
     *
     * @param previewWidth
     * @param previewHeight
     * @param windowRect
     * @return
     */
    @Deprecated
    public static Rect getWindowRect2K(final int previewWidth, final int previewHeight, final Rect windowRect) {
        return getWindowRect(previewWidth, previewHeight, windowRect);
    }

    /**
     * 根据LCD屏幕的rect，获取到preview的区域rect
     *
     * @param previewWidth
     * @param previewHeight
     * @param windowRect
     * @return
     */
    public static Rect getWindowRect(final int previewWidth, final int previewHeight, final Rect windowRect) {
        RectF rectF = getAlignmentRectF(previewWidth);
        if (null == rectF) {
            return null;
        }

        int width = ContextUtil.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        int height = ContextUtil.getApplicationContext().getResources().getDisplayMetrics().heightPixels;


//        int width = BaseLibrary.getInstance().getScreenSize().first;
//        int height = BaseLibrary.getInstance().getScreenSize().second;

        float w = ((rectF.right - rectF.left) * previewWidth);
        float h = ((rectF.bottom - rectF.top) * previewHeight);

        int left = (int) ((windowRect.left * w) * 1.0f / width + rectF.left * previewWidth);
        int top = (int) ((windowRect.top * h) * 1.0f / height + rectF.top * previewHeight);
        int right = (int) ((windowRect.right * w) * 1.0f / width + rectF.left * previewWidth);
        int bottom = (int) ((windowRect.bottom * h) * 1.0f / height + rectF.top * previewHeight);

        return new Rect(left, top, right, bottom);
    }

    /**
     * use getAlignmentRect method
     *
     * @param previewWidth
     * @param previewHeight
     * @param previewRect
     * @return
     */
    @Deprecated
    public static Rect getAlignmentRect2K(final int previewWidth, final int previewHeight, final Rect previewRect) {
        return getAlignmentRect(previewWidth, previewHeight, previewRect);
    }

    /**
     * 根据preview的rect，获取到映射到LCD屏幕的区域
     *
     * @param previewWidth
     * @param previewHeight
     * @param previewRect
     * @return
     */
    public static Rect getAlignmentRect(final int previewWidth, final int previewHeight, final Rect previewRect) {
        RectF rectF = getAlignmentRectF(previewWidth);
        if (null == rectF) {
            return null;
        }

        float w = ((rectF.right - rectF.left) * previewWidth);
        float h = ((rectF.bottom - rectF.top) * previewHeight);

        int width = ContextUtil.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        int height = ContextUtil.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
//        if (type == Alignment.alignHD) {
//            width = BASE_WIDTH_HD;
//            height = BASE_HEIGHT_HD;
//        } else {
//            width = BASE_WIDTH_720P;
//            height = BASE_HEIGHT_720P;
//        }

        int left = (int) ((previewRect.left - rectF.left * previewWidth) * 1.0f / w * width);
        int top = (int) ((previewRect.top - rectF.top * previewHeight) * 1.0f / h * height);
        int right = (int) ((previewRect.right - rectF.left * previewWidth) * 1.0f / w * width);
        int bottom = (int) ((previewRect.bottom - rectF.top * previewHeight) * 1.0f / h * height);

        return new Rect(left, top, right, bottom);
//        return new Rect((int) ((previewRect.left - rect.left) * 1.0 / w * previewWidth),
//                (int) ((previewRect.top - rect.top) * 1.0 / h * previewHeight),
//                (int) ((previewRect.right - rect.left) * 1.0 / w * previewWidth),
//                (int) ((previewRect.bottom - rect.top) * 1.0 / h * previewHeight));
    }

    /**
     * 获取系统的Alignment百分比
     * 真实区域在虚拟世界的比例
     *
     * @return
     */
    public static RectF getAlignmentPercent(final int width, final int height) {
        Rect rect;
        if (width == BASE_WIDTH_HD) {
            rect = getAlignmentBaseRectHD();
        } else {
            rect = getAlignmentBaseRect();
        }
        return new RectF(rect.left * 1.0f / width, rect.top * 1.0f / height,
                rect.right * 1.0f / width, rect.bottom * 1.0f / height);
    }

    private static RectF getAlignmentRectF(final int previewWidth) {
        @Alignment.type int type = getBenefitResolution(previewWidth);
        int width;
        int height;

        if (type == Alignment.alignHD) {
            if (noAlignmentHD()) {
                return null;
            }
            width = BASE_WIDTH_HD;
            height = BASE_HEIGHT_HD;
        } else {
            if (noAlignment()) {
                return null;
            }
            width = BASE_WIDTH_720P;
            height = BASE_HEIGHT_720P;
        }

        return getAlignmentPercent(width, height);
    }

    private static @Alignment.type
    int getBenefitResolution(final int width) {
//        int close2K = Math.abs(BASE_WIDTH_2K - width);
        int closeHD = Math.abs(BASE_WIDTH_HD - width);
        int close720p = Math.abs(BASE_WIDTH - width);

        return closeHD < close720p ? Alignment.alignHD : Alignment.align720p;
    }

    /**
     * 获取不同 glass 下的 alignment 参数
     *
     * @return
     */
    public static Rect getAlignmentBaseRect() {
        return new Rect(toInt(getSystemProperty(ALIGNMENT_LEFT)),
                toInt(getSystemProperty(ALIGNMENT_TOP)),
                toInt(getSystemProperty(ALIGNMENT_RIGHT)),
                toInt(getSystemProperty(ALIGNMENT_BOTTOM)));

    }

    public static Rect getAlignmentBaseRectHD() {
        return new Rect(toInt(getSystemProperty(ALIGNMENT_LEFT_HD)),
                toInt(getSystemProperty(ALIGNMENT_TOP_HD)),
                toInt(getSystemProperty(ALIGNMENT_RIGHT_HD)),
                toInt(getSystemProperty(ALIGNMENT_BOTTOM_HD)));

    }

    /**
     * 获取不同 glass 下的 alignment 参数
     *
     * @return
     */
    private static Rect getAlignmentBaseRect2K() {
        return new Rect(toInt(getSystemProperty(ALIGNMENT_LEFT_2K)),
                toInt(getSystemProperty(ALIGNMENT_TOP_2K)),
                toInt(getSystemProperty(ALIGNMENT_RIGHT_2K)),
                toInt(getSystemProperty(ALIGNMENT_BOTTOM_2K)));

    }

    /**
     * 获取OpticalSeeThrough眼镜的 3D alignment ProjectionMatrix
     * at present defaults for rokid glass 1, landscape, opengl es
     *
     * @return opengl es column major ProjectionMatrix, for rokid glass 1 landscape
     */
    public static float[] getProjectionMatrix_OpticalSeeThrough() {

        float rawProjectionMatrixGL2[] = {5.2f, 0.00f, 0, 0, 0, 9f, 0.0f, 0, 0.001f, 0.004f, -1, -1, 0, 0, -0.02f, 0};//for default lands

        double tmp[] = {1.0, 0.011, -0.001, 0.0, -0.011, 0.994, 0.047, 0.0, 0.002, -0.047, 0.994, 0.0, -35.4, 10.4, -55.4, 1.0};
        float eyeAdjustmentGL2[] = new float[16];
        for (int i = 0; i < 16; i++) {
            eyeAdjustmentGL2[i] = (float) tmp[i];
        }

        float projectionMatrix[] = new float[16];
        Matrix.multiplyMM(projectionMatrix, 0, rawProjectionMatrixGL2, 0, eyeAdjustmentGL2, 0);

        return projectionMatrix;

    }

    /**
     * 获取glass版本
     *
     * @return dvt or evt
     */
    public static String getHardwareVersion() {
        return getSystemProperty(HARDWARE_VERSION);
    }

    private static int toInt(final String value) {
        return !TextUtils.isEmpty(value) && TextUtils.isDigitsOnly(value)
                ? Integer.parseInt(value) : 0;
    }

    public static String getSystemProperty(String key) {
        String value = null;

        try {
            value = (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    private static boolean noAlignment() {
        return TextUtils.isEmpty(getSystemProperty(ALIGNMENT_LEFT));
    }

    private static boolean noAlignmentHD() {
        return TextUtils.isEmpty(getSystemProperty(ALIGNMENT_LEFT_HD));
    }

    private static boolean noAlignment2K() {
        return TextUtils.isEmpty(getSystemProperty(ALIGNMENT_LEFT_2K));
    }
}
