package com.example.map_app;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.Marker;

public class Graph_Draw extends View {

    //그래프 요소 클래스 선언
    Graph_Element graph_element;

    //그래프 요소 타입 변수
    int graph_type;

    //그래프 번호
    int view_num;

    //그래프 데이터 수
    int graph_data_num;

    //그래프 값 배열
    float[] data_value;

    Graph_Draw(Context context, int type) {
        super(context);

        graph_element = new Graph_Element(context);

        //그래프 요소 타입 변수 초기화
        graph_type = type;
    }

    //그래프 데이터수(graph_data_num)만큼 배열 할당
    public void alloc_data_array(int data_value_length)
    {
        graph_data_num = data_value_length;

        //데이터 값 할당
        data_value = new float[graph_data_num];
    }

    //그래프 배경 크기 설정
    public void set_graph_size(float background_width, float background_height)
    {
        graph_element.graph_background.background_width = background_width;
        graph_element.graph_background.background_height = background_height;
    }

    //그래프 배경 크기를 기본으로 설정
    public void set_graph_base_size(View main_map_layout)
    {
        graph_element.graph_background.background_width = (float)(main_map_layout.getMeasuredWidth()/4);
        graph_element.graph_background.background_height = (float)(main_map_layout.getMeasuredHeight()/3);
    }

    //그래프 배경 크기를 리스트에 맞춰 설정
    public void set_graph_list_size(View nation_comp_menu_list_view)
    {
        graph_element.graph_background.background_width = (float)nation_comp_menu_list_view.getMeasuredWidth();
        graph_element.graph_background.background_height = (float)nation_comp_menu_list_view.getMeasuredHeight();
    }

    //그래프 배경 크기를 통계 모음에 맞춰 설정
    public void set_statistical_select_graph_list_size(View nation_comp_menu_list_view)
    {
        graph_element.graph_background.background_width = (float)nation_comp_menu_list_view.getMeasuredWidth()/4;
        graph_element.graph_background.background_height = (float)nation_comp_menu_list_view.getMeasuredHeight()/3;
    }

    //그래프 위치 설정
    public void set_graph_pos(float GRAPH_POS_X, float GRAPH_POS_Y)
    {
        graph_element.GRAPH_POS_X = GRAPH_POS_X;
        graph_element.GRAPH_POS_Y = GRAPH_POS_Y;
    }

    //메인 그래프 기본 위치 설정
    public void set_main_graph_background_pos(View main_map_layout, int view_num)
    {
        //마진 크기
        float margin = 20;

        //버튼과 겹치지 않기 위한 높이
        //float floor_margin = ;

        this.view_num = view_num;

        switch(view_num)
        {
            case 0:
                graph_element.GRAPH_POS_X = margin;
                graph_element.GRAPH_POS_Y = margin;
                break;
            case 1:
                graph_element.GRAPH_POS_X = main_map_layout.getMeasuredWidth() - graph_element.graph_background.background_width - margin;
                graph_element.GRAPH_POS_Y = margin;
                break;
            case 2:
                graph_element.GRAPH_POS_X = margin;
                graph_element.GRAPH_POS_Y = (float)(main_map_layout.getMeasuredHeight()/(1.2)) - graph_element.graph_background.background_height - margin;
                break;
            case 3:
                graph_element.GRAPH_POS_X = main_map_layout.getMeasuredWidth() - graph_element.graph_background.background_width - margin;
                graph_element.GRAPH_POS_Y = (float)(main_map_layout.getMeasuredHeight()/(1.2)) - graph_element.graph_background.background_height - margin;
                break;
            default:
                break;
        }
    }

    //리스트 그래프 기본 위치 설정
    public void set_list_graph_background_pos(int view_num)
    {
        this.view_num = view_num;

        graph_element.GRAPH_POS_X = 0;
        graph_element.GRAPH_POS_Y = 0;
    }

    //통계 모음의 위치 설정
    public void set_statisticalselect_graph_background_pos(View statistical_select_menu_layout, int view_num)
    {
        this.view_num = -1;

        graph_element.GRAPH_POS_X = (float)((statistical_select_menu_layout.getMeasuredWidth()/4)*(view_num%4));
        graph_element.GRAPH_POS_Y = (float)((statistical_select_menu_layout.getMeasuredHeight()/3)*(int)(view_num/4));
    }


    //그래프 배경 그리기(유형 1)
    public void draw_graph_background(Canvas canvas, int view_num)
    {
        graph_element.draw_graph_background(canvas, view_num);
    }

    //그래프 데이터 초기화
    public void graph_data_array_init()
    {
        graph_element.graph_data.graph_data_array_init(graph_data_num);
    }

    //그래프 배경 정보 구하기
    public void set_graph_data_pos_and_size(Graph_Element graph_background)
    {
        graph_element.graph_background.background_pos_x = graph_background.GRAPH_POS_X;
        graph_element.graph_background.background_pos_y = graph_background.GRAPH_POS_Y;
        graph_element.graph_background.background_width = graph_background.graph_background.background_width;
        graph_element.graph_background.background_height = graph_background.graph_background.background_height;
    }

    //그래프 데이터 그리기(유형 2)
    public void draw_graph_data(Canvas canvas)
    {
        //그래프 데이터 그리기
        graph_element.draw_graph_data(canvas, graph_data_num, data_value);
    }

    //그래프 텍스트 그리기
    public void draw_graph_text(Canvas canvas)
    {
        graph_element.draw_graph_info(canvas, graph_data_num, data_value);
    }

    //그래프 요소 유형 결정후 그리기
    public void draw_graph(Canvas canvas, int graph_type, int view_num)
    {
        //그래프 요소 유형 확인
        switch(graph_type)
        {
            case 1:
                draw_graph_background(canvas, view_num);
                break;
            case 2:
                draw_graph_data(canvas);
                draw_graph_text(canvas);
                break;
            default:
                break;
        }
    }

    //맵에 마커 표시
    public void map_marker(Map_Draw map_draw, int view_num, int[] local_num)
    {
        for(int i=0;i<map_draw.map_local_marker.length;i++)
        {
            for(int j=0;j<local_num.length;j++)
            {
                //마커로 표시된 지역이 local_num[j]에 해당하지 않는 경우
                if(i == local_num[j])
                {
                    map_draw.map_local_marker[i].setPosition(map_draw.map_local_pos[i]);
                    map_draw.map_local_marker[i].setMap(map_draw.naver_map);

                    map_draw.map_local_marker[i].setIconTintColor(graph_element.graph_data.data_color[j]);
                    //마커 색 저장
                    map_draw.map_marker_color[view_num][local_num[j]] = graph_element.graph_data.data_color[j];
                    break;
                }
                else
                {
                    map_draw.map_local_marker[i].setMap(null);
                }
            }
        }
    }

    public void save_color()
    {
        //
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        draw_graph(canvas, graph_type, view_num);
    }
}
