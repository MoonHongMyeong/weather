package com.portfolio.weather.api.controller;

import com.portfolio.weather.api.data.type.WeatherImageType;
import com.portfolio.weather.api.service.ImageProxyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles({"local", "api"})
class WeatherApiControllerTest {
    @Autowired
    private ImageProxyService imageProxyService;

    @Test
    @DisplayName("날씨 이미지 조회 - 위성영상")
    void getWeatherImage_Satellite() throws Exception {
        // given
        String imageType = WeatherImageType.SATELLITE.name();
        byte[] imageByte = imageProxyService.getWeatherImage(imageType);

        // then
        assertThat(imageByte)
            .isNotNull()                                    // null이 아닌지
            .isNotEmpty()                                   // 빈 배열이 아닌지
            .hasSizeGreaterThan(1000);                     // 최소 크기 확인 (실제 이미지는 보통 1KB 이상)

        // PNG 파일 시그니처 확인 (PNG 파일은 항상 아래 바이트로 시작)
        byte[] pngSignature = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
        assertThat(Arrays.copyOfRange(imageByte, 0, 8))
                    .isEqualTo(pngSignature);                      // PNG 파일 형식인지

        // 이미지 유효성 검증
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageByte));
            assertThat(image)
                .isNotNull();                              // 실제로 이미지로 변환 가능한지
            assertThat(image.getWidth())
                .isGreaterThan(0);                         // 너비가 있는지
            assertThat(image.getHeight())
                .isGreaterThan(0);                         // 높이가 있는지
        } catch (IOException e) {
            fail("이미지 변환 실패");
        }
    }

    @Test
    @DisplayName("날씨 이미지 조회 - 레이더")
    void getWeatherImage_Radar() throws Exception {
        // given
        String imageType = WeatherImageType.RADAR.name();
        byte[] imageByte = imageProxyService.getWeatherImage(imageType);

        // then
        assertThat(imageByte)
                .isNotNull()                                    // null이 아닌지
                .isNotEmpty()                                   // 빈 배열이 아닌지
                .hasSizeGreaterThan(1000);                     // 최소 크기 확인 (실제 이미지는 보통 1KB 이상)

        // PNG 파일 시그니처 확인 (PNG 파일은 항상 아래 바이트로 시작)
        byte[] pngSignature = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
        assertThat(Arrays.copyOfRange(imageByte, 0, 8))
                .isEqualTo(pngSignature);                      // PNG 파일 형식인지

        // 이미지 유효성 검증
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageByte));
            assertThat(image)
                    .isNotNull();                              // 실제로 이미지로 변환 가능한지
            assertThat(image.getWidth())
                    .isGreaterThan(0);                         // 너비가 있는지
            assertThat(image.getHeight())
                    .isGreaterThan(0);                         // 높이가 있는지
        } catch (IOException e) {
            fail("이미지 변환 실패");
        }
    }

    @Test
    @DisplayName("날씨 이미지 조회 - 낙뢰")
    void getWeatherImage_Lightning() throws Exception {
        // given
        String imageType = WeatherImageType.LIGHTNING.name();
        byte[] imageByte = imageProxyService.getWeatherImage(imageType);

        // then
        assertThat(imageByte)
                .isNotNull()                                    // null이 아닌지
                .isNotEmpty()                                   // 빈 배열이 아닌지
                .hasSizeGreaterThan(1000);                     // 최소 크기 확인 (실제 이미지는 보통 1KB 이상)

        // PNG 파일 시그니처 확인 (PNG 파일은 항상 아래 바이트로 시작)
        byte[] pngSignature = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
        assertThat(Arrays.copyOfRange(imageByte, 0, 8))
                .isEqualTo(pngSignature);                      // PNG 파일 형식인지

        // 이미지 유효성 검증
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageByte));
            assertThat(image)
                    .isNotNull();                              // 실제로 이미지로 변환 가능한지
            assertThat(image.getWidth())
                    .isGreaterThan(0);                         // 너비가 있는지
            assertThat(image.getHeight())
                    .isGreaterThan(0);                         // 높이가 있는지
        } catch (IOException e) {
            fail("이미지 변환 실패");
        }
    }
} 