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

-- 중기 육상예보 테이블
CREATE TABLE mid_land_forecast (
    reg_id VARCHAR(10) NOT NULL,    -- 예보구역코드
    rn_st4_am INT,                  -- 4일후 오전 강수확률
    rn_st4_pm INT,                  -- 4일후 오후 강수확률
    rn_st5_am INT,                  -- 5일후 오전 강수확률
    rn_st5_pm INT,                  -- 5일후 오후 강수확률
    rn_st6_am INT,                  -- 6일후 오전 강수확률
    rn_st6_pm INT,                  -- 6일후 오후 강수확률
    rn_st7_am INT,                  -- 7일후 오전 강수확률
    rn_st7_pm INT,                  -- 7일후 오후 강수확률
    wf4_am VARCHAR(20),             -- 4일후 오전 날씨예보
    wf4_pm VARCHAR(20),             -- 4일후 오후 날씨예보
    wf5_am VARCHAR(20),             -- 5일후 오전 날씨예보
    wf5_pm VARCHAR(20),             -- 5일후 오후 날씨예보
    wf6_am VARCHAR(20),             -- 6일후 오전 날씨예보
    wf6_pm VARCHAR(20),             -- 6일후 오후 날씨예보
    wf7_am VARCHAR(20),             -- 7일후 오전 날씨예보
    wf7_pm VARCHAR(20),             -- 7일후 오후 날씨예보
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (reg_id)
);

-- 중기 기온예보 테이블
CREATE TABLE mid_temp_forecast (
    reg_id VARCHAR(10) NOT NULL,    -- 예보구역코드
    ta_min4 INT,                    -- 4일후 최저기온
    ta_max4 INT,                    -- 4일후 최고기온
    ta_min5 INT,                    -- 5일후 최저기온
    ta_max5 INT,                    -- 5일후 최고기온
    ta_min6 INT,                    -- 6일후 최저기온
    ta_max6 INT,                    -- 6일후 최고기온
    ta_min7 INT,                    -- 7일후 최저기온
    ta_max7 INT,                    -- 7일후 최고기온
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (reg_id)
);