-- 단계예보 서비스 버전확인 테이블
CREATE TABLE village_forecast_version (
    version VARCHAR(12) NOT NULL,        -- YYYYMMDDHHmm
    file_type VARCHAR(4) NOT NULL,       -- ODAM/VSRT/SHRT
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_village_forecast_version PRIMARY KEY (file_type, version DESC)
);