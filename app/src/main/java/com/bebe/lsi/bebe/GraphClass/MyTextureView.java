package com.bebe.lsi.bebe.GraphClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.bebe.lsi.bebe.R;
import com.handstudio.android.hzgrapherlib.canvas.GraphCanvasWrapper;
import com.handstudio.android.hzgrapherlib.path.GraphPath;

import java.util.WeakHashMap;

/**
 * Created by LSJ on 2015-08-28.
 */
public class MyTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    public DrawThread drawthread;
    private MyLineGraphVO mLineGraphVO = null;
    private Context context;
    private static final Object touchLock = new Object();
    private int width;
    private int height;
    private Object mLock = new Object();
    private SurfaceTexture mSurfaceTexture;
    boolean isRun = true;

    public MyTextureView(Context context, MyLineGraphVO vo) {
        super(context);
        this.context = context;
        this.mLineGraphVO = vo;
        init();
    }

    private void init() {
        this.setSurfaceTextureListener(this);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.width = width;
        this.height = height;
        isRun = true;
        synchronized (mLock) {
            mSurfaceTexture = surface;
            if (mSurfaceTexture != null)
            mLock.notify();
        }
        drawthread = new DrawThread();
        drawthread.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        synchronized (mLock) {
            try {
                isRun = false;
                drawthread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    class DrawThread extends Thread {
        boolean isDirty = true;
        int xLength;
        int yLength;
        int chartXLength;
        int chartYLength;
        Paint p;
        Paint pCircle;
        Paint miniCircle;
        Paint pLine;
        Paint pBaseLine;
        Paint pBaseLineX;
        Paint pBaseLineY;
        Paint pMarkText;
        float anim;
        boolean isAnimation;
        boolean isDrawRegion;
        long animStartTime;
        Bitmap bg;
        Canvas canvas;
        Context mCtx;

        public DrawThread() {
            this.xLength = width - (MyTextureView.this.mLineGraphVO.getPaddingLeft() + MyTextureView.this.mLineGraphVO.getPaddingRight() + MyTextureView.this.mLineGraphVO.getMarginRight());
            this.yLength = height - (MyTextureView.this.mLineGraphVO.getPaddingBottom() + MyTextureView.this.mLineGraphVO.getPaddingTop() + MyTextureView.this.mLineGraphVO.getMarginTop());
            this.chartXLength = width - (MyTextureView.this.mLineGraphVO.getPaddingLeft() + MyTextureView.this.mLineGraphVO.getPaddingRight());
            this.chartYLength = height - (MyTextureView.this.mLineGraphVO.getPaddingBottom() + MyTextureView.this.mLineGraphVO.getPaddingTop());
            this.p = new Paint();
            this.pCircle = new Paint();
            this.miniCircle = new Paint();
            this.pLine = new Paint();
            this.pBaseLine = new Paint();
            this.pBaseLineX = new Paint();
            this.pBaseLineY = new Paint();
            this.pMarkText = new Paint();
            this.anim = 0.0F;
            this.isAnimation = false;
            this.isDrawRegion = false;
            this.animStartTime = -1L;
            this.bg = null;
            this.mCtx = context;
            int size = MyTextureView.this.mLineGraphVO.getArrGraph().size();
        }

        @Override
        public void run() {
            // Latch the SurfaceTexture when it becomes available.  We have to wait for
            // the TextureView to create it.
            GraphCanvasWrapper graphCanvasWrapper = null;
            this.setPaint();
            this.isAnimation();
            this.isDrawRegion();
            this.animStartTime = System.currentTimeMillis();
            while (isRun) { // isRun Default true
                if (!this.isDirty) { // isDirty Default true
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException var13) {
                        var13.printStackTrace();
                    }
                } else {
                    Surface surface = null;
                    SurfaceTexture surfaceTexture = mSurfaceTexture;
                    if (surfaceTexture == null) {
                        Log.d("Graph", "ST null on entry");
                        return;
                    }
                    surface = new Surface(surfaceTexture);
                    Rect dirty = new Rect(0, 0, width, height);
                    canvas = surface.lockCanvas(dirty);
                    //
                    graphCanvasWrapper = new GraphCanvasWrapper(canvas, width, height, MyTextureView.this.mLineGraphVO.getPaddingLeft(), MyTextureView.this.mLineGraphVO.getPaddingBottom());
                    synchronized (MyTextureView.touchLock) {
                        try {
                            canvas.drawColor(getResources().getColor(R.color.graphBG));
                            if (this.bg != null) {
                                canvas.drawColor(getResources().getColor(R.color.graphBG));
                            }
                            //this.drawBaseLine(graphCanvasWrapper);
                            //graphCanvasWrapper.drawLine(0.0F, 0.0F, 0.0F, (float) this.chartYLength, this.pBaseLine);
                            //graphCanvasWrapper.drawLine(0.0F, 0.0F, (float) this.chartXLength, 0.0F, this.pBaseLine);
                            //this.drawXMark(graphCanvasWrapper);
                            //this.drawYMark(graphCanvasWrapper);
                            //this.drawXText(graphCanvasWrapper);
                            //this.drawYText(graphCanvasWrapper);
                            this.drawGraphRegion(graphCanvasWrapper);
                            this.drawGraph(graphCanvasWrapper);
                        } catch (Exception var15) {
                            var15.printStackTrace();
                        } finally {
                            if (surface != null) {
                                surface.unlockCanvasAndPost(canvas);
                            }
                        }
                    }
                    try {
                        Thread.sleep(0L);
                    } catch (InterruptedException var14) {
                        var14.printStackTrace();
                    }

                    this.calcTimePass();
                    surface.release();
                }
            }
        }

        public void stopPainting(){
            isRun = false;
            synchronized(this) {
                this.notify();
            }
        }

        private void setPaint() {
            this.p = new Paint();
            this.p.setFlags(1);
            this.p.setAntiAlias(true);
            this.p.setFilterBitmap(true);
            this.p.setColor(0xaaFFFFFF);
            this.p.setStrokeWidth(5.0F);
            this.p.setStyle(Paint.Style.STROKE);
            this.pCircle = new Paint();
            this.pCircle.setFlags(1);
            this.pCircle.setAntiAlias(true);
            this.pCircle.setFilterBitmap(true);
            this.pCircle.setColor(Color.WHITE); //
            this.pCircle.setStrokeWidth(3.0F);
            this.pCircle.setStyle(Paint.Style.STROKE);
            this.miniCircle = new Paint();
            this.miniCircle.setFlags(2);
            this.miniCircle.setAntiAlias(true);
            this.miniCircle.setFilterBitmap(true);
            this.miniCircle.setColor(Color.WHITE);
            this.miniCircle.setStrokeWidth(6.0F);
            this.miniCircle.setStyle(Paint.Style.FILL);
            this.pLine = new Paint();
            this.pLine.setFlags(1);
            this.pLine.setAntiAlias(true);
            this.pLine.setFilterBitmap(true);
            this.pLine.setShader(new LinearGradient(0.0F, 300.0F, 0.0F, 0.0F, -16777216, -1, Shader.TileMode.MIRROR));
            this.pBaseLine = new Paint();
            this.pBaseLine.setFlags(1);
            this.pBaseLine.setAntiAlias(true);
            this.pBaseLine.setFilterBitmap(true);
            this.pBaseLine.setColor(getResources().getColor(R.color.GraphLineColor));
            this.pBaseLine.setStrokeWidth(3.0F);
            this.pBaseLineX = new Paint();
            this.pBaseLineX.setFlags(1);
            this.pBaseLineX.setAntiAlias(true);
            this.pBaseLineX.setFilterBitmap(true);
            this.pBaseLineX.setColor(getResources().getColor(R.color.GraphLineColor));
            this.pBaseLineX.setStrokeWidth(2.0F);
            this.pBaseLineX.setStyle(Paint.Style.STROKE);
            // this.pBaseLineX.setPathEffect(new DashPathEffect(new float[]{10.0F, 5.0F}, 0.0F));
            this.pBaseLineY = new Paint();
            this.pBaseLineY.setFlags(1);
            this.pBaseLineY.setAntiAlias(true);
            this.pBaseLineY.setFilterBitmap(true);
            this.pBaseLineY.setColor(getResources().getColor(R.color.GraphLineColor));
            this.pBaseLineY.setStrokeWidth(2.0F);
            this.pBaseLineY.setStyle(Paint.Style.STROKE);
            // this.pBaseLineY.setPathEffect(new DashPathEffect(new float[]{10.0F, 5.0F}, 0.0F));
            this.pMarkText = new Paint();
            this.pMarkText.setFlags(1);
            this.pMarkText.setAntiAlias(true);
            this.pMarkText.setColor(Color.WHITE);
            this.pMarkText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        }


        private void calcTimePass() {
            if (this.isAnimation) {
                long curTime = System.currentTimeMillis();
                long gapTime = curTime - this.animStartTime;
                long animDuration = (long) MyTextureView.this.mLineGraphVO.getAnimation().getDuration();
                if (gapTime >= animDuration) {
                    gapTime = animDuration;
                    this.isDirty = false;
                }
                this.anim = (float) (MyTextureView.this.mLineGraphVO.getLegendArr().length * (float) gapTime / (float) animDuration);
            } else {
                this.isDirty = false;
            }

        }

        private void isAnimation() {
            if (MyTextureView.this.mLineGraphVO.getAnimation() != null) { // vo.setAnimation(new GraphAnimation(GraphAnimation.LINEAR_ANIMATION, GraphAnimation.DEFAULT_DURATION)); �̰ɷ� �ִϸ��̼� ������
                this.isAnimation = true;
            } else {        // �׷��� ���� true��ȯ
                this.isAnimation = false;
            }

        }

        private void isDrawRegion() {
            if (MyTextureView.this.mLineGraphVO.isDrawRegion()) { // ���ο��� �̰� true���ִ°� �ּ�ó�������� vo.setDrawRegion(true);
                this.isDrawRegion = true;
            } else {
                this.isDrawRegion = false;
            }

        }


        private void drawGraphRegion(GraphCanvasWrapper graphCanvas) {
            if (this.isDrawRegion) {
                if (this.isAnimation) {
                    this.drawGraphRegionWithAnimation(graphCanvas);
                } else {
                    this.drawGraphRegionWithoutAnimation(graphCanvas);
                }
            }

        }

        private void drawGraph(GraphCanvasWrapper graphCanvas) {
            if (this.isAnimation) {
                this.drawGraphWithAnimation(graphCanvas);
            } else {
                this.drawGraphWithoutAnimation(graphCanvas);
            }

        }


        private void drawBaseLine(GraphCanvasWrapper graphCanvas) {
            for (int i = 1; (MyTextureView.this.mLineGraphVO.getIncrement() * i) <= MyTextureView.this.mLineGraphVO.getMaxValue(); ++i) {
                float y = (float) (this.yLength * MyTextureView.this.mLineGraphVO.getIncrement() * i / (MyTextureView.this.mLineGraphVO.getMaxValue() - MyTextureView.this.mLineGraphVO.getMinValue()));
                //  yLength�� �Ƹ��� View�� ���� ����
                // ȭ�� ���� - (paddingBottom + marginTop + marginBottom)
                graphCanvas.drawLine(0.0F, y, (float) this.chartXLength, y, this.pBaseLineX);
                //Log.i("LineGraphView",(float)this.chartXLength+":"+y+":"+i);
            }
            for (int i = 1; i < 6; i++) { // MyLineGraphView.this.mLineGraphVO.getLegendArr().length �̰ɷ��ϸ� �׻� 5���� height ������ �׷����� ����
                float xGap = (float) (this.xLength / 5); // (MyLineGraphView.this.mLineGraphVO.getLegendArr().length-1) �̰ſ��� 4�� �ٲ� �׷��� �Ǽ��� height ���� ������ �Բ� 4���� ������ �׷���
                float x = xGap * (float) (i);
                //float x =(float)this.yLength * ((LineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[i] / (float)MyLineGraphView.this.mLineGraphVO.getMaxValue();
                graphCanvas.drawLine(x, 0.0F, x, (float) this.chartYLength, this.pBaseLineY);
                //Log.i("LineGraphView", (float) this.chartYLength +":"+x+":"+i);
            }

        }

        private void drawGraphRegionWithoutAnimation(GraphCanvasWrapper graphCanvas) throws NullPointerException {
            boolean isDrawRegion = MyTextureView.this.mLineGraphVO.isDrawRegion();
            for (int i = 0; i < MyTextureView.this.mLineGraphVO.getArrGraph().size(); ++i) {
                GraphPath regionPath = new GraphPath(width, height, MyTextureView.this.mLineGraphVO.getPaddingLeft(), MyTextureView.this.mLineGraphVO.getPaddingBottom());
                boolean firstSet = false;
                float x = 0.0F;
                float y = 0.0F;
                this.p.setColor(((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getColor());  // 그래프선의 색을 지정해줌
                this.pCircle.setColor(((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getColor());    // 동그라미 포인트 색깔 지정하는거
                float xGap = (float) (this.xLength / 6);//(float)(this.xLength / (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length - 1));
                // xGap은 전체화면에서 배열길이를 나눈값
                Log.d("Graph", xGap + " 한개의길이");

                for (int pBg = 0; pBg < 6; ++pBg) { //((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length;
                    if (pBg < ((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length) {
                        if (!firstSet) {// 처음 선을 그림
                            x = xGap * (float) pBg;
                            y = (float) this.yLength * ((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[pBg] / (float) MyTextureView.this.mLineGraphVO.getMaxValue();
                            regionPath.moveTo(x, 0.0F);
                            regionPath.lineTo(x, y);
                            firstSet = true;
                        } else { // 그다음은 다음 포인트를 찾아서 애니메이션으로 그림
                            x = xGap * (float) pBg;
                            y = (float) this.yLength * ((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[pBg] / (float) MyTextureView.this.mLineGraphVO.getMaxValue();
                            regionPath.lineTo(x, y);
                            // http://baramziny.tistory.com/75 넘어려워시발머야
                        }
                    }
                }

                if (isDrawRegion) {
                    regionPath.lineTo(x, 0.0F);
                    regionPath.lineTo(0.0F, 0.0F);
                    Paint var10 = new Paint();
                    var10.setFlags(1);
                    var10.setAntiAlias(true);
                    var10.setFilterBitmap(true);
                    var10.setStyle(Paint.Style.FILL);
                    var10.setColor(((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getColor());
                    graphCanvas.getCanvas().drawPath(regionPath, var10);
                }
            }

        }

        /*
                애니메이션을 넣어 그림을 그리는 메소드
         */
        private void drawGraphRegionWithAnimation(GraphCanvasWrapper graphCanvas) {
            float prev_x = 0.0F; // 처음 좌표
            float prev_y = 0.0F;
            float next_x = 0.0F; // 다음 좌표
            float next_y = 0.0F;
            boolean value = false;
            float mode = 0.0F;
            boolean isDrawRegion = MyTextureView.this.mLineGraphVO.isDrawRegion();

            for (int i = 0; i < MyTextureView.this.mLineGraphVO.getArrGraph().size(); ++i) { // 총 5번돔
                GraphPath regionPath = new GraphPath(width, height, MyTextureView.this.mLineGraphVO.getPaddingLeft(), MyTextureView.this.mLineGraphVO.getPaddingBottom());
                boolean firstSet = false; // 처음선은 제자리에서 동그라미가 찍히기 떄문에 걸러줘야함
                float x = 0.0F;
                float y = 0.0F;
                this.p.setColor(((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getColor()); // 선 색깔
                this.pCircle.setColor(((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getColor()); // 동그라미 포인트 색깔
                float xGap = (float) (this.xLength / 6);//(float)(this.xLength / 5);//(float)(this.xLength / (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length - 1)); // 그려야할 공간 width
                int var18 = (int) (this.anim / 1.0F);
                mode = this.anim % 1.0F;
                boolean isFinish = false;
                Log.d("Graph", var18 + " 이건뭘까요");
                for (int x_bg = 0; x_bg <= var18 + 1; ++x_bg) {
                    if (x_bg < ((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length) {
                        if (!firstSet) {
                            x = xGap * (float) x_bg;
                            y = (float) this.yLength * ((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[x_bg] / (float) MyTextureView.this.mLineGraphVO.getMaxValue();
                            regionPath.moveTo(x, 0.0F);
                            regionPath.lineTo(x, y);
                            firstSet = true;
                        } else {
                            x = xGap * (float) x_bg;
                            y = (float) this.yLength * ((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[x_bg] / (float) MyTextureView.this.mLineGraphVO.getMaxValue();
                            if (x_bg > var18) {
                                next_x = x - prev_x;
                                next_y = y - prev_y;
                                regionPath.lineTo(prev_x + next_x * mode, prev_y + next_y * mode);
                            } else {
                                regionPath.lineTo(x, y);
                            }
                        }
                        prev_x = x;
                        prev_y = y;
                    }
                }

                isFinish = true;
                if (isDrawRegion) {
                    float var19 = prev_x + next_x * mode;
                    if (var19 >= (float) this.xLength) {
                        var19 = (float) this.xLength;
                    }

                    regionPath.lineTo(var19, 0.0F);
                    regionPath.lineTo(0.0F, 0.0F);
                    Paint pBg = new Paint();
                    pBg.setFlags(1);
                    pBg.setAntiAlias(true);
                    pBg.setFilterBitmap(true);
                    pBg.setStyle(Paint.Style.FILL);
                    pBg.setColor(((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getColor());
                    graphCanvas.getCanvas().drawPath(regionPath, pBg);
                }
            }

        }

        private void drawGraphWithoutAnimation(GraphCanvasWrapper graphCanvas) {
            for (int i = 0; i < MyTextureView.this.mLineGraphVO.getArrGraph().size(); ++i) {
                GraphPath linePath = new GraphPath(width, height, MyTextureView.this.mLineGraphVO.getPaddingLeft(), MyTextureView.this.mLineGraphVO.getPaddingBottom());
                new GraphPath(width, height, MyTextureView.this.mLineGraphVO.getPaddingLeft(), MyTextureView.this.mLineGraphVO.getPaddingBottom());
                boolean firstSet = false;
                float x = 0.0F;
                float y = 0.0F;
                this.p.setColor(((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getColor());
                this.pCircle.setColor(((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getColor());
                float xGap = (float) (this.xLength / 6);//(float)(this.xLength / (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length - 1));
                Log.d("Graph", xGap + "= 한개의 길이");
                for (int j = 0; j < 6; ++j) { //((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length;
                    if (j < ((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length) {
                        if (!firstSet) {
                            x = xGap * (float) j;
                            y = (float) this.yLength * ((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[j] / (float) MyTextureView.this.mLineGraphVO.getMaxValue();
                            linePath.moveTo(x, y);
                            firstSet = true;
                        } else {
                            x = xGap * (float) j;
                            y = (float) this.yLength * ((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[j] / (float) MyTextureView.this.mLineGraphVO.getMaxValue();
                            linePath.lineTo(x, y);
                        }
                        this.pCircle.setColor(MyTextureView.this.mLineGraphVO.getArrGraph().get(i).getColor());
                        this.miniCircle.setColor(MyTextureView.this.mLineGraphVO.getArrGraph().get(i).getColor());
                        graphCanvas.drawCircle(x, y, 15.0F, this.pCircle); // miniCircle
                        graphCanvas.drawCircle(x, y, 10.0F, this.miniCircle);
                    }
                }

                graphCanvas.getCanvas().drawPath(linePath, this.p);
            }

        }

        private void drawGraphWithAnimation(GraphCanvasWrapper graphCanvas) {
            float prev_x = 0.0F;
            float prev_y = 0.0F;
            float next_x = 0.0F;
            float next_y = 0.0F;
            float value = 0.0F;
            float mode = 0.0F;
            for (int i = 0; i < MyTextureView.this.mLineGraphVO.getArrGraph().size(); ++i) {
                GraphPath linePath = new GraphPath(width, height, MyTextureView.this.mLineGraphVO.getPaddingLeft(), MyTextureView.this.mLineGraphVO.getPaddingBottom());
                new GraphPath(width, height, MyTextureView.this.mLineGraphVO.getPaddingLeft(), MyTextureView.this.mLineGraphVO.getPaddingBottom());
                boolean firstSet = false;
                float x = 0.0F;
                float y = 0.0F;
                this.p.setColor(((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getColor());
                this.pCircle.setColor(((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getColor());
                float xGap = (float) (this.xLength / 5);//(float)(this.xLength / (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(0)).getCoordinateArr().length - 1)); // 0 replace i
                value = this.anim / 1.0F;
                mode = this.anim % 1.0F;

                for (int j = 0; (float) j < value + 1.0F; ++j) {
                    if (j < ((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length) {
                        if (!firstSet) {
                            x = xGap * (float) j;
                            y = (float) this.yLength * (((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[j] - MyTextureView.this.mLineGraphVO.getMinValue()) / ((float) MyTextureView.this.mLineGraphVO.getMaxValue() - MyTextureView.this.mLineGraphVO.getMinValue());
                            linePath.moveTo(x, y);
                            firstSet = true;
                        } else {
                            x = xGap * (float) j;
                            y = (float) this.yLength * (((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[j] - MyTextureView.this.mLineGraphVO.getMinValue()) / ((float) MyTextureView.this.mLineGraphVO.getMaxValue() - MyTextureView.this.mLineGraphVO.getMinValue());
                            if ((float) j > value && mode != 0.0F) {
                                next_x = x - prev_x;
                                next_y = y - prev_y;
                                linePath.lineTo(prev_x + next_x * mode, prev_y + next_y * mode);
                            } else {
                                linePath.lineTo(x, y);
                            }
                        }
                        String text = ((int) ((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[j]) + "cm";
                        Rect rect = new Rect();
                        this.pMarkText.setColor(MyTextureView.this.mLineGraphVO.getArrGraph().get(i).getColor());
                        this.pMarkText.getTextBounds(text, 0, text.length(), rect);
                        this.pMarkText.setTextSize(30.0F);
                        graphCanvas.drawText(text, x - 25, y + 30, this.pMarkText);

                        this.pCircle.setColor(MyTextureView.this.mLineGraphVO.getArrGraph().get(i).getColor());
                        this.miniCircle.setColor(MyTextureView.this.mLineGraphVO.getArrGraph().get(i).getColor());
                        graphCanvas.drawCircle(x, y, 15.0F, this.pCircle);
                        graphCanvas.drawCircle(x, y, 10.0F, this.miniCircle);
                        prev_x = x;
                        prev_y = y;
                    }
                }

                graphCanvas.getCanvas().drawPath(linePath, this.p);
            }

        }


        private void drawXMark(GraphCanvasWrapper graphCanvas) {
            float x = 0.0F;
            float xGap = (float) (this.xLength / 5);//(float)(this.xLength / (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(0)).getCoordinateArr().length-1)); // ���׸��� 5���ϱ� View / 4
            float yGap = (float) (this.yLength);
            if (MyTextureView.this.mLineGraphVO.getArrGraph().size() > 0) {
                for (int i = 0; i <= ((MyLineGraph) MyTextureView.this.mLineGraphVO.getArrGraph().get(0)).getCoordinateArr().length; i++) { //((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(0)).getCoordinateArr().length;
                    x = xGap * (float) i;
                    graphCanvas.drawLine(x, yGap, x, yGap + 30.0F, this.pBaseLine);
                }
            }
        }

        private void drawYMark(GraphCanvasWrapper canvas) {
            for (int i = 0; (MyTextureView.this.mLineGraphVO.getIncrement() * i) <= MyTextureView.this.mLineGraphVO.getMaxValue(); ++i) {
                float y = (float) (this.yLength * (MyTextureView.this.mLineGraphVO.getIncrement() * i) / (MyTextureView.this.mLineGraphVO.getMaxValue() - MyTextureView.this.mLineGraphVO.getMinValue()));
                canvas.drawLine(0.0F, y, -40.0F, y, this.pBaseLine);
            }

        }

        private void drawXText(GraphCanvasWrapper graphCanvas) {
            float x = 0.0F;
            float y = 0.0F;
            float xGap = (float) (this.xLength / 5);//(float)(this.xLength / (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(0)).getCoordinateArr().length-1));
            float yGap = (float) (this.yLength + 70);
            for (int i = 0; i < MyTextureView.this.mLineGraphVO.getLegendArr().length; i++) {
                x = xGap * (float) i;
                String text = MyTextureView.this.mLineGraphVO.getLegendArr()[i];
                this.pMarkText.measureText(text);
                this.pMarkText.setTextSize(30.0F);
                Rect rect = new Rect();
                this.pMarkText.getTextBounds(text, 0, text.length(), rect);
                graphCanvas.drawText(text, x - (float) (rect.width() / 2), yGap, this.pMarkText);

            }

        }

        private void drawYText(GraphCanvasWrapper graphCanvas) {
            for (int i = 0; (MyTextureView.this.mLineGraphVO.getIncrement() * i) <= MyTextureView.this.mLineGraphVO.getMaxValue(); ++i) {
                String text = Integer.toString((MyTextureView.this.mLineGraphVO.getIncrement() * i) + MyTextureView.this.mLineGraphVO.getMinValue()); //
                float y = (float) (this.yLength * (MyTextureView.this.mLineGraphVO.getIncrement() * i) / (MyTextureView.this.mLineGraphVO.getMaxValue() - MyTextureView.this.mLineGraphVO.getMinValue()));
                this.pMarkText.measureText(text);
                this.pMarkText.setTextSize(35.0F);
                Rect rect = new Rect();
                this.pMarkText.getTextBounds(text, 0, text.length(), rect);
                graphCanvas.drawText(text, (float) (-(rect.width() + 55)), y - (float) (rect.height() / 2), this.pMarkText);
            }

        }
    }

}
