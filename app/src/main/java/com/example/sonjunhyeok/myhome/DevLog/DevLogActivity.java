package com.example.sonjunhyeok.myhome.DevLog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.sonjunhyeok.myhome.R;

import java.util.ArrayList;
import java.util.List;

public class DevLogActivity extends AppCompatActivity {

    private DevLogAdapter devLogAdapter;
    private RecyclerView recyclerview;

    private DevLogAdapter.Item ver1, ver2, ver3, ver4, ver5, ver6, ver7, ver8, ver9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_log);

        recyclerview = findViewById(R.id.Dev_recyclerView);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<DevLogAdapter.Item> data = new ArrayList<>();

        /*data.add(new DevLogAdapter.Item(DevLogAdapter.HEADER, "Ver 0.1"));
        data.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "Apple"));
        data.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "Orange"));
        data.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "Banana"));
        data.add(new DevLogAdapter.Item(DevLogAdapter.HEADER, "Ver 0.2"));
        data.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "Audi"));
        data.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "Aston Martin"));
        data.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "BMW"));
        data.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "Cadillac"));*/

        ver1 = new DevLogAdapter.Item(DevLogAdapter.HEADER, "Ver 0.1");
        ver2 = new DevLogAdapter.Item(DevLogAdapter.HEADER, "Ver 0.2");
        ver3 = new DevLogAdapter.Item(DevLogAdapter.HEADER, "Ver 0.3");
        ver4 = new DevLogAdapter.Item(DevLogAdapter.HEADER, "Ver 0.4");
        ver5 = new DevLogAdapter.Item(DevLogAdapter.HEADER, "Ver 0.5");
        ver6 = new DevLogAdapter.Item(DevLogAdapter.HEADER, "Ver 0.6");
        ver7 = new DevLogAdapter.Item(DevLogAdapter.HEADER, "Ver 0.7");
        ver8 = new DevLogAdapter.Item(DevLogAdapter.HEADER, "Ver 0.8");
        ver9 = new DevLogAdapter.Item(DevLogAdapter.HEADER, "Ver 0.9");

        Ver1();
        Ver2();
        Ver3();
        Ver4();
        Ver5();
        Ver6();
        Ver7();
        Ver8();
        Ver9();

        data.add(ver1);
        data.add(ver2);
        data.add(ver3);
        data.add(ver4);
        data.add(ver5);
        data.add(ver6);
        data.add(ver7);
        data.add(ver8);
        data.add(ver9);

        recyclerview.setAdapter(new DevLogAdapter(data));
    }
    private void Ver1(){
        ver1.invisibleChildren = new ArrayList<>();
        ver1.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "FTP 파일 업로드, 다운로드 기능"));
        ver1.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "GridView 형식 지원"));
        ver1.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "메뉴바 형식 지원"));
        ver1.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "서버 폴더 생성 기능"));
    }
    private void Ver2(){
        ver2.invisibleChildren = new ArrayList<>();
        ver2.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "개발자 로그 추가"));
        ver2.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "FTP 새로고침 추가"));
        ver2.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "RecyclerView로 대체"));
        ver2.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "내부 탐색 기능 추가"));
        ver2.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "내부 파일 삭제 및 추가 시 자동 새로고침"));
        ver2.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "폴더 및 파일 이미지 구분"));
        ver2.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "폴더 및 파일 이름 변경 추가"));
        ver2.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "서버상 폴더 및 파일 삭제, 부분 복구 기능"));
    }
    private void Ver3(){
        ver3.invisibleChildren = new ArrayList<>();
        ver3.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "로그인 기능 활성화"));
        ver3.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "자동 로그인 기능 추가"));
        ver3.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "회원가입 추가"));
    }
    private void Ver4(){
        ver4.invisibleChildren = new ArrayList<>();
        ver4.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "세팅 기능 활성화"));
        ver4.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "회원 정보 수정 기능 추가"));
        ver4.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "어플 내부 최신버전 확인 및 업데이트 기능 추가"));
        ver4.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "개발자 로그 미 출력 버그 픽스"));
        ver4.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "일부 영어 UI 한글화"));
        ver4.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "회원가입 정보 입력란 샘플 텍스트로 변경"));
        ver4.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "메인 화면 메뉴에 사용자 이름 및 아이디 출력"));
    }
    private void Ver5(){
        ver5.invisibleChildren = new ArrayList<>();
        ver5.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "파일 서버 개인 폴더, 공용 폴더 이원화"));
        ver5.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "기존 서버내 복원 폴더 삭제"));
        ver5.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "서버내 삭제된 파일 및 폴더 기존 위치로 복구 기능 추가"));
        ver5.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "파일 업로드 위치 표시 간결화"));
        ver5.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "파일 서버 처리속도 최적화 및 개선"));
        ver5.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "서버 내 파일 크기 표시"));
        ver5.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "파일 서버 기능 중 오탈자 및 일부 문구 변경"));
    }
    private void Ver6(){
        ver6.invisibleChildren = new ArrayList<>();
        ver6.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "전체적인 UI 개편"));
        ver6.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "업데이트 확인 관련 버그 수정"));
        ver6.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "로그아웃 기능 추가"));
        ver6.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "자동 로그인 해제 기능 추가"));
        ver6.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "실내 조명 컨트롤 기능 추가 (작은방만 가능)"));
    }
    private void Ver7(){
        ver7.invisibleChildren = new ArrayList<>();
        ver7.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "실행 시 로딩 화면 추가"));
        ver7.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "날씨 기능 추가"));
        ver7.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "공지사항 기능 추가"));
        ver7.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "메인화면 위젯 추가"));
        ver7.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "실내 조명코드 수정(http -> MQTT)"));
    }
    private void Ver8(){
        ver8.invisibleChildren = new ArrayList<>();
        ver8.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "알림 기능 활성화"));
        ver8.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "실내 조명코드 수정"));
    }
    private void Ver9(){
        ver9.invisibleChildren = new ArrayList<>();
        ver9.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "실내 조명 전용화면 활성화"));
        ver9.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "실내 조명 예약 기능 추가"));
        ver9.invisibleChildren.add(new DevLogAdapter.Item(DevLogAdapter.CHILD, "실내 조명 즐겨찾기 기능 추가"));
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
    }
}