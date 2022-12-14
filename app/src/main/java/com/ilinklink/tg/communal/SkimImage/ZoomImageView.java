package com.ilinklink.tg.communal.SkimImage;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;

import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;


public class ZoomImageView extends AppCompatImageView {
	private float maxScale = 3f;
	private float minScale = 0.5f;
	private BaseSkimImageActivity context;

	private enum State {
		INIT, DRAG, ZOOM
	}

	private State state;

	private Matrix matrix;
	private float[] finalTransformation = new float[9];
	private PointF last = new PointF();
	private float currentScale = 1f;

	private int viewWidth;
	private int viewHeight;
	private float afterScaleDrawableWidth;
	private float afterScaleDrawableHeight;

	private ScaleGestureDetector scaleDetector;

	private GestureDetector doubleTapDetecture;

	// private Context context;

	public ZoomImageView(BaseSkimImageActivity context) {
		super(context);
		setUp(context);
		// this.context = context;
		this.context = context;
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setUp(context);
	}

	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setUp(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		viewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
		viewHeight = View.MeasureSpec.getSize(heightMeasureSpec);

		// Set up drawable at first load
		if (hasDrawable()) {
			resetImage();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean flag1 = scaleDetector.onTouchEvent(event);
		boolean flag2 = doubleTapDetecture.onTouchEvent(event);
		if (flag1 || flag2) {
			PointF current = new PointF(event.getX(), event.getY());

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				last.set(current);
				state = State.DRAG;
				break;

			case MotionEvent.ACTION_MOVE:
				if (state == State.DRAG) {
					drag(current);
					last.set(current);
				}
				break;

			case MotionEvent.ACTION_UP:
				if (currentScale < 1f) {// ??????????????1?? ??????????????????
					resetImage();
					currentScale = 1f;
					state = State.INIT;
				}

				break;

			case MotionEvent.ACTION_POINTER_UP:
				state = State.INIT;
				break;
			}

			setImageMatrix(matrix);
			invalidate();
			return true;
		}
		return false;
	}

	/**
	 * Set up the class. Method called by constructors.
	 * 
	 * @param context
	 */
	private void setUp(Context context) {
		super.setClickable(false);
		matrix = new Matrix();
		state = State.INIT;
		scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		doubleTapDetecture = new GestureDetector(context, new GestureListener());
		setScaleType(ImageView.ScaleType.MATRIX);
	}

	private void resetImage() {

		// Scale Image
		float scale = getScaleForDrawable();
		matrix.setScale(scale, scale);

		// Center Image
		float marginY = ((float) viewHeight - (scale * getDrawable().getIntrinsicHeight())) / 2;
		float marginX = ((float) viewWidth - (scale * getDrawable().getIntrinsicWidth())) / 2;
		matrix.postTranslate(marginX, marginY);

		afterScaleDrawableWidth = (float) viewWidth - 2 * marginX;
		afterScaleDrawableHeight = (float) viewHeight - 2 * marginY;

		setImageMatrix(matrix);
	}

	/**
	 * Getter and setter for max/min scale. Default are min=1 and max=3
	 */

	public float getMaxScale() {
		return maxScale;
	}

	public void setMaxScale(float maxScale) {
		this.maxScale = maxScale;
	}

	public float getMinScale() {
		return minScale;
	}

	public void setMinScale(float minScale) {
		this.minScale = minScale;
	}

	/**
	 * Drag method
	 * 
	 * @param current
	 *            Current point to drag to.
	 */
	private void drag(PointF current) {
		float deltaX = getMoveDraggingDelta(current.x - last.x, viewWidth, afterScaleDrawableWidth * currentScale);
		float deltaY = getMoveDraggingDelta(current.y - last.y, viewHeight, afterScaleDrawableHeight * currentScale);
		matrix.postTranslate(deltaX, deltaY);
		limitDrag();
	}

	/**
	 * Scale method for zooming
	 * 
	 * @param focusX
	 *            X of center of scale
	 * @param focusY
	 *            Y of center of scale
	 * @param scaleFactor
	 *            scale factor to zoom in/out
	 */
	private void scale(float focusX, float focusY, float scaleFactor) {
		float lastScale = currentScale;
		float newScale = lastScale * scaleFactor;

		// Calculate next scale with resetting to max or min if required
		if (newScale > maxScale) {
			currentScale = maxScale;
			scaleFactor = maxScale / lastScale;
		} else if (newScale < minScale) {
			currentScale = minScale;
			scaleFactor = minScale / lastScale;
		} else {
			currentScale = newScale;
		}

		// Do scale
		if (requireCentering()) {
			matrix.postScale(scaleFactor, scaleFactor, (float) viewWidth / 2, (float) viewHeight / 2);
		} else
			matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);

		limitDrag();

	}

	/**
	 * This method permits to keep drag and zoom inside the drawable. It makes
	 * sure the drag is staying in bound.
	 */
	private void limitDrag() {
		matrix.getValues(finalTransformation);
		float finalXTransformation = finalTransformation[Matrix.MTRANS_X];
		float finalYTransformation = finalTransformation[Matrix.MTRANS_Y];

		float deltaX = getScaleDraggingDelta(finalXTransformation, viewWidth, afterScaleDrawableWidth * currentScale);
		float deltaY = getScaleDraggingDelta(finalYTransformation, viewHeight, afterScaleDrawableHeight * currentScale);

		matrix.postTranslate(deltaX, deltaY);
	}

	private float getScaleDraggingDelta(float delta, float viewSize, float contentSize) {
		float minTrans = 0;
		float maxTrans = 0;

		if (contentSize <= viewSize) {
			maxTrans = viewSize - contentSize;
		} else {
			minTrans = viewSize - contentSize;
		}

		if (delta < minTrans)
			return minTrans - delta;
		else if (delta > maxTrans)
			return maxTrans - delta;
		else
			return 0;
	}

	// Check if dragging is still possible if so return delta otherwise return 0
	// (do nothing)
	private float getMoveDraggingDelta(float delta, float viewSize, float contentSize) {
		if (contentSize <= viewSize) {
			return 0;
		}
		return delta;
	}

	private float getScaleForDrawable() {
		float scaleX = (float) viewWidth / (float) getDrawable().getIntrinsicWidth();
		float scaleY = (float) viewHeight / (float) getDrawable().getIntrinsicHeight();
		return Math.min(scaleX, scaleY);
	}

	private boolean hasDrawable() {
		return getDrawable() != null && getDrawable().getIntrinsicWidth() != 0
				&& getDrawable().getIntrinsicHeight() != 0;
	}

	private boolean requireCentering() {
		return afterScaleDrawableWidth * currentScale <= (float) viewWidth
				|| afterScaleDrawableHeight * currentScale <= (float) viewHeight;
	}

	private boolean isZoom() {
		return currentScale != 1f;
	}

	/**
	 * Listener for detecting scale.
	 * 
	 * @author jmartinez
	 */
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			state = State.ZOOM;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			scale(detector.getFocusX(), detector.getFocusY(), detector.getScaleFactor());
			return true;
		}
	}

	/**
	 * Listener for double tap detection
	 * 
	 * @author jmartinez
	 */
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (isZoom()) {
				resetImage();
				currentScale = 1f;
				state = State.INIT;
				return true;
			} else if (!isZoom()) {
				scale(e.getX(), e.getY(), maxScale);
				return true;
			}
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			if (context != null)
				context.onLongClick();
			super.onLongPress(e);
		}

	}

}
