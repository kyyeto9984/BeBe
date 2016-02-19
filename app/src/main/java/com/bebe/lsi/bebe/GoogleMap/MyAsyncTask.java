package com.bebe.lsi.bebe.GoogleMap;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by LSJ on 2015-08-27.
 */
public class MyAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> implements OnExcuteFinishListener {

    public OnExcuteFinishListener onExcuteFinishListener;
    public ExcuteFinish excuteFinish;

    @Override
    protected Result doInBackground(Params... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
    }

    @Override
    public void OnExcuteFinish(ExcuteFinish excuteFinish) {

    }

    public void setOnExcuteFinishListener(OnExcuteFinishListener onExcuteFinishListener){
        this.onExcuteFinishListener = onExcuteFinishListener;
    }

    public OnExcuteFinishListener getOnExcuteFinishListener(){
        return onExcuteFinishListener;
    }

    public void setOnExcuteFinish(ExcuteFinish excuteFinish){
        this.excuteFinish = excuteFinish;
    }
    public ExcuteFinish getOnExcuteFinish(){
        return excuteFinish;
    }


}
