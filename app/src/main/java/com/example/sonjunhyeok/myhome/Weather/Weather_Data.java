package com.example.sonjunhyeok.myhome.Weather;

import java.io.Serializable;

public class Weather_Data implements Serializable {
    private String Type;
    private String POP; //강수확률,%
    private String PTY; //강수형태, 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)
    private String R06; //6시간 강수량 ,1mm
    private String REH; //습도 %
    private String S06; //6시간 적설, 1cm
    private String SKY; //하늘상태, 맑음(1), 비(2), 구름많음(3), 흐림(4)
    private String T3H; //3시간 기온, ℃
    private String TMN; //아침 최저기온, ℃
    private String TMX; //낮 최고기온 ,℃
    private String UUU; //풍속(동서풍), m/s. + : 동, - : 서
    private String VVV; //풍속(남북풍), m/s, + : 북, - : 남
    private String WAV; //파고
    private String VEC; //풍향
    private String WSD; //풍속
    private String T1H; //기온, ℃
    private String RN1; //1시간 강수량, 1mm
    private String LGT; //낙뢰, 초단기실황[없음(0), 있음(1)], 초단기예보[없음(0), 낮음(1), 보통(2), 높음(3)] - 삭제
    private String fcstDate; //예보 날짜
    private String fcstTime; //예보 시간

    public String getFcstDate() {
        return fcstDate;
    }

    public void setFcstDate(String fcstDate) {
        this.fcstDate = fcstDate;
    }

    public String getFcstTime() {
        return fcstTime;
    }

    public void setFcstTime(String fcstTime) {
        this.fcstTime = fcstTime;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getPOP() {
        return POP;
    }

    public void setPOP(String POP) {
        this.POP = POP;
    }

    public String getPTY() {
        return PTY;
    }

    public void setPTY(String PTY) {
        this.PTY = PTY;
    }

    public String getR06() {
        return R06;
    }

    public void setR06(String r06) {
        R06 = r06;
    }

    public String getREH() {
        return REH;
    }

    public void setREH(String REH) {
        this.REH = REH;
    }

    public String getS06() {
        return S06;
    }

    public void setS06(String s06) {
        S06 = s06;
    }

    public String getSKY() {
        return SKY;
    }

    public void setSKY(String SKY) {
        this.SKY = SKY;
    }

    public String getT3H() {
        return T3H;
    }

    public void setT3H(String t3H) {
        T3H = t3H;
    }

    public String getTMN() {
        return TMN;
    }

    public void setTMN(String TMN) {
        this.TMN = TMN;
    }

    public String getTMX() {
        return TMX;
    }

    public void setTMX(String TMX) {
        this.TMX = TMX;
    }

    public String getUUU() {
        return UUU;
    }

    public void setUUU(String UUU) {
        this.UUU = UUU;
    }

    public String getVVV() {
        return VVV;
    }

    public void setVVV(String VVV) {
        this.VVV = VVV;
    }

    public String getWAV() {
        return WAV;
    }

    public void setWAV(String WAV) {
        this.WAV = WAV;
    }

    public String getVEC() {
        return VEC;
    }

    public void setVEC(String VEC) {
        this.VEC = VEC;
    }

    public String getWSD() {
        return WSD;
    }

    public void setWSD(String WSD) {
        this.WSD = WSD;
    }

    public String getT1H() {
        return T1H;
    }

    public void setT1H(String t1H) {
        T1H = t1H;
    }

    public String getRN1() {
        return RN1;
    }

    public void setRN1(String RN1) {
        this.RN1 = RN1;
    }

    public String getLGT() {
        return LGT;
    }

    public void setLGT(String LGT) {
        this.LGT = LGT;
    }
    public void print_UltraNcst(){
        System.out.println("*********UltraNcst***********");
        System.out.println("습도 : "+this.REH);
        System.out.println("동서풍 : "+this.UUU);
        System.out.println("북남풍 : "+this.VVV);
        System.out.println("기온 : "+this.T1H);
        System.out.println("강수형태 : "+this.PTY);
        System.out.println("풍향 : "+this.VEC);
        System.out.println("풍속 : "+this.WSD);
        System.out.println("1시간강수량 : " + this.RN1);
    }
    public void print_UltraFcst(){
        System.out.println("*********UltraFcst***********");
        System.out.println("기온 : "+this.T1H);
        System.out.println("1시간강수량 : "+this.RN1);
        System.out.println("하늘상태: "+this.SKY);
        System.out.println("동서바람성분: "+this.UUU);
        System.out.println("남북바람성분: "+this.VVV);
        System.out.println("습도: "+this.REH);
        System.out.println("강수형태 : "+this.PTY);
        System.out.println("낙뢰 : "+this.LGT);
        System.out.println("풍향 : "+this.VEC);
        System.out.println("풍속 : "+this.WSD);
    }
    public void print_VilageFcst(){
        System.out.println("*********VilageFcst***********");
        System.out.println("강수확률 : " + this.POP);
        System.out.println("강수형태 : " + this.PTY);
        System.out.println("6시간강수량: " + this.R06);
        System.out.println("습도 : " + this.REH);
        System.out.println("6시간신적설 : " + this.S06);
        System.out.println("하늘상태 : " + this.SKY);
        System.out.println("3시간기온: " + this.T3H);
        System.out.println("동서바람속도 : " + this.UUU);
        System.out.println("남북바람속도 : " + this.VVV);
        System.out.println("풍향 : " + this.VEC);
        System.out.println("풍속 : " + this.WSD);
    }
}
