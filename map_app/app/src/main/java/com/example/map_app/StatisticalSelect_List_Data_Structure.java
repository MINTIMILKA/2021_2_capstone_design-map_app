package com.example.map_app;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//Statistical_Data에 사용할 데이터
class StatisticalSelect_List_Data_Structure
{
    //버전 번호
    int version;

    //제목 번호
    int title_num;
    //부제목 번호
    int subtitle_num;

    //출력할 리스트 뷰
    ListView main_statistical_list_list_view;

    //리스트 어댑터
    StatisticalSelect_List_Adapter main_statistical_list_item_adapter;

    StatisticalSelect_List_Data_Structure(int version, ListView main_statistical_list_list_view, StatisticalSelect_List_Adapter main_statistical_list_item_adapter)
    {
        this.version = version;
        this.main_statistical_list_list_view = main_statistical_list_list_view;
        this.main_statistical_list_item_adapter = main_statistical_list_item_adapter;
    }

    public void set_title_num(int title_num)
    {
        this.title_num = title_num;
    }

}
