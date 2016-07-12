package edu.scu.vbhadana.photonotes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class TouchDrawView extends ImageView {
    Paint paint = new Paint();
    final List<Point> points = new ArrayList<>();

    public TouchDrawView(Context context) {
        super(context);
        paint.setColor(Color.RED);
    }

    public TouchDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.RED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = new Point();
        point.x = (int)event.getX();
        point.y = (int)event.getY();
        points.add(point);

        invalidate();

        Log.d("Vidushi", "point: " + point.x + "," + point.y + ", action: " + event.getAction());
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Point point : points) {
            canvas.drawCircle(point.x, point.y, 10, paint);
        }
    }

    public List<Point> getPoints() {
        return new ArrayList<>(points);
    }

    public Paint getPaint() {
        return paint;
    }

    public void clear() {
        points.clear();
        invalidate();
    }

}
