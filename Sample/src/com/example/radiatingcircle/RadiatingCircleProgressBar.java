package com.example.radiatingcircle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Radiating Circle Progress bar.
 * @see showProgress and hideProgress delegates here.
 * 
 * @author rohit
 *
 */
public class RadiatingCircleProgressBar extends View {

    // Constants
    private static final int FADDED_CIRCLE_RED_MATRIX = 51;
    private static final int FADDED_CIRCLE_GREEN_MATRIX = 181;
    private static final int FADDED_CIRCLE_BLUE_MATRIX = 229;
    
    private int centerBitmapMarginFromTop = 30;
    private int maxIncrementInOvalDimension = 100;

    private int intialDistanceLeftSideFromRectCenter = 10;
    private int initialDistanceTopSideFromRectCenter = 5;
    
    // Rect coordinates of the oval showing circle animation.
    // These will get modify to increase the rect accordingly 
    // to make the outward moving circle animation.
    private float faddedCircleTopX;
    private float faddedCircleTopY;
    private float faddeCircleBottomX;
    private float faddedCircleBottomY;
    
    private float faddedCircleXIncrement = 2.0f;
    private float faddedCircleYIncrement = 0.5f;
    
    // Top X and Y for the Center Bitmap Piece.
    // Circle will be drawn from the bottom mid point of the bitmap.
    private float bitmapTopX;
    private float bitmapTopY;

    private boolean isHighDensityDevice;
    
    private int faddedCircleIniatialOriginalAlpha = 100;
    // This will gradually decrease as animation progresses.
    // It will get reset once circle animation single round
    // completes.
    
    private int faddedCircleInitialAlpha = faddedCircleIniatialOriginalAlpha;
    
    
    // Bitmap holding center drawble.
    private Bitmap imageBitmap;

    // Variable to control the progress animation.
    private volatile boolean showProgress = false;

    // Varible holding the number of times change in dimension of circle occured.
    // This is to reset the coordinates when reached its limit.
    private int increment = 0;

    private Paint fadedCirclePaint;
    private Paint outerRingCirclePaint; 
    private Paint bitmapDrawingPaint;

    public RadiatingCircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RadiatingCircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadiatingCircleProgressBar(Context context) {
        super(context);
        init();
    }

    private void resetCordinate() {
        increment = 0;
        faddedCircleTopX = getWidth() / 2 - intialDistanceLeftSideFromRectCenter;
        faddedCircleTopY = imageBitmap.getHeight() - initialDistanceTopSideFromRectCenter + 
                           centerBitmapMarginFromTop;
        
        faddeCircleBottomX = getWidth() / 2 + intialDistanceLeftSideFromRectCenter;
        faddedCircleBottomY = imageBitmap.getHeight() + initialDistanceTopSideFromRectCenter + 
                              centerBitmapMarginFromTop;
        
        faddedCircleInitialAlpha = faddedCircleIniatialOriginalAlpha;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        prepareCenterBitmap();
        resetCordinate();
    };

    private void prepareCenterBitmap() {
        bitmapTopX = getWidth()/2 - imageBitmap.getWidth()/2;
        if(bitmapTopX < 0) bitmapTopX = 0;
        
        bitmapTopY = centerBitmapMarginFromTop;
    }
    
    private void init() {
        fadedCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fadedCirclePaint.setStyle(Paint.Style.FILL);
        fadedCirclePaint.setARGB(faddedCircleIniatialOriginalAlpha, 
                                 FADDED_CIRCLE_RED_MATRIX,
                                 FADDED_CIRCLE_GREEN_MATRIX,
                                 FADDED_CIRCLE_BLUE_MATRIX);

        outerRingCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerRingCirclePaint.setStyle(Paint.Style.STROKE);
        outerRingCirclePaint.setARGB(faddedCircleIniatialOriginalAlpha, 
                                     FADDED_CIRCLE_RED_MATRIX, 
                                     FADDED_CIRCLE_GREEN_MATRIX,
                                     FADDED_CIRCLE_BLUE_MATRIX);
        
        bitmapDrawingPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        bitmapDrawingPaint.setAntiAlias(true);
        bitmapDrawingPaint.setFilterBitmap(true);
        bitmapDrawingPaint.setDither(true);
     
        centerBitmapMarginFromTop = 10;
        maxIncrementInOvalDimension = 100;
        faddedCircleIniatialOriginalAlpha = maxIncrementInOvalDimension - 10;
        faddedCircleInitialAlpha = faddedCircleIniatialOriginalAlpha;     
        imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
        
        isHighDensityDevice = isHighScreenDensityDevice(getResources());
        
        if(!isHighDensityDevice) {
            faddedCircleXIncrement /= 2;
            faddedCircleYIncrement /= 2;
            intialDistanceLeftSideFromRectCenter /= 2;
            initialDistanceTopSideFromRectCenter /= 2;
        }
    }

    
    
    private void applyPhysics() {
        
        faddedCircleTopX -= faddedCircleXIncrement;
        faddedCircleTopY -= faddedCircleYIncrement;
        faddeCircleBottomX += faddedCircleXIncrement;
        faddedCircleBottomY += faddedCircleYIncrement;
        
        faddedCircleInitialAlpha--;
        
        if(faddedCircleInitialAlpha < 0 ) faddedCircleInitialAlpha = 0;
        
        fadedCirclePaint.setAlpha(faddedCircleInitialAlpha);
        outerRingCirclePaint.setAlpha(faddedCircleInitialAlpha);
        
        if (++increment > maxIncrementInOvalDimension) {
            resetCordinate();
        }
    }

    public void showProgress() {
        showProgress = true;
        resetCordinate();
        invalidate();
    }

    public void hideProgress() {
        showProgress = false;
        invalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(bitmapTopX == 0) {
            prepareCenterBitmap();
        }
        
        if (showProgress) {
            
            RectF rect = new RectF(faddedCircleTopX, 
                                   faddedCircleTopY, 
                                   faddeCircleBottomX, 
                                   faddedCircleBottomY);
            
            canvas.drawOval(rect, fadedCirclePaint);

            rect = new RectF(faddedCircleTopX - intialDistanceLeftSideFromRectCenter, 
                             faddedCircleTopY - initialDistanceTopSideFromRectCenter, 
                             faddeCircleBottomX + intialDistanceLeftSideFromRectCenter, 
                             faddedCircleBottomY + initialDistanceTopSideFromRectCenter);
            
            canvas.drawOval(rect, outerRingCirclePaint);
            
            canvas.drawBitmap(imageBitmap, bitmapTopX, bitmapTopY, bitmapDrawingPaint);            
            
            applyPhysics();
            invalidate();
            
        } else {
            canvas.drawBitmap(imageBitmap, bitmapTopX, bitmapTopY, bitmapDrawingPaint);
            resetCordinate();
        }
        
    }
    
    public static boolean isHighScreenDensityDevice(Resources res) {

        boolean result = true;

        switch (res.getDisplayMetrics().densityDpi) {
        case DisplayMetrics.DENSITY_LOW:
            result = false;
            break;
        case DisplayMetrics.DENSITY_MEDIUM:
            result = false;
            break;
        case DisplayMetrics.DENSITY_HIGH:
            result = true;
            break;
        case DisplayMetrics.DENSITY_XHIGH:
            result = true;
            break;
        case DisplayMetrics.DENSITY_XXHIGH:
            result = true;
            break;
        case DisplayMetrics.DENSITY_XXXHIGH:
            result = true;
            break;
        }

        return result;
    }
}