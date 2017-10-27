package com.syezon.clean;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 旋转加载视图
 */

public class DotRotateLoadingView extends View {
    private RectF rectF;
    private Paint paint;
    private Path path;
    private int position;
    private Timer timer;
    public DotRotateLoadingView(Context context) {
        super(context);
        initParam();
    }

    public DotRotateLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParam();
    }

    public DotRotateLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParam();
    }

    private void initParam() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);
        rectF = new RectF();
        path = new Path();
        timer = new Timer();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        path.reset();
        float radius = Math.min(getWidth() /2, getHeight() / 2);
        path.addCircle(radius, radius, (float)(radius * 0.7), Path.Direction.CW);
        canvas.clipPath(path, Region.Op.DIFFERENCE);
        rectF.set(0, 0, 2 * radius, 2 * radius);

        for (int i = 0; i < 8; i++) {
            if(position % 8 == i){
                paint.setColor(Color.BLUE);
            }else{
                paint.setColor(Color.GRAY);
            }
            canvas.drawArc(rectF, -90 + 45 * i, 35, true, paint);
        }

    }

    public void startRotating(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(position < Integer.MAX_VALUE){
                    position ++;
                }else{
                    position = 0;
                }
                postInvalidate();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 150);

    }

    public void cleanRotating(){
        timer.cancel();
    }


}
