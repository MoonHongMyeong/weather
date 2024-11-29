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