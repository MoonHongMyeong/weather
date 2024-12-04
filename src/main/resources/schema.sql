-- 단기예보(SHRT) 정보 테이블
CREATE TABLE short_forecast (
    id BIGINT AUTO_INCREMENT,                    -- 기본키
    nx VARCHAR(3) NOT NULL,                      -- 예보지점 X 좌표
    ny VARCHAR(3) NOT NULL,                      -- 예보지점 Y 좌표
    base_date VARCHAR(8) NOT NULL,               -- 발표일자 (yyyyMMdd)
    base_time VARCHAR(4) NOT NULL,               -- 발표시각 (HHmm)
    fcst_date VARCHAR(8) NOT NULL,               -- 예보일자 (yyyyMMdd)
    fcst_time VARCHAR(4) NOT NULL,               -- 예보시각 (HHmm)
    category VARCHAR(3) NOT NULL,                -- 자료구분코드
    fcst_value VARCHAR(10) NOT NULL,             -- 예보값
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 생성일시
    
    CONSTRAINT pk_village_forecast_shrt PRIMARY KEY (id),
    CONSTRAINT uk_village_forecast_shrt UNIQUE (nx, ny, fcst_date, fcst_time, category)
);

-- 중기예보 공통 테이블
CREATE TABLE mid_forecast (
    reg_id VARCHAR(10) NOT NULL,         -- 예보구역코드
    tm_fc VARCHAR(12) NOT NULL,          -- 발표시각
    tm_ef VARCHAR(12) NOT NULL,          -- 예보시각
    mod VARCHAR(3) NOT NULL,             -- 발표시간코드
    stn INT NOT NULL,                    -- 발표관서
    c INT,                               -- 신뢰도
    sky VARCHAR(4),                      -- 하늘상태코드
    pre VARCHAR(4),                      -- 강수형태코드
    conf VARCHAR(10),                    -- 신뢰도
    wf VARCHAR(20),                      -- 날씨
    rn_st INT DEFAULT 0,                 -- 강수확률
    min_temp INT,                             -- 최저기온
    max_temp INT,                             -- 최고기온
    min_l INT DEFAULT 0,                 -- 최저기온 하한
    min_h INT DEFAULT 0,                 -- 최저기온 상한
    max_l INT DEFAULT 0,                 -- 최고기온 하한
    max_h INT DEFAULT 0,                 -- 최고기온 상한
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_mid_forecast PRIMARY KEY (reg_id, tm_ef, mod)
);

-- 기상특보 테이블
CREATE TABLE weather_warn (
    reg_id VARCHAR(10) NOT NULL,         -- 예보구역코드
    tm_fc VARCHAR(12) NOT NULL,          -- 발표시각
    tm_ef VARCHAR(12) NOT NULL,          -- 예보시각
    tm_in VARCHAR(12) NOT NULL,          -- 특보발표시각
    stn INT NOT NULL,                    -- 발표관서
    wrn VARCHAR(10),                     -- 특보종류
    lvl VARCHAR(10),                     -- 특보등급
    cmd VARCHAR(10),                     -- 특보상태
    grd VARCHAR(10),                     -- 특보강도
    cnt INT,                             -- 특보횟수
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_weather_warn PRIMARY KEY (reg_id, tm_ef)
);

-- 환경지수 테이블 (자외선지수, 대기정체지수)
CREATE TABLE environmental_index (
    code VARCHAR(5) NOT NULL,            -- 지수 코드 (UV: 자외선, AD: 대기정체)
    area_no VARCHAR(10) NOT NULL,         -- 지역 코드
    fcst_date VARCHAR(10) NOT NULL,            -- 발표일자 (yyyyMMddHH)
    fcst_value VARCHAR(3) DEFAULT '0',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_environmental_index PRIMARY KEY (code, area_no, fcst_date)
);