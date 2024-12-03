-- 단계예보 서비스 버전확인 테이블
CREATE TABLE village_forecast_version (
    version VARCHAR(12) NOT NULL,        -- YYYYMMDDHHmm
    file_type VARCHAR(4) NOT NULL,       -- ODAM/VSRT
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_village_forecast_version PRIMARY KEY (file_type, version DESC)
);

-- 단기예보(SHRT) 정보 테이블
CREATE TABLE village_forecast_shrt (
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
    CONSTRAINT uk_village_forecast_shrt UNIQUE (nx, ny, base_date, base_time, category)
);

-- 자주 사용되는 조회 조건에 대한 인덱스
CREATE INDEX idx_village_forecast_shrt_location_time 
    ON village_forecast_shrt (nx, ny, base_date, base_time);

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
    min INT,                             -- 최저기온
    max INT,                             -- 최고기온
    min_l INT DEFAULT 0,                 -- 최저기온 하한
    min_h INT DEFAULT 0,                 -- 최저기온 상한
    max_l INT DEFAULT 0,                 -- 최고기온 하한
    max_h INT DEFAULT 0,                 -- 최고기온 상한
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_mid_forecast PRIMARY KEY (reg_id, tm_fc, tm_ef, mod)
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
    
    CONSTRAINT pk_weather_warn PRIMARY KEY (reg_id, tm_fc, tm_ef, tm_in)
);

-- 자주 사용되는 조회를 위한 인덱스
CREATE INDEX idx_weather_warn_time 
    ON weather_warn (reg_id,tm_fc, tm_ef);

-- 환경지수 테이블 (자외선지수, 대기정체지수)
CREATE TABLE environmental_index (
    code VARCHAR(2) NOT NULL,            -- 지수 코드 (UV: 자외선, AD: 대기정체)
    area_no VARCHAR(5) NOT NULL,         -- 지역 코드
    date VARCHAR(8) NOT NULL,            -- 발표일자 (yyyyMMdd)
    h3 VARCHAR(10),                      -- 3시간 예보
    h6 VARCHAR(10),                      -- 6시간 예보
    h9 VARCHAR(10),                      -- 9시간 예보
    h12 VARCHAR(10),                     -- 12시간 예보
    h15 VARCHAR(10),                     -- 15시간 예보
    h18 VARCHAR(10),                     -- 18시간 예보
    h21 VARCHAR(10),                     -- 21시간 예보
    h24 VARCHAR(10),                     -- 24시간 예보
    h27 VARCHAR(10),                     -- 27시간 예보
    h30 VARCHAR(10),                     -- 30시간 예보
    h33 VARCHAR(10),                     -- 33시간 예보
    h36 VARCHAR(10),                     -- 36시간 예보
    h39 VARCHAR(10),                     -- 39시간 예보
    h42 VARCHAR(10),                     -- 42시간 예보
    h45 VARCHAR(10),                     -- 45시간 예보
    h48 VARCHAR(10),                     -- 48시간 예보
    h51 VARCHAR(10),                     -- 51시간 예보
    h54 VARCHAR(10),                     -- 54시간 예보
    h57 VARCHAR(10),                     -- 57시간 예보
    h60 VARCHAR(10),                     -- 60시간 예보
    h63 VARCHAR(10),                     -- 63시간 예보
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_environmental_index PRIMARY KEY (code, area_no, date)
);

-- 조회 성능 향상을 위한 인덱스
CREATE INDEX idx_environmental_index_date 
    ON environmental_index (code, date);