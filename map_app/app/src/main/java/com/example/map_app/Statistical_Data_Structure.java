package com.example.map_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

//Statistical_Data에 사용할 데이터
class Statistical_Data_Structure extends View
{
    int title_num;
    int subtitle_num;
    int[] local_num;

    /*
    그래프를 그리기 위한 변수

    main_map_layout: 지도 레이아웃
    map_draw: 지도 클래스
    graph_background_view: 그래프 배경 그림
    graph_data_view: 그래프 데이터 그림
    VIEW_BASE_I: view의 기초 id (다른 view의 id는 offset으로 계산)
    view_num: 그래프 식별 번호
    main_graph_bool: 그래프 출력 확인 변수 (0: 그래프 배경 뷰, 1: 데이터 뷰)
    data_color: 그래프 테이터 색

    @SuppressLint 설명: @SuppressLint("NewApi")는 해당 프로젝트의 설정 된 minSdkVersion 이후에 나온 API를 사용할때  warning을 없애고 개발자가 해당 APi를 사용할 수 있게 합니다.
    출처: https://nsstbg.tistory.com/13
    */

    @SuppressLint("StaticFieldLeak")
    FrameLayout main_map_layout;

    Map_Draw map_draw;

    @SuppressLint("StaticFieldLeak")
    Graph_Draw graph_background_view, graph_data_view;

    int VIEW_BASE_ID = 111000;

    int view_num;

    boolean[] main_graph_bool;

    //view_num: 그래프 식별 번호, dsd_api: DSD 주소, structure_specific_api: structure_specific 주소, subtitle_num: 부제목 번호, local_num: 출력할 지역 번호
    @SuppressLint("ResourceType")
    Statistical_Data_Structure(Context context, View map_view, Map_Draw map_draw, int view_num, int title_num, int subtitle_num, int[] local_num)
    {
        super(context);

        this.title_num = title_num;
        this.subtitle_num = subtitle_num;


        this.local_num = new int[local_num.length];
        for(int i=0;i<local_num.length;i++)
        {
            this.local_num[i] = local_num[i];
        }

        //지도 레이아웃
        this.main_map_layout = (FrameLayout)map_view;

        this.map_draw = map_draw;

        this.graph_background_view = new Graph_Draw(context, 1);
        this.graph_background_view.setId(VIEW_BASE_ID + (2*view_num));

        this.graph_data_view = new Graph_Draw(context, 2);
        this.graph_data_view.setId(VIEW_BASE_ID + (2*view_num) + 1);

        this.view_num = view_num;

        //그래프 출력 확인 변수
        this.main_graph_bool = new boolean[2];
        for(int i=0;i<2;i++)
        {
            this.main_graph_bool[i] = false;
        }
    }

    //그래프 출력 상태 변경
    public void set_graph_status(boolean graph_background_bool, boolean graph_data_bool)
    {
        this.main_graph_bool[0] = graph_background_bool;
        this.main_graph_bool[1] = graph_data_bool;
    }
}
