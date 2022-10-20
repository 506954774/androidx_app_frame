/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.qdong.communal.library.widget.TimePicker.timePicker.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qdong.communal.library.util.LogUtil;

import java.util.HashMap;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter extends AbstractWheelTextAdapter {
    
    /** The default min value */
    public static final int DEFAULT_MAX_VALUE = 9;

    /** The default max value */
    private static final int DEFAULT_MIN_VALUE = 0;
    
    // Values
    private int minValue;
    private int maxValue;
    
    // format
    private String format;
    
    private String label;
    
    /**
     * Constructor
     * @param context the current context
     */
    public NumericWheelAdapter(Context context) {
        this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }

    /**
     * Constructor
     * @param context the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue) {
        this(context, minValue, maxValue, null);
    }

    /**
     * Constructor
     * @param context the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param format the format string
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue, String format) {
        super(context);
        
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = minValue + index;
            return format != null ? String.format(format, value) : Integer.toString(value);
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return maxValue - minValue + 1;
    }
    
    @Override
    public View getItem(int index, View convertView, ViewGroup parent, boolean isChosen) {
        if (index >= 0 && index < getItemsCount()) {
            if (convertView == null) {
                convertView = getView(itemResourceId, parent);
            }
            TextView textView = getTextView(convertView, itemTextResourceId);


            if (textView != null) {
                CharSequence text = getItemText(index);
                if (text == null) {
                    text = "";
                }
                textView.setText(text+label);


                if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
                    configureTextView(textView,isChosen);
                }
            }
            return convertView;
        }
    	return null;
    }

	public void setLabel(String label) {
		this.label=label;
	}

    public void onScollStarted(HashMap<Integer,View> mViewMaps, int currentItem){

        LogUtil.i("NumberPicker","onScollStarted,currentItem:"+currentItem+",mViewMaps.size():"+mViewMaps.size());

        for(int i=0;i<mViewMaps.size();i++){
            View view= mViewMaps.get(i);
            if(view !=null){
                TextView textView= (TextView) view;
                textView.setTextSize(DEFAULT_TEXT_SIZE);
                textView.setTextColor(DEFAULT_TEXT_COLOR_UNCHOSED);

            }

        }
    };
    public void onScroll(HashMap<Integer,View> mViewMaps, int currentItem){
        LogUtil.i("NumberPicker","onScroll,currentItem:"+currentItem+",mViewMaps.size():"+mViewMaps.size());

        for(int i=0;i<mViewMaps.size();i++){

            View view= mViewMaps.get(i);
            if(view !=null){
                TextView textView= (TextView) view;
                textView.setTextSize(DEFAULT_TEXT_SIZE);

                textView.setTextColor(DEFAULT_TEXT_COLOR_UNCHOSED);
            }
        }
    };
    public void onScollEnd(HashMap<Integer,View> mViewMaps, int currentItem){
        LogUtil.i("NumberPicker","onScollEnd,currentItem:"+currentItem+",mViewMaps.size():"+mViewMaps.size());

        for(int i=0;i<mViewMaps.size();i++){
            if(i==currentItem){
                View view= mViewMaps.get(i);
                if(view !=null){
                    TextView textView= (TextView) view;
                    textView.setTextSize(SELECTED_TEXT_SIZE);

                    textView.setTextColor(DEFAULT_TEXT_COLOR);
                }
            }
        }
    }
}
