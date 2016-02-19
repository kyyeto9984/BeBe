package com.bebe.lsi.bebe;

import android.content.Context;

import com.bebe.lsi.bebe.GraphClass.GraphData;
import com.bebe.lsi.bebe.GraphClass.MyLineGraph;
import com.bebe.lsi.bebe.GraphClass.MyLineGraphVO;
import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LSJ on 2015-12-21.
 */
public class GraphManager {

    private GraphData graphData;

    public MyLineGraphVO getGraph(Context context){
        graphData = new GraphData();
        MyLineGraphVO vo;
        int paddingBottom = MyLineGraphVO.DEFAULT_PADDING + 30;
        int paddingTop = MyLineGraphVO.DEFAULT_PADDING + 110;
        int paddingLeft = MyLineGraphVO.DEFAULT_PADDING + 130;
        int paddingRight = MyLineGraphVO.DEFAULT_PADDING + 30;
        int marginTop = MyLineGraphVO.DEFAULT_MARGIN_TOP;
        int marginRight = MyLineGraphVO.DEFAULT_MARGIN_RIGHT;
        int maxValue = MyLineGraphVO.DEFAULT_MAX_VALUE;
        int minValue = MyLineGraphVO.DEFAULT_MIN_VALUE;
        int increment = MyLineGraphVO.DEFAULT_INCREMENT;
        String name = context.getSharedPreferences("baby", context.MODE_PRIVATE).getString("babyname", null);
        float[] myBaby = graphData.getBabyHeight(context,name);
        float[] avgheight = new float[myBaby.length];
        String[] array = new String[6];
        String getBabyBirth = graphData.getBabyBirth(context);
        int Babymonth = graphData.getBabyMonth(getBabyBirth);
        String[] legendArr = graphData.getBabyMonth(array, Babymonth);
        legendArr = new String[]{"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}; // Test
        List<MyLineGraph> arrGraph = new ArrayList<>();
        arrGraph.add(new MyLineGraph(0xFFFFFFFF, myBaby));//boyGraph
        arrGraph.add(new MyLineGraph(0xFFF48FB1, avgheight));//boyGraph

        vo = new MyLineGraphVO(
                paddingBottom, paddingTop, paddingLeft, paddingRight,
                marginTop, marginRight, maxValue, increment, legendArr, arrGraph);
        vo.setAnimation(new GraphAnimation(GraphAnimation.LINEAR_ANIMATION, GraphAnimation.DEFAULT_DURATION));
        return vo;
    }

}
