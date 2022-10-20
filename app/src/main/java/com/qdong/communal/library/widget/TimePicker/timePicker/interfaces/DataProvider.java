package com.qdong.communal.library.widget.TimePicker.timePicker.interfaces;

import com.qdong.communal.library.widget.TimePicker.entity.SelectData;

/**
 * DataProvider
 * Created By:Chuck
 * Des:
 * on 2018/9/18 17:18
 */
public interface DataProvider {
    SelectData[] getFirstColumnDatas();
    SelectData[] getSecondColumnDatas(String firstDataCode);
    SelectData[] getThirdColumnDatas(String secondDataCode);
}
