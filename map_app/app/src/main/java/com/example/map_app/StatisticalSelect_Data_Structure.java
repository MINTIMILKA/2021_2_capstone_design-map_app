package com.example.map_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

public class StatisticalSelect_Data_Structure extends View
{
    int[] title_num;
    int[] subtitle_num;
    int[] local_num;

    //마지막 그래프 출력 번호 (0 ~ 11)
    int last_view_num;

    /*
    그래프를 그리기 위한 변수

    graph_background_view: 그래프 배경 그림
    graph_data_view: 그래프 데이터 그림
    nation_comp_menu_list_view: 출력할 리스트 뷰
    nation_comp_menu_list_item_adapter: 리스트 어댑터

    @SuppressLint 설명: @SuppressLint("NewApi")는 해당 프로젝트의 설정 된 minSdkVersion 이후에 나온 API를 사용할때  warning을 없애고 개발자가 해당 APi를 사용할 수 있게 합니다.
    출처: https://nsstbg.tistory.com/13
    */
    @SuppressLint("StaticFieldLeak")
    Graph_Draw[] graph_background_view, graph_data_view;

    //출력할 뷰
    FrameLayout statistical_select_menu_layout;

    //출력 확인 변수
    boolean[][] main_graph_bool;

    int VIEW_BASE_ID = 333000;

    int version;

    StatisticalSelect_Data_Structure(int version, Context context, FrameLayout statistical_select_menu_layout, int last_view_num, int[] title_num, int[] subtitle_num, int[] local_num)
    {
        super(context);

        this.version = version;

        this.statistical_select_menu_layout = statistical_select_menu_layout;

        this.title_num = title_num;
        this.subtitle_num = subtitle_num;

        this.last_view_num = last_view_num;

        this.local_num = new int[local_num.length];
        for(int i=0;i<local_num.length;i++)
        {
            this.local_num[i] = local_num[i];
        }

        this.graph_background_view = new Graph_Draw[last_view_num+1];
        this.graph_data_view = new Graph_Draw[last_view_num+1];
        for(int i=0;i<last_view_num+1;i++)
        {
            this.graph_background_view[i] = new Graph_Draw(context, 1);
            this.graph_background_view[i].setId(VIEW_BASE_ID + (i*2));

            this.graph_data_view[i] = new Graph_Draw(context, 2);
            this.graph_data_view[i].setId(VIEW_BASE_ID + (i*2) +1);
        }

        //그래프 출력 확인 변수
        this.main_graph_bool = new boolean[last_view_num+1][2];
        for(int i=0;i<last_view_num+1;i++)
        {
            for(int j=0;j<2;j++)
            {
                this.main_graph_bool[i][j] = false;
            }
        }

    }

    //그래프 출력 상태 변경
    public void set_graph_status(int view_num, boolean graph_background_bool, boolean graph_data_bool)
    {
        this.main_graph_bool[view_num][0] = graph_background_bool;
        this.main_graph_bool[view_num][1] = graph_data_bool;
    }
}
