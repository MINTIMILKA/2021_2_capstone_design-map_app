package com.example.map_app;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.Projection;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.MarkerIcons;

import org.w3c.dom.Text;

public class Map_Draw extends AppCompatActivity implements OnMapReadyCallback {

    //화면 크기
    int main_map_height;
    int main_map_width;

    //지도의 좌표를 화면의 좌표로 변환하기 위한 변수 선언
    Projection main_map_projection;

    //맵 레이아웃 초기화
    FrameLayout main_map_layout;

    //맵 MapFragment 초기화
    MapFragment main_map_fragment;
    FragmentManager fragment_manager;

    //각 도시의 실제 위치를 변환한 화면 위치 (0: 위도, 1: 경도)
    LatLng[] map_local_pos;

    //네이버 맵
    NaverMap naver_map;

    //각 도시의 마커
    Marker[] map_local_marker;

    //각 지도의 마커 색
    int[][] map_marker_color;

    Map_Draw(MapFragment mmp, FragmentManager fm)
    {
        main_map_fragment = mmp;
        fragment_manager = fm;

        main_map_height = 0;
        main_map_width = 0;

        map_local_pos = new LatLng[8];
        map_local_marker = new Marker[8];

        map_marker_color = new int[4][8];
    }

    //맵 초기화 (객체 생성)
    public void map_init()
    {
        if(main_map_fragment == null)
        {
            main_map_fragment = MapFragment.newInstance();
            fragment_manager.beginTransaction().add(R.id.main_map, main_map_fragment).commit();
        }

        main_map_fragment.getMapAsync(this);
    }

    //각 지역의 위도, 경도를 초기화
    public void init_map_local_pos()
    {
        //서울 시청 위도, 경도
        map_local_pos[0] = new LatLng(37.56717528939028, 126.97803054714682);

        //부산 시청 위도, 경도
        map_local_pos[1] = new LatLng(35.18000144440749, 129.07496096386438);

        //대구 시청 위도, 경도
        map_local_pos[2] = new LatLng(35.874584605847645, 128.6018415461034);

        //인천 시청 위도, 경도
        map_local_pos[3] = new LatLng(37.45614319931984, 126.70589175164325);

        //광주 시청 위도, 경도
        map_local_pos[4] = new LatLng(35.16019060741141, 126.851518713441);

        //대전 시청 위도, 경도
        map_local_pos[5] = new LatLng(36.35060357818756, 127.38485088477643);

        //울산 시청 위도, 경도
        map_local_pos[6] = new LatLng(35.53967215882703, 129.3115247902951);

        //세종 시청 위도, 경도
        map_local_pos[7] = new LatLng(36.480142682076156, 127.28876211764629);
    }

    //각 지역의 마커의 위치 가져오기
    public void init_map_local_marker()
    {
        for(int i=0;i<map_local_marker.length;i++)
        {
            map_local_marker[i] = new Marker();
            map_local_marker[i].setIcon(MarkerIcons.BLACK);
            map_local_marker[i].setPosition(map_local_pos[i]);
        }
    }

    //맵 초기화 (준비 과정)
    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap main_map)
    {
        //상수 지정
        //카메라 초기 줌 값
        final double init_zoom_value = 5.2;
        //카메라 초기 위치 값
        final LatLng init_position_value = new LatLng(35.8, 128);

        //네이버 맵 저장
        naver_map = main_map;

        //맵 유형 초기화 (그대로 유지함)
        main_map.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true);

        //맵 UI 초기화
        UiSettings main_map_ui_setting = main_map.getUiSettings();
        //줌 버튼 제거
        main_map_ui_setting.setZoomControlEnabled(false);

        //카메라 줌 초기화
        CameraUpdate camera_zoom_update = CameraUpdate.zoomTo(init_zoom_value);
        main_map.moveCamera(camera_zoom_update);

        //카메라 위치 초기화
        CameraUpdate camera_position_update = CameraUpdate.scrollTo(init_position_value);
        main_map.moveCamera(camera_position_update);

        //지역 좌표 가져오기
        init_map_local_pos();

        //각 지역의 마커의 위치 가져오기
        init_map_local_marker();

    }
}
