package com.example.map_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.Random;

public class Graph_Element extends View {

    private Paint paint_element_draw;

    //그래프 전체 위치
    float GRAPH_POS_X, GRAPH_POS_Y;

    //그래프 배경 뷰 관련 구조체(클래스)
    public class graph_background_structure
    {
        //배경 위치(왼쪽 위) 좌표
        float background_pos_x, background_pos_y;

        //배경 크기
        float background_width, background_height;

        //배경색
        int background_color;

        //구조체 기본 초기화
        graph_background_structure()
        {
            //그래프 배경 위치 초기화
            background_pos_x = GRAPH_POS_X;
            background_pos_y = GRAPH_POS_Y;

            //그래프 배경 크기 초기화
            background_width = 0;
            background_height = 0;

            //그래프 배경색 초기화
            background_color = Color.WHITE;
        }

        //구조체 배경색 초기화
        graph_background_structure(int color)
        {
            //그래프 배경 위치 초기화
            background_pos_x = GRAPH_POS_X;
            background_pos_y = GRAPH_POS_Y;

            //그래프 배경 크기 초기화
            background_width = 0;
            background_height = 0;

            //그래프 배경색 초기화
            background_color = color;
        }
    }

    //그래프 데이터 뷰 관련 구조체(클래스)
    public class graph_data_structure
    {
        //데이터 위치(왼쪽 위) 좌표
        float[] data_pos_x, data_pos_y;

        //데이터 크기
        float[] data_width, data_height;

        //그래프 바닥 높이
        float floor_height;

        //데이터 색
        int[] data_color;

        //색을 결정하기 위한 랜덤 숫자
        Random rand_num;

        //데이터 수에 따른 배열 할당, 초기화
        public void graph_data_array_init(int data_num)
        {
            //데이터 배경 위치 할당
            data_pos_x = new float[data_num];
            data_pos_y = new float[data_num];

            //데이터 배경 크기 할당
            data_width = new float[data_num];
            data_height = new float[data_num];

            //데이터 색 할당
            data_color = new int[data_num];

            //색을 결정하기 위한 랜덤 숫자 할당
            rand_num = new Random();

            for(int i=0;i<data_num;i++)
            {
                //데이터 배경 위치 초기화
                data_pos_x[i] = GRAPH_POS_X;
                data_pos_y[i] = GRAPH_POS_Y;

                //데이터 높이, 너비 초기화
                data_width[i] = 0;
                data_height[i] = 0;

                //그래프 바닥 높이 초기화
                floor_height = 0;

                //데이터 색 초기화
                init_data_color(data_num);
            }
        }

        public void init_data_color(int data_num)
        {
            for(int i=0;i<data_num;i++)
            {
                data_color[i] = Color.rgb((int)(255*((1+Math.sin((Math.PI/4)*rand_num.nextInt(7)))/2.8)), (int)(255*((1+Math.sin((Math.PI/4)*rand_num.nextInt(7)))/2.8)), (int)(255*((1+Math.sin((Math.PI/4)*rand_num.nextInt(7)))/2.8)));
            }
        }
    }

    public class graph_text_structure
    {
        //그래프 부제목 텍스트
        String graph_title;

        //그래프 부제목 텍스트
        String graph_subtitle;

        //그래프 제목 좌표
        float title_pos_x, title_pos_y;

        //그래프 지역 이름 텍스트
        String[] data_text;

        //데이터별 텍스트 위치(왼쪽 위) 좌표
        float[] text_pos_x, text_pos_y;

        //데이터 텍스트 크기
        float text_width, text_height;

        //그래프 제목 색

        //데이터별 색 = 막대그래프의 데이터 색

        //데이터 수에 따른 배열 할당, 초기화
        public void graph_text_array_init(int data_num)
        {
            //그래프 지역 이름 텍스트 할당, 초기화는 나중에 작업

            //각 데이터별 텍스트 위치 할당
            text_pos_x = new float[data_num];
            text_pos_y = new float[data_num];
        }
    }

    //사용할 그래프 배경, 맵 데이터 선언
    graph_background_structure graph_background;
    graph_data_structure graph_data;
    graph_text_structure graph_text;

    @SuppressLint({"CutPasteId", "WrongViewCast"})
    public Graph_Element(Context context)
    {
        super(context);

        //그래프 위치 지정
        GRAPH_POS_X = 0;
        GRAPH_POS_Y = 0;

        //사용할 그래프 배경, 데이터 할당
        graph_background = new graph_background_structure();
        graph_data = new graph_data_structure();
        graph_text = new graph_text_structure();

        graph_background.background_width = 300;
        graph_background.background_height = 500;
    }

    //그래프 배경 그리기
    public void draw_graph_background(Canvas canvas, int view_num)
    {
        paint_element_draw = new Paint();

        //테두리 크기
        int margin = 8;

        //view_num에 따라 테두리 색이 다름
        switch(view_num)
        {
            case 0:
                //빨강색
                paint_element_draw.setColor(Color.parseColor("#F15F5F"));
                break;
            case 1:
                //노랑색
                paint_element_draw.setColor(Color.parseColor("#F2CB61"));
                break;
            case 2:
                //초록색
                paint_element_draw.setColor(Color.parseColor("#47C83E"));
                break;
            case 3:
                //파랑색
                paint_element_draw.setColor(Color.parseColor("#6799FF"));
                break;
            default:
                //회색
                paint_element_draw.setColor(Color.parseColor("#8C8C8C"));
                break;
        }
        //기본 그래프 테두리(일반 사각형)
        canvas.drawRect(graph_background.background_pos_x + GRAPH_POS_X, graph_background.background_pos_y + GRAPH_POS_Y, graph_background.background_pos_x + GRAPH_POS_X + graph_background.background_width, graph_background.background_pos_y + GRAPH_POS_Y + graph_background.background_height, paint_element_draw);

        //기본 그래프 배경(일반 사각형)
        paint_element_draw.setColor(Color.WHITE);
        canvas.drawRect(graph_background.background_pos_x + GRAPH_POS_X + margin, graph_background.background_pos_y + GRAPH_POS_Y + margin, graph_background.background_pos_x + GRAPH_POS_X + graph_background.background_width - margin, graph_background.background_pos_y + GRAPH_POS_Y + graph_background.background_height - margin, paint_element_draw);
    }

    //그래프 데이터 그리기
    public void draw_graph_data(Canvas canvas, int data_num, float[] data_value)
    {
        paint_element_draw = new Paint();

        //그래프 바닥높이
        graph_data.floor_height = graph_background.background_height/8;

        //제일 큰 데이터 값, 임시 데이터 값
        float MAX_DATA_VALUE = 0, temp_value = -1;

        //제일 큰 데이터 값 찾기
        for(int i=0;i<data_num;i++)
        {
            temp_value = data_value[i];
            if(MAX_DATA_VALUE < temp_value)
            {
                MAX_DATA_VALUE = temp_value;
            }
        }

        //각 데이터마다 그래프 그리기
        for(int i=0;i<data_num;i++)
        {
            //데이터 색
            paint_element_draw.setColor(graph_data.data_color[i]);

            //데이터 막대 너비, x축 위치 구하기
            graph_data.data_width[i] = graph_background.background_width/(2*data_num);
            graph_data.data_pos_x[i] = graph_background.background_pos_x + (graph_background.background_width)*(4*i+1)/(4*data_num);

            //데이터 막대 높이, y축 위치 구하기
            graph_data.data_height[i] = (graph_background.background_height - 4*graph_data.floor_height)*(data_value[i])/(MAX_DATA_VALUE);
            graph_data.data_pos_y[i] = graph_background.background_pos_y + graph_background.background_height - (2*graph_data.floor_height) - graph_data.data_height[i];

            canvas.drawRect(graph_data.data_pos_x[i], graph_data.data_pos_y[i], graph_data.data_pos_x[i]+graph_data.data_width[i], graph_data.data_pos_y[i]+graph_data.data_height[i], paint_element_draw);
        }
    }

    //그래프 지역정보 입력
    public void set_local_data(String title, String subtitle, String[] local_data)
    {
        int temp_num_1;
        int temp_num_2;

        //그래프 부제목 텍스트
        temp_num_1 = title.indexOf("(");
        temp_num_2 = title.indexOf("＜");

        graph_text.graph_title = title;

        if(temp_num_1 > 0)
        {
            graph_text.graph_title = graph_text.graph_title.substring(0, temp_num_1);
        }
        if(temp_num_2 > 0)
        {
            graph_text.graph_title = graph_text.graph_title.substring(0, temp_num_2);
        }

        //그래프 부제목 텍스트
        temp_num_1 = subtitle.indexOf("(");
        temp_num_2 = subtitle.indexOf("＜");

        graph_text.graph_subtitle = subtitle;

        if(temp_num_1 > 0)
        {
            graph_text.graph_subtitle = graph_text.graph_subtitle.substring(0, temp_num_1);
        }
        if(temp_num_2 > 0)
        {
            graph_text.graph_subtitle = graph_text.graph_subtitle.substring(0, temp_num_2);
        }

        //그래프 지역 이름 텍스트 할당, 초기화
        graph_text.data_text = new String[local_data.length];
        for(int i=0;i<local_data.length;i++)
        {
            graph_text.data_text[i] = local_data[i];
        }
    }

    //그래프 정보(글자) 그리기
    public void draw_graph_info(Canvas canvas, int data_num, float[] data_value)
    {
        paint_element_draw = new Paint();

        graph_text.graph_text_array_init(data_num);

        /*
        //데이터 막대 너비
        graph_text.text_width = 9*graph_background.background_width/(8*data_num);
        //데이터 막대 높이
        graph_text.text_height = (7/8)*graph_data.floor_height;
        */

        int text_size = 30;
        int margin = 20;

        paint_element_draw.setTextSize(text_size);

        //그래프 제목 그리기
        paint_element_draw.setColor(Color.BLACK);
        canvas.drawText(graph_text.graph_title, graph_background.background_pos_x+margin, graph_background.background_pos_y+text_size+5, paint_element_draw);
        canvas.drawText(graph_text.graph_subtitle, graph_background.background_pos_x+margin, graph_background.background_pos_y+(2*text_size)+5, paint_element_draw);

        for(int i=0;i<data_num;i++)
        {
            //데이터 텍스트 색 구하기
            paint_element_draw.setColor(graph_data.data_color[i]);

            //x축 위치 구하기
            graph_text.text_pos_x[i] = graph_background.background_pos_x + (graph_background.background_width)*(8*i+1)/(8*data_num);

            //y축 위치 구하기
            graph_text.text_pos_y[i] = graph_background.background_pos_y + graph_background.background_height - ((9/8)*graph_data.floor_height);

            //데이터의 텍스트는 Statistical_Data에서 지정됨

            canvas.drawText(String.valueOf(data_value[i]), graph_text.text_pos_x[i], graph_text.text_pos_y[i], paint_element_draw);
            canvas.drawText(graph_text.data_text[i], graph_text.text_pos_x[i], graph_text.text_pos_y[i] + text_size, paint_element_draw);
        }
    }

    //지역 좌표 그리기
    public void draw_map_local_position(Canvas canvas, float[][] map_local_pos)
    {
        paint_element_draw = new Paint();

        for(int i=0;i<map_local_pos.length;i++)
        {
            paint_element_draw.setColor(graph_data.data_color[i]);
            paint_element_draw.setStrokeWidth(5);
            canvas.drawLine(graph_background.background_pos_x + (graph_background.background_width/2), graph_background.background_pos_y + (graph_background.background_height/2), map_local_pos[i][0], map_local_pos[i][1], paint_element_draw);
        }

        //canvas.drawRect(graph_background.background_pos_x + GRAPH_POS_X, graph_background.background_pos_y + GRAPH_POS_Y, graph_background.background_pos_x + GRAPH_POS_X + graph_background.background_width, graph_background.background_pos_y + GRAPH_POS_Y + graph_background.background_height, paint_element_draw);
    }
}
