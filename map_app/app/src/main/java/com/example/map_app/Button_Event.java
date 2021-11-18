package com.example.map_app;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

public class Button_Event extends View {

    //버튼 상수 정의{도시 버튼(8개), 기능 버튼(3개), 마지막을 가리키는 열거형 상수}
    //java의 열거형 상수를 참조하기 위해서는 ordial() 함수 필요 >> ex) BUTTON_NUM.MAX_BUTTON_NUM.ordial();
    enum BUTTON_NUM{seoul_button, busan_button_onclick, deagu_button, incheon_button, gwangju_button,
                    daejeon_button, ulsan_button, sejong_button,
                    nation_comp, static_comp, statistic_reset, MAX_BUTTON_NUM}
    //버튼 확인 변수 정의
    boolean[] button_bool;

    Button_Event(Context context)
    {
        super(context);

        //버튼 확인 변수 초기화
        button_bool = new boolean[BUTTON_NUM.MAX_BUTTON_NUM.ordinal()];
        for(int i=0;i<BUTTON_NUM.MAX_BUTTON_NUM.ordinal();i++)
        {
            button_bool[i] = false;
        }
    }

    //그래프 생성 기초 함수
    public void graph_base_create(Context context, boolean[] current_view_num_bool, Map_Draw map_draw, FrameLayout main_map_layout, int view_num, int title, int subtitle, int[] main_local_num)
    {
        Statistical_Data main_graph = new Statistical_Data();
        Statistical_Data_Structure  main_graph_data = new Statistical_Data_Structure(context, main_map_layout, map_draw, view_num, title, subtitle, main_local_num);

        if(current_view_num_bool[view_num] == true)
        {
            main_graph_data.set_graph_status(true, true);
        }

        main_graph.execute(main_graph_data);

        main_graph = null;
        main_graph_data = null;
    }

    //맵을 참고하지 않는 그래프 생성 함수
    public void graph_list_recreate(Context context, boolean[] current_view_num_bool, FrameLayout main_map_layout, int view_num, int title, int subtitle, int[] main_local_num)
    {
        Statistical_Data main_graph = new Statistical_Data();
        Statistical_Data_Structure  main_graph_data = new Statistical_Data_Structure(context, main_map_layout, null, view_num, title, subtitle, main_local_num);

        if(current_view_num_bool[view_num] == true)
        {
            main_graph_data.set_graph_status(true, true);
        }

        main_graph.execute(main_graph_data);

        main_graph = null;
        main_graph_data = null;
    }

    //지역 데이터 그래프 자동 생성 함수
    public void graph_auto_create(int local_num, boolean[] current_view_num_bool, Context context, Map_Draw map_draw, FrameLayout main_map_layout, int view_num, int title, int subtitle, int[] main_local_num)
    {
        if(current_view_num_bool[view_num] == true)
        {
            //그래프에 지역이 이미 있는지 확인하는 변수
            boolean local_boolean = false;

            for(int i=0;i<main_local_num.length;i++)
            {
                if(main_local_num[i] == local_num)
                {
                    local_boolean = true;
                    continue;
                }
                if(local_boolean == true)
                {
                    main_local_num[i-1] = main_local_num[i];
                }
            }
            if(local_boolean == false)
            {
                for(int i=1;i<main_local_num.length;i++)
                {
                    main_local_num[i-1] = main_local_num[i];
                }
            }
            main_local_num[main_local_num.length-1] = local_num;

            graph_base_create(context, current_view_num_bool, map_draw, main_map_layout, view_num, title, subtitle, main_local_num);
        }
    }

    //그래프 제거 기초 함수
    public void graph_base_remove(FrameLayout main_map_layout, int view_num)
    {
        int VIEW_BASE_ID = 111000;
        main_map_layout.removeView(main_map_layout.findViewById(VIEW_BASE_ID + (2*view_num)));
        main_map_layout.removeView(main_map_layout.findViewById(VIEW_BASE_ID + (2*view_num) + 1));
    }

    //지정한 뷰의 내용으로 마커 변경
    public void change_marker(boolean[] current_view_num_bool, int view_num, Map_Draw map_draw, MainActivity.graph_structure graph_structure)
    {
        if(current_view_num_bool[view_num] == true)
        {
            for(int i=0;i<map_draw.map_local_marker.length;i++)
            {
                for(int j=0;j<graph_structure.main_local_num.length;j++)
                {
                    //마커로 표시된 지역이 local_num[j]에 해당하지 않는 경우
                    //if((map_draw.map_local_marker_bool[i] == true) && (i != local_num[j]))
                    if(i == graph_structure.main_local_num[j])
                    {
                        map_draw.map_local_marker[i].setPosition(map_draw.map_local_pos[i]);
                        map_draw.map_local_marker[i].setMap(map_draw.naver_map);

                        //데이터 색 가져오기
                        map_draw.map_local_marker[i].setIconTintColor(map_draw.map_marker_color[view_num][i]);
                        break;
                    }
                    else
                    {
                        map_draw.map_local_marker[i].setMap(null);
                    }
                }
            }
        }
    }

    //지정한 뷰의 마커 지우기
    public void remove_maker(Map_Draw map_draw)
    {
        for(int i=0;i<map_draw.map_local_marker.length;i++)
        {
            map_draw.map_local_marker[i].setMap(null);
        }
    }

    //지도를 참고하지 않는 그래프 리셋 함수
    public void graph_reset(boolean[] current_view_num_bool, Context context, FrameLayout main_map_layout, int current_view_num, int title, int subtitle, int[] main_local_num)
    {
        if(current_view_num_bool[current_view_num] == true)
        {
            graph_base_remove(main_map_layout, current_view_num);
            current_view_num_bool[current_view_num] = false;
        }

        if(current_view_num_bool[current_view_num] == false)
        {
            graph_list_recreate(context, current_view_num_bool, main_map_layout, current_view_num, title, subtitle, main_local_num);
            current_view_num_bool[current_view_num] = true;
        }
    }

    //버튼 클릭 함수
    public void seoul_button_onclick(boolean[] current_view_num_bool, Context context, Map_Draw map_draw, FrameLayout main_map_layout, int view_num, int title, int subtitle, int[] main_local_num)
    {
        graph_auto_create(0, current_view_num_bool, context, map_draw, main_map_layout, view_num, title, subtitle, main_local_num);
    }
    public void busan_button_onclick(boolean[] current_view_num_bool, Context context, Map_Draw map_draw, FrameLayout main_map_layout, int view_num, int title, int subtitle, int[] main_local_num)
    {
        graph_auto_create(1, current_view_num_bool,  context, map_draw, main_map_layout, view_num, title, subtitle, main_local_num);
    }
    public void deagu_button_onclick(boolean[] current_view_num_bool, Context context, Map_Draw map_draw, FrameLayout main_map_layout, int view_num, int title, int subtitle, int[] main_local_num)
    {
        graph_auto_create(2, current_view_num_bool, context, map_draw, main_map_layout, view_num, title, subtitle, main_local_num);
    }
    public void incheon_button_onclick(boolean[] current_view_num_bool, Context context, Map_Draw map_draw, FrameLayout main_map_layout, int view_num, int title, int subtitle, int[] main_local_num)
    {
        graph_auto_create(3, current_view_num_bool, context, map_draw, main_map_layout, view_num, title, subtitle, main_local_num);
    }
    public void gwangju_button_onclick(boolean[] current_view_num_bool, Context context, Map_Draw map_draw, FrameLayout main_map_layout, int view_num, int title, int subtitle, int[] main_local_num)
    {
        graph_auto_create(4, current_view_num_bool, context, map_draw, main_map_layout, view_num, title, subtitle, main_local_num);
    }
    public void daejeon_button_onclick(boolean[] current_view_num_bool, Context context, Map_Draw map_draw, FrameLayout main_map_layout, int view_num, int title, int subtitle, int[] main_local_num)
    {
        graph_auto_create(5, current_view_num_bool, context, map_draw, main_map_layout, view_num, title, subtitle, main_local_num);
    }
    public void ulsan_button_onclick(boolean[] current_view_num_bool, Context context, Map_Draw map_draw, FrameLayout main_map_layout, int view_num, int title, int subtitle, int[] main_local_num)
    {
        graph_auto_create(6, current_view_num_bool, context, map_draw, main_map_layout, view_num, title, subtitle, main_local_num);
    }
    public void sejong_button_onclick(boolean[] current_view_num_bool, Context context, Map_Draw map_draw, FrameLayout main_map_layout, int view_num, int title, int subtitle, int[] main_local_num)
    {
        graph_auto_create(7, current_view_num_bool,  context, map_draw, main_map_layout, view_num, title, subtitle, main_local_num);
    }
    public void graph_add_onclick(boolean[] current_view_num_bool, Context context,  Map_Draw map_draw, FrameLayout main_map_layout, int current_view_num, int title, int subtitle, int[] main_local_num)
    {
        if(current_view_num_bool[current_view_num] == false)
        {
            graph_base_create(context, current_view_num_bool, map_draw, main_map_layout, current_view_num, title, subtitle, main_local_num);
            current_view_num_bool[current_view_num] = true;
        }
    }
    public void graph_remove_onclick(boolean[] current_view_num_bool, Map_Draw map_draw, FrameLayout main_map_layout, int current_view_num)
    {
        if(current_view_num_bool[current_view_num] == true)
        {
            graph_base_remove(main_map_layout, current_view_num);
            remove_maker(map_draw);
            current_view_num_bool[current_view_num] = false;
        }
    }
    public void title_select_onclick()
    {
        //
    }
    public void subtitle_select_onclick()
    {
        //
    }
    public int change_next_onclick(boolean[] current_view_num_bool, MainActivity.graph_structure[] graph_structure, Map_Draw map_draw, int current_view_num, int MAX_VIEW_NUM)
    {
        if(current_view_num < MAX_VIEW_NUM-1)
        {
            if(current_view_num_bool[current_view_num+1] == true)
            {
                change_marker(current_view_num_bool, current_view_num+1, map_draw, graph_structure[current_view_num+1]);
            }
            else
            {
                remove_maker(map_draw);
            }
            return current_view_num + 1;
        }
        else
        {
            if(current_view_num_bool[0] == true)
            {
                change_marker(current_view_num_bool, 0, map_draw, graph_structure[0]);
            }
            else
            {
                remove_maker(map_draw);
            }
            return 0;
        }

    }
    public void statistic_color_onclick(Context context, boolean[] current_view_num_bool, Map_Draw map_draw, FrameLayout main_map_layout, int view_num, int title, int subtitle, int[] main_local_num)
    {
        if(current_view_num_bool[view_num] == true)
        {
            graph_base_create(context, current_view_num_bool, map_draw, main_map_layout, view_num, title, subtitle, main_local_num);
        }
    }
}
