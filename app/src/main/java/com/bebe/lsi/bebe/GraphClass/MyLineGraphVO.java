package com.bebe.lsi.bebe.GraphClass;

import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;

import java.util.List;

/**
 * Created by Jun on 2015-05-12.
 */
public class MyLineGraphVO  extends MyGraph {
    private int maxValue = 100;
    private int minValue = 50;
    private int increment = 10;
    private GraphAnimation animation = null;
    private String[] legendArr = null;
    private List<MyLineGraph> arrGraph = null;
    private int graphBG = -1;
    private boolean isDrawRegion = false;

    public MyLineGraphVO(String[] legendArr, List<MyLineGraph> arrGraph) {
        this.setLegendArr(legendArr);
        this.arrGraph = arrGraph;
    }

    public MyLineGraphVO(String[] legendArr, List<MyLineGraph> arrGraph, int graphBG) {
        this.setLegendArr(legendArr);
        this.arrGraph = arrGraph;
        this.setGraphBG(graphBG);
    }

    public MyLineGraphVO(int paddingBottom, int paddingTop, int paddingLeft, int paddingRight, int marginTop, int marginRight, int maxValue, int increment, String[] legendArr, List<MyLineGraph> arrGraph) {
        super(paddingBottom, paddingTop, paddingLeft, paddingRight, marginTop, marginRight);
        this.maxValue = maxValue;
        this.increment = increment;
        this.setLegendArr(legendArr);
        this.arrGraph = arrGraph;
    } // ���� ȣ���ϴ� �����ڴ� �̰���

    public MyLineGraphVO(int paddingBottom, int paddingTop, int paddingLeft, int paddingRight, int marginTop, int marginRight, int maxValue, int increment, String[] legendArr, List<MyLineGraph> arrGraph, int graphBG) {
        super(paddingBottom, paddingTop, paddingLeft, paddingRight, marginTop, marginRight);
        this.maxValue = maxValue;
        this.increment = increment;
        this.setLegendArr(legendArr);
        this.arrGraph = arrGraph;
        this.setGraphBG(graphBG);
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    public int getMinValue() {
        return this.minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getIncrement() {
        return this.increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public String[] getLegendArr() {
        return this.legendArr;
    }

    public void setLegendArr(String[] legendArr) {
        this.legendArr = legendArr;
    }

    public List<MyLineGraph> getArrGraph() {
        return this.arrGraph;
    }

    public void setArrGraph(List<MyLineGraph> arrGraph) {
        this.arrGraph = arrGraph;
    }

    public int getGraphBG() {
        return this.graphBG;
    }

    public void setGraphBG(int graphBG) {
        this.graphBG = graphBG;
    }

    public GraphAnimation getAnimation() {
        return this.animation;
    }

    public void setAnimation(GraphAnimation animation) {
        this.animation = animation;
    }

    public boolean isDrawRegion() {
        return this.isDrawRegion;
    }

    public void setDrawRegion(boolean isDrawRegion) {
        this.isDrawRegion = isDrawRegion;
    }
}
