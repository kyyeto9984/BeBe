package com.bebe.lsi.bebe.GraphClass;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bebe.lsi.bebe.R;
import com.handstudio.android.hzgrapherlib.canvas.GraphCanvasWrapper;
import com.handstudio.android.hzgrapherlib.path.GraphPath;

import java.util.WeakHashMap;

/**
 * Created by Jun on 2015-05-11.
 */
public class MyLineGraphView extends SurfaceView implements SurfaceHolder.Callback {

    /*

        SurfaceView는 View를 상속받은 클래스로 Video Memory로 바로 접근하기 때문에 일반 View에서의 랜더링
        속도보다 빠르다.
        일반 View는 많은 그리기 작업을 하면 메인 스레드의 자원을 다 잡아먹어버리기때문에 상당히 느려지게 되는데
        이러한 단점을 보완하기 위한 클래스라고 보면 된다

        SurfaceView 생명주기

         -surfaceCreated = Surface가 생성 후 호출됨
         -surfaceChanged = Surface가 변경되었을 경우 호출됨
         -surfaceDestroyed = Surface가 Destroy된 경우 호출되미 . 여기서 필요한 자원 해체 작업을 수행.

          MyLineGraphView 실행 순서
          1 . surfaceCreated()
          2. DrawThread()
          3. DrawThread.run();


         */
    public static final String TAG = "LineGraphView";
    private SurfaceHolder mHolder;
    private MyLineGraphView.DrawThread mDrawThread;
    private Context context;

       /* private Display display;
        private int width;
        private int height;
        private int past_x = 0;
        private int past_y = 0;

        private int cur_x = 0;
        private int cur_y = 0;

        private Rect newRect;

        private Canvas canvas;*/


    private MyLineGraphVO mLineGraphVO = null;
    private static final Object touchLock = new Object();


    public MyLineGraphView(Context context, MyLineGraphVO vo) {
        super(context);
        this.mLineGraphVO = vo;
        this.initView(context, vo);
           /* display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            width = display.getWidth();
            height = display.getHeight();*/
    }

    public MyLineGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs, 0);
    }

    public MyLineGraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        this.initView(context, attrs, defStyle);
    }

    private void initView(Context context, MyLineGraphVO vo) {
        this.context = context;
        this.mHolder = this.getHolder();
        this.mHolder.addCallback(this);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        this.mHolder = this.getHolder();
        this.mHolder.addCallback(this);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) { // 1 . SurfaceView�� �����Ǿ�����
        if(this.mDrawThread == null) { // DrawThread �� View�� �׸��� �׸��� Thread�� Null �϶� �����Ѵ�.
            this.mDrawThread = new MyLineGraphView.DrawThread(this.mHolder, this.getContext());
            this.mDrawThread.start();
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if(this.mDrawThread != null) {
            this.mDrawThread.setRunFlag(false);
            this.mDrawThread = null;
        }

    }

    class DrawThread extends Thread {
        SurfaceHolder mHolder;
        Context mCtx;
        boolean isRun = true;
        boolean isDirty = true;
        Matrix matrix = new Matrix();
        int height = MyLineGraphView.this.getHeight();
        int width = MyLineGraphView.this.getWidth();
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
        WeakHashMap<Integer, Bitmap> arrIcon;
        Bitmap bg;

        public DrawThread(SurfaceHolder holder, Context context) { // 2 SurfaceHolder와 Context를 받음 여기서 화면의 크기등 인스턴스들을 다 생성시킴
            Log.i("Graph", "height=" + height);
            this.xLength = this.width - (MyLineGraphView.this.mLineGraphVO.getPaddingLeft() + MyLineGraphView.this.mLineGraphVO.getPaddingRight() + MyLineGraphView.this.mLineGraphVO.getMarginRight());
            this.yLength = this.height - (MyLineGraphView.this.mLineGraphVO.getPaddingBottom() + MyLineGraphView.this.mLineGraphVO.getPaddingTop() + MyLineGraphView.this.mLineGraphVO.getMarginTop());
            this.chartXLength = this.width - (MyLineGraphView.this.mLineGraphVO.getPaddingLeft() + MyLineGraphView.this.mLineGraphVO.getPaddingRight());
            this.chartYLength = this.height - (MyLineGraphView.this.mLineGraphVO.getPaddingBottom() + MyLineGraphView.this.mLineGraphVO.getPaddingTop());
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
            this.arrIcon = new WeakHashMap();
            this.bg = null;
            this.mHolder = holder;
            this.mCtx = context;
            int size = MyLineGraphView.this.mLineGraphVO.getArrGraph().size();

            int bgResource;
            for(bgResource = 0; bgResource < size; ++bgResource) {
                int tempBg = ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(bgResource)).getBitmapResource();
                if(tempBg != -1) {
                    this.arrIcon.put(Integer.valueOf(bgResource), BitmapFactory.decodeResource(MyLineGraphView.this.getResources(), tempBg));
                } else if(this.arrIcon.get(Integer.valueOf(bgResource)) != null) {
                    this.arrIcon.remove(Integer.valueOf(bgResource));
                }
            }

            bgResource = MyLineGraphView.this.mLineGraphVO.getGraphBG();
            if(bgResource != -1) {
                Bitmap var7 = BitmapFactory.decodeResource(MyLineGraphView.this.getResources(), getResources().getColor(R.color.ColorPrimary));
                Log.i("D2DD","bgResource="+bgResource);
                if(var7 == null) Log.i("D2DD","var null");
                this.bg = Bitmap.createScaledBitmap(var7, this.width, this.height, true);
                var7.recycle();
            }

        }

        public void setRunFlag(boolean bool) {
            this.isRun = bool;
        }

        public void run() {
            Canvas canvas = null;
            GraphCanvasWrapper graphCanvasWrapper = null;
            this.setPaint();
            this.isAnimation();
            this.isDrawRegion();
            this.animStartTime = System.currentTimeMillis();
            while(this.isRun) { 
                if(!this.isDirty) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException var13) {
                        var13.printStackTrace();
                    }
                } else {
                    canvas = this.mHolder.lockCanvas();
                    graphCanvasWrapper = new GraphCanvasWrapper(canvas, this.width, this.height,
                            MyLineGraphView.this.mLineGraphVO.getPaddingLeft(), MyLineGraphView.this.mLineGraphVO.getPaddingBottom());
                    SurfaceHolder e = this.mHolder;
                    synchronized(this.mHolder) {
                        synchronized(MyLineGraphView.touchLock) {
                            try {
                                canvas.drawColor(getResources().getColor(R.color.ColorPrimary));
                                if(this.bg != null)
                                    canvas.drawColor(getResources().getColor(R.color.ColorPrimary));
                            } catch (Exception var15) {
                                var15.printStackTrace();
                            } finally {
                                if(graphCanvasWrapper.getCanvas() != null)
                                    this.mHolder.unlockCanvasAndPost(graphCanvasWrapper.getCanvas());
                            }
                        }
                    }
                    try {
                        Thread.sleep(0L);
                    } catch (InterruptedException var14) {
                        var14.printStackTrace();
                    }
                    this.calcTimePass();
                }
            }
        }

         /*
                                        5.SurfaceHolder.lockCanvas() & SurfaceHolder.unlockCanvasAndPost(Canvas c)
                                        더블 버퍼링은 스윙이나 OpenGL에서도 사용되는 고전적인 방법으로 애니메이션과 같이 여러 이미지를 번갈아 보여주어야 하는 경우
                                        Back-Buffer에 이미지를 미리 그린 다음 화면에 바로 표시하여 이미지 처리 성능을 향상 시키는 방법입니다.
                                        안드로이드에서도 역시 더블 버퍼링을 제공하고 있습니다. lockCanvas를 통해 얻어진 Canvas에 그림을 그리는 것은 Back-Buffer에
                                         그리는 것으로 화면에 전혀 표시되지 않습니다. 반드시 unlockCanvasAndPost를 수행해주어야 비로소 작성된 이미지는 화면에 표현됩니다.
                                          따라서 애니메이션 처리를 하시려면 애니메이션 관련 루프 내에서 lockCanvas와 unlockCanvasAndPost룰 반복적으로 수행해야 합니다.
                                          http://anddev.tistory.com/14 <- 짱 도움됨
                                         */

        private void calcTimePass() {
            if(this.isAnimation) {
                long curTime = System.currentTimeMillis();
                long gapTime = curTime - this.animStartTime;
                long animDuration = (long)MyLineGraphView.this.mLineGraphVO.getAnimation().getDuration();
                if(gapTime >= animDuration) {
                    gapTime = animDuration;
                    this.isDirty = false;
                }

                this.anim = (float)(MyLineGraphView.this.mLineGraphVO.getLegendArr().length * (float)gapTime / (float)animDuration);
            } else {
                this.isDirty = false;
            }

        }

        private void drawGraphName(Canvas canvas) throws NullPointerException {
            MyGraphNameBox gnb = MyLineGraphView.this.mLineGraphVO.getGraphNameBox();
            if(gnb != null) {
                boolean nameboxWidth = false;
                boolean nameboxHeight = false;
                int nameboxIconWidth = gnb.getNameboxIconWidth();
                int nameboxIconHeight = gnb.getNameboxIconHeight();
                int nameboxMarginTop = gnb.getNameboxMarginTop();
                int nameboxMarginRight = gnb.getNameboxMarginRight();
                int nameboxPadding = gnb.getNameboxPadding();
                int nameboxTextIconMargin = gnb.getNameboxIconMargin();
                int nameboxIconMargin = gnb.getNameboxIconMargin();
                int nameboxTextSize = gnb.getNameboxTextSize();
                int maxTextWidth = 0;
                int maxTextHeight = 0;
                Paint nameRextPaint = new Paint();
                nameRextPaint.setFlags(1);
                nameRextPaint.setAntiAlias(true);
                nameRextPaint.setFilterBitmap(true);
                nameRextPaint.setColor(0xFFFF0000);
                nameRextPaint.setStrokeWidth(3.0F);
                nameRextPaint.setStyle(Paint.Style.STROKE);
                Paint pIcon = new Paint();
                pIcon.setFlags(1);
                pIcon.setAntiAlias(true);
                pIcon.setFilterBitmap(true);
                pIcon.setColor(0xFFFF00FF);
                pIcon.setStrokeWidth(3.0F);
                pIcon.setStyle(Paint.Style.FILL_AND_STROKE);
                Paint pNameText = new Paint();
                pNameText.setFlags(1);
                pNameText.setAntiAlias(true);
                pNameText.setTextSize((float)nameboxTextSize);
                pNameText.setColor(0xFFFFFF00);
                int graphSize = MyLineGraphView.this.mLineGraphVO.getArrGraph().size();

                int maxCellHight;
                for(maxCellHight = 0; maxCellHight < graphSize; ++maxCellHight) {
                    String i = ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(maxCellHight)).getName();
                    Rect text = new Rect();
                    pNameText.getTextBounds(i, 0, i.length(), text);
                    if(text.width() > maxTextWidth) {
                        maxTextWidth = text.width();
                        maxTextHeight = text.height();
                    }

                    ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(maxCellHight)).getName();
                }

                ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(0)).getName();
                int var22 = 1 * maxTextWidth + nameboxTextIconMargin + nameboxIconWidth;
                maxCellHight = maxTextHeight;
                if(nameboxIconHeight > maxTextHeight) {
                    maxCellHight = nameboxIconHeight;
                }

                int var23 = graphSize * maxCellHight + (graphSize - 1) * nameboxIconMargin;
                canvas.drawRect((float)(this.width - (nameboxMarginRight + var22) - nameboxPadding * 2), (float)nameboxMarginTop, (float)(this.width - nameboxMarginRight), (float)(nameboxMarginTop + var23 + nameboxPadding * 2), nameRextPaint);

                for(int var24 = 0; var24 < graphSize; ++var24) {
                    pIcon.setColor(((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(var24)).getColor());
                    canvas.drawRect((float)(this.width - (nameboxMarginRight + var22) - nameboxPadding), (float)(nameboxMarginTop + maxCellHight * var24 + nameboxPadding + nameboxIconMargin * var24), (float)(this.width - (nameboxMarginRight + maxTextWidth) - nameboxPadding - nameboxTextIconMargin), (float)(nameboxMarginTop + maxCellHight * (var24 + 1) + nameboxPadding + nameboxIconMargin * var24), pIcon);
                    String var25 = ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(var24)).getName();
                    canvas.drawText(var25, (float)(this.width - (nameboxMarginRight + maxTextWidth) - nameboxPadding), (float)(nameboxMarginTop + maxTextHeight / 2 + maxCellHight * var24 + maxCellHight / 2 + nameboxPadding + nameboxIconMargin * var24), pNameText);
                }
            }

        }

        private void isAnimation() {
            if(MyLineGraphView.this.mLineGraphVO.getAnimation() != null) { // vo.setAnimation(new GraphAnimation(GraphAnimation.LINEAR_ANIMATION, GraphAnimation.DEFAULT_DURATION)); �̰ɷ� �ִϸ��̼� ������
                this.isAnimation = true;
            } else {        // �׷��� ���� true��ȯ
                this.isAnimation = false;
            }

        }

        private void isDrawRegion() {
            if(MyLineGraphView.this.mLineGraphVO.isDrawRegion()) { // ���ο��� �̰� true���ִ°� �ּ�ó�������� vo.setDrawRegion(true);
                this.isDrawRegion = true;
            } else {
                this.isDrawRegion = false;
            }

        }
        /*
                ���� ���� ������ �׸��� �޼ҵ�
                MyLineGraphVO = �Ű������� �ѱ� �����͵��� ������

         */
        private void drawBaseLine(GraphCanvasWrapper graphCanvas) {
            for(int i = 1; (MyLineGraphView.this.mLineGraphVO.getIncrement() * i)<= MyLineGraphView.this.mLineGraphVO.getMaxValue(); ++i) {
                float y = (float)(this.yLength * MyLineGraphView.this.mLineGraphVO.getIncrement() * i /(MyLineGraphView.this.mLineGraphVO.getMaxValue() - MyLineGraphView.this.mLineGraphVO.getMinValue()));
                //  yLength�� �Ƹ��� View�� ���� ����
                // ȭ�� ���� - (paddingBottom + marginTop + marginBottom)
                graphCanvas.drawLine(0.0F, y, (float) this.chartXLength, y, this.pBaseLineX);
                //Log.i("LineGraphView",(float)this.chartXLength+":"+y+":"+i);
            }
            for(int i = 1; i< 6 ; i++){ // MyLineGraphView.this.mLineGraphVO.getLegendArr().length �̰ɷ��ϸ� �׻� 5���� height ������ �׷����� ����
                float xGap = (float)(this.xLength / 5); // (MyLineGraphView.this.mLineGraphVO.getLegendArr().length-1) �̰ſ��� 4�� �ٲ� �׷��� �Ǽ��� height ���� ������ �Բ� 4���� ������ �׷���
                float x = xGap *(float)(i);
                //float x =(float)this.yLength * ((LineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[i] / (float)MyLineGraphView.this.mLineGraphVO.getMaxValue();
                graphCanvas.drawLine(x, 0.0F , x , (float) this.chartYLength , this.pBaseLineY);
                //Log.i("LineGraphView", (float) this.chartYLength +":"+x+":"+i);
            }

        }

        private void setPaint() { // ����Ʈ�����ϴ� �޼ҵ�
 /*
                    Paint란
                        그리기(Draw)하기위해 쓰여지는 도구 라고 생각하시면 됩니다.
                        쉽게 말해 Canvas가 도화지라면 Paint는 붓이라고 생각하시면 좋을듯 합니다.
                        도화지에 그림을 그릴때 우리들은 여러가지 붓을 사용하여 효과를 줍니다.
                        붓을 굵기, 색상, 모양등을 선택해서 원하는 형태로 그릴수 있는 것이죠.
                        http://jwandroid.tistory.com/182 -> 출처

                 */
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

        private void drawGraphRegion(GraphCanvasWrapper graphCanvas) {
            if(this.isDrawRegion) {
                if(this.isAnimation) {
                    this.drawGraphRegionWithAnimation(graphCanvas);
                } else {
                    this.drawGraphRegionWithoutAnimation(graphCanvas);
                }
            }

        }

        private void drawGraph(GraphCanvasWrapper graphCanvas) {
            if(this.isAnimation) {
                this.drawGraphWithAnimation(graphCanvas);
            } else {
                this.drawGraphWithoutAnimation(graphCanvas);
            }

        }

        private void drawGraphRegionWithoutAnimation(GraphCanvasWrapper graphCanvas) throws NullPointerException{
            boolean isDrawRegion = MyLineGraphView.this.mLineGraphVO.isDrawRegion();
            for(int i = 0; i < MyLineGraphView.this.mLineGraphVO.getArrGraph().size(); ++i) {
                GraphPath regionPath = new GraphPath(this.width, this.height, MyLineGraphView.this.mLineGraphVO.getPaddingLeft(), MyLineGraphView.this.mLineGraphVO.getPaddingBottom());
                boolean firstSet = false;
                float x = 0.0F;
                float y = 0.0F;
                this.p.setColor(((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getColor());  // 그래프선의 색을 지정해줌
                this.pCircle.setColor(((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getColor());    // 동그라미 포인트 색깔 지정하는거
                float xGap = (float)(this.xLength / 6);//(float)(this.xLength / (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length - 1));
                // xGap은 전체화면에서 배열길이를 나눈값
                Log.i("asdasd",xGap+" 한개의길이");

                for(int pBg = 0; pBg <6; ++pBg) { //((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length;
                    if(pBg < ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length) {
                        if(!firstSet) {// 처음 선을 그림
                            x = xGap * (float)pBg;
                            y = (float)this.yLength * ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[pBg] / (float)MyLineGraphView.this.mLineGraphVO.getMaxValue();
                            regionPath.moveTo(x, 0.0F);
                            regionPath.lineTo(x, y);
                            firstSet = true;
                        } else { // 그다음은 다음 포인트를 찾아서 애니메이션으로 그림
                            x = xGap * (float)pBg;
                            y = (float)this.yLength * ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[pBg] / (float)MyLineGraphView.this.mLineGraphVO.getMaxValue();
                            regionPath.lineTo(x, y);
                            // http://baramziny.tistory.com/75 넘어려워시발머야
                        }
                    }
                }

                if(isDrawRegion) {
                    regionPath.lineTo(x, 0.0F);
                    regionPath.lineTo(0.0F, 0.0F);
                    Paint var10 = new Paint();
                    var10.setFlags(1);
                    var10.setAntiAlias(true);
                    var10.setFilterBitmap(true);
                    var10.setStyle(Paint.Style.FILL);
                    var10.setColor(((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getColor());
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
            boolean isDrawRegion = MyLineGraphView.this.mLineGraphVO.isDrawRegion();

            for(int i = 0; i < MyLineGraphView.this.mLineGraphVO.getArrGraph().size(); ++i) { // 총 5번돔
                GraphPath regionPath = new GraphPath(this.width, this.height, MyLineGraphView.this.mLineGraphVO.getPaddingLeft(), MyLineGraphView.this.mLineGraphVO.getPaddingBottom());
                boolean firstSet = false; // 처음선은 제자리에서 동그라미가 찍히기 떄문에 걸러줘야함
                float x = 0.0F;
                float y = 0.0F;
                this.p.setColor(((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getColor()); // 선 색깔
                this.pCircle.setColor(((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getColor()); // 동그라미 포인트 색깔
                float xGap = (float)(this.xLength / 6);//(float)(this.xLength / 5);//(float)(this.xLength / (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length - 1)); // 그려야할 공간 width
                int var18 = (int)(this.anim / 1.0F);
                mode = this.anim % 1.0F;
                boolean isFinish = false;
                Log.i("asdasd",var18+" 이건뭘까요");
                for(int x_bg = 0; x_bg <= var18 + 1; ++x_bg) {
                    if(x_bg < ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length) {
                        if(!firstSet) {
                            x = xGap * (float)x_bg;
                            y = (float)this.yLength * ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[x_bg] / (float)MyLineGraphView.this.mLineGraphVO.getMaxValue();
                            regionPath.moveTo(x, 0.0F);
                            regionPath.lineTo(x, y);
                            firstSet = true;
                        } else {
                            x = xGap * (float)x_bg;
                            y = (float)this.yLength * ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[x_bg] / (float)MyLineGraphView.this.mLineGraphVO.getMaxValue();
                            if(x_bg > var18) {
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
                if(isDrawRegion) {
                    float var19 = prev_x + next_x * mode;
                    if(var19 >= (float)this.xLength) {
                        var19 = (float)this.xLength;
                    }

                    regionPath.lineTo(var19, 0.0F);
                    regionPath.lineTo(0.0F, 0.0F);
                    Paint pBg = new Paint();
                    pBg.setFlags(1);
                    pBg.setAntiAlias(true);
                    pBg.setFilterBitmap(true);
                    pBg.setStyle(Paint.Style.FILL);
                    pBg.setColor(((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getColor());
                    graphCanvas.getCanvas().drawPath(regionPath, pBg);
                }
            }

        }

        private void drawGraphWithoutAnimation(GraphCanvasWrapper graphCanvas) {
            for(int i = 0; i < MyLineGraphView.this.mLineGraphVO.getArrGraph().size(); ++i) {
                GraphPath linePath = new GraphPath(this.width, this.height, MyLineGraphView.this.mLineGraphVO.getPaddingLeft(), MyLineGraphView.this.mLineGraphVO.getPaddingBottom());
                new GraphPath(this.width, this.height, MyLineGraphView.this.mLineGraphVO.getPaddingLeft(), MyLineGraphView.this.mLineGraphVO.getPaddingBottom());
                boolean firstSet = false;
                float x = 0.0F;
                float y = 0.0F;
                this.p.setColor(((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getColor());
                this.pCircle.setColor(((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getColor());
                float xGap = (float)(this.xLength / 6);//(float)(this.xLength / (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length - 1));
                Bitmap icon = (Bitmap)this.arrIcon.get(Integer.valueOf(i));
                Log.i("asdasd",xGap+"= 한개의 길이");
                for(int j = 0; j < 6; ++j) { //((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length;
                    if(j < ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length) {
                        if(!firstSet) {
                            x = xGap * (float)j;
                            y = (float)this.yLength * ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[j] / (float)MyLineGraphView.this.mLineGraphVO.getMaxValue();
                            linePath.moveTo(x, y);
                            firstSet = true;
                        } else {
                            x = xGap * (float)j;
                            y = (float)this.yLength * ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[j] / (float)MyLineGraphView.this.mLineGraphVO.getMaxValue();
                            linePath.lineTo(x, y);
                        }

                        if(icon == null) {
                            graphCanvas.drawCircle(x, y, 15.0F, this.pCircle); // miniCircle
                            graphCanvas.drawCircle(x, y, 10.0F, this.miniCircle);
                        } else {
                            graphCanvas.drawBitmapIcon(icon, x, y, (Paint)null);
                        }
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
            for(int i = 0; i < MyLineGraphView.this.mLineGraphVO.getArrGraph().size(); ++i) {
                GraphPath linePath = new GraphPath(this.width, this.height, MyLineGraphView.this.mLineGraphVO.getPaddingLeft(), MyLineGraphView.this.mLineGraphVO.getPaddingBottom());
                new GraphPath(this.width, this.height, MyLineGraphView.this.mLineGraphVO.getPaddingLeft(), MyLineGraphView.this.mLineGraphVO.getPaddingBottom());
                boolean firstSet = false;
                float x = 0.0F;
                float y = 0.0F;
                this.p.setColor(((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getColor());
                this.pCircle.setColor(((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getColor());
                float xGap = (float)(this.xLength / 5);//(float)(this.xLength / (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(0)).getCoordinateArr().length - 1)); // 0 replace i
                Bitmap icon = (Bitmap)this.arrIcon.get(Integer.valueOf(i));
                value = this.anim / 1.0F;
                mode = this.anim % 1.0F;

                for(int j = 0; (float)j < value + 1.0F; ++j) {
                    if(j < ((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr().length) {
                        if(!firstSet) {
                            x = xGap * (float)j;
                            y = (float)this.yLength * (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[j] - MyLineGraphView.this.mLineGraphVO.getMinValue()) / ((float)MyLineGraphView.this.mLineGraphVO.getMaxValue() - MyLineGraphView.this.mLineGraphVO.getMinValue());
                            linePath.moveTo(x, y);
                            firstSet = true;
                        } else {
                            x = xGap * (float)j;
                            y = (float)this.yLength * (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[j]  - MyLineGraphView.this.mLineGraphVO.getMinValue())/ ((float)MyLineGraphView.this.mLineGraphVO.getMaxValue() - MyLineGraphView.this.mLineGraphVO.getMinValue());
                            if((float)j > value && mode != 0.0F) {
                                next_x = x - prev_x;
                                next_y = y - prev_y;
                                linePath.lineTo(prev_x + next_x * mode, prev_y + next_y * mode);
                            } else {
                                linePath.lineTo(x, y);
                            }
                        }

                        if(icon == null) {
                            String text = ((int)((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(i)).getCoordinateArr()[j])+"cm";
                            Rect rect = new Rect();
                            this.pMarkText.getTextBounds(text, 0, text.length(), rect);
                            graphCanvas.drawText(text, x-25, y + 50, this.pMarkText);

                            graphCanvas.drawCircle(x, y, 15.0F, this.pCircle);
                            graphCanvas.drawCircle(x, y, 10.0F, this.miniCircle);
                        } else {
                            graphCanvas.drawBitmapIcon(icon, x, y, (Paint)null);
                        }

                        prev_x = x;
                        prev_y = y;
                    }
                }

                graphCanvas.getCanvas().drawPath(linePath, this.p);
            }

        }


        private void drawXMark(GraphCanvasWrapper graphCanvas) {
            float x = 0.0F;
            float xGap = (float)(this.xLength / 5);//(float)(this.xLength / (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(0)).getCoordinateArr().length-1)); // ���׸��� 5���ϱ� View / 4
            float yGap = (float)(this.yLength);
            if(MyLineGraphView.this.mLineGraphVO.getArrGraph().size() > 0) {
                for (int i = 0; i <= ((MyLineGraph) MyLineGraphView.this.mLineGraphVO.getArrGraph().get(0)).getCoordinateArr().length; i++) { //((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(0)).getCoordinateArr().length;
                    x = xGap * (float) i;
                    graphCanvas.drawLine(x, yGap, x, yGap + 30.0F, this.pBaseLine);
                }
            }
        }

        private void drawYMark(GraphCanvasWrapper canvas) {
            for(int i = 0; (MyLineGraphView.this.mLineGraphVO.getIncrement() * i)<=  MyLineGraphView.this.mLineGraphVO.getMaxValue(); ++i) {
                float y = (float)(this.yLength * (MyLineGraphView.this.mLineGraphVO.getIncrement() * i) / (MyLineGraphView.this.mLineGraphVO.getMaxValue() - MyLineGraphView.this.mLineGraphVO.getMinValue()));
                canvas.drawLine(0.0F, y, -40.0F, y, this.pBaseLine);
            }

        }

        private void drawXText(GraphCanvasWrapper graphCanvas) {
            float x = 0.0F;
            float y = 0.0F;
            float xGap = (float)(this.xLength /5);//(float)(this.xLength / (((MyLineGraph)MyLineGraphView.this.mLineGraphVO.getArrGraph().get(0)).getCoordinateArr().length-1));
            float yGap = (float)(this.yLength+70);
            for(int i = 0; i < MyLineGraphView.this.mLineGraphVO.getLegendArr().length; i++) {
                x = xGap * (float) i;
                String text = MyLineGraphView.this.mLineGraphVO.getLegendArr()[i];
                this.pMarkText.measureText(text);
                this.pMarkText.setTextSize(30.0F);
                Rect rect = new Rect();
                this.pMarkText.getTextBounds(text, 0, text.length(), rect);
                graphCanvas.drawText(text, x - (float) (rect.width() / 2), yGap, this.pMarkText);

            }

        }
        private void drawYText(GraphCanvasWrapper graphCanvas) {
            for(int i = 0; (MyLineGraphView.this.mLineGraphVO.getIncrement() * i) <= MyLineGraphView.this.mLineGraphVO.getMaxValue(); ++i) {
                String text = Integer.toString((MyLineGraphView.this.mLineGraphVO.getIncrement() * i) + MyLineGraphView.this.mLineGraphVO.getMinValue()); //
                float y = (float)(this.yLength * (MyLineGraphView.this.mLineGraphVO.getIncrement() * i) /(MyLineGraphView.this.mLineGraphVO.getMaxValue() - MyLineGraphView.this.mLineGraphVO.getMinValue()));
                this.pMarkText.measureText(text);
                this.pMarkText.setTextSize(35.0F);
                Rect rect = new Rect();
                this.pMarkText.getTextBounds(text, 0, text.length(), rect);
                graphCanvas.drawText(text, (float)(-(rect.width() + 55)), y - (float)(rect.height() / 2), this.pMarkText);
            }

        }
    }

/*

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int keyAction = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (keyAction){
            case MotionEvent.ACTION_DOWN:
                past_x = x;
                break;
            case MotionEvent.ACTION_MOVE:
                cur_x = cur_x + (past_x - x );
                if (cur_x <0) cur_x = 0;
                if (cur_x  > width) cur_x = width;
                past_x = x;
                break;
        }
        return true;
    }
*/

}
