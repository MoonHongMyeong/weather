import { GRID_COORDINATES } from './coordinates.js';

const DAEJEON = "DAEJEON";

const ImageType = {
    SATELLITE: "satellite",
    RADAR: "radar",
    LIGHTNING: "lightning"
};

const days = ['일', '월', '화', '수', '목', '금', '토'];

const BACKGROUND_COLOR ={
    'light': '#87CEEB',
    'dark': '#001F3F'
}

let mapMarkers = [];

// API 호출 함수들
const fetchIndexData = async () => {
    try {
        const response = await fetch(`api/v1/weather/index/${DAEJEON}`);
        if (!response.ok) throw new Error('환경지수 데이터를 가져오는데 실패했습니다.');
        return await response.json();
    } catch (error) {
        console.error('Error fetching index data:', error);
        return null;
    }
};

const fetchShortTermForecast = async () => {
    try {
        const response = await fetch(`api/v1/weather/forecast/short/${DAEJEON}`);
        if (!response.ok) throw new Error('날씨 데이터를 가져오는데 실패했습니다.');
        return await response.json();
    } catch (error) {
        console.error('Error fetching weather data:', error);
        return null;
    }
};

const fetchPopupShortTermForecast = async (nx, ny) => {
    try {
        const response = await fetch(`api/v1/weather/forecast/popup?nx=${nx}&ny=${ny}`);
        if (!response.ok) throw new Error('단기예보 데이터를 가져오는데 실패했습니다.');
        return await response.json();
    } catch (error) {
        console.error('Error fetching popup short term forecast:', error);
        return null;
    }
};

const fetchMidTermForecast = async () => {
    try {
        const response = await fetch(`api/v1/weather/forecast/mid/${DAEJEON}`);
        if (!response.ok) throw new Error('중기 예보 데이터를 가져오는데 실패했습니다.');
        return await response.json();
    } catch (error) {
        console.error('Error fetching mid term forecast:', error);
        return null;
    }
};

const fetchWeatherImages = async (type) => {
    try {
        const response = await fetch(`api/v1/weather/images/${type}`);
        if (!response.ok) throw new Error('날씨 이미지를 가져오는데 실패했습니다.');
        return await response.blob();
    } catch (error) {
        console.error('Error fetching weather image:', error);
        return null;
    }
};

const fetchWarnings = async () => {
    try {
        const response = await fetch(`api/v1/weather/warnings/${DAEJEON}`);
        if (!response.ok) throw new Error('경보 데이터를 가져오는데 실패했습니다.');
        return await response.json();
    } catch (error) {
        console.error('Error fetching warnings:', error);
        return null;
    }
};

const renderIndexData = (data) => {
    const container = document.getElementById('index-forecast-container');
    if (!data) return;
    
};

const renderShortTermForecast = (data) => {
    const container = document.getElementById('today-forecast-container');
    if (!data) return;

    let html = `
        <div class="weather-container card">
            <h1 class="weather-title">시간대별 날씨</h1>`;
    
    const groupedData = data.reduce((acc, item) => {
        if (!acc[item.FCST_DATETIME]) {
            acc[item.FCST_DATETIME] = {};
        }
        acc[item.FCST_DATETIME][item.CATEGORY] = item.FCST_VALUE;
        return acc;
    }, {});

    const temperatures = Object.values(groupedData)
        .map(items => Number(items['TMP']))
        .filter(temp => !isNaN(temp));
    
    const maxTemp = Math.max(...temperatures);
    const minTemp = Math.min(...temperatures);

    html += `
        <div class="hourly-forecast">`;

    Object.entries(groupedData).forEach(([datetime, items]) => {
        const hour = datetime.substring(8, 10);
        const temp = Number(items['TMP']);
        const marginBottom = ((temp - minTemp) / (maxTemp - minTemp)) * 100; // margin 계산

        html += `
            <div class="forecast-item">
                <span class="time">${new Date().getHours()+'' === hour ? '지금' : hour+'시'}</span>
                <span>${getWeatherDescription(items['SKY'])}</span>
                <div class="temp-bar">
                    <span class="temp" style="margin-bottom: ${marginBottom * 2}%;">${items['TMP']}℃</span>
                </div>`;

        if (items['POP']) {
            html += `<span class="pop">${items['POP']}%</span>`;
        }
        if (items['PCP']) {
            html += `<span class="precipitation">${items['PCP']}</span>`;
        }
        
        html += `</div>`;
    });

    html += `   </div>`; // hourly-forecast
    html += `</div>`; // weather-container
    
    container.innerHTML = html;
};
const getWeatherDescription = (sky) => {
    switch(sky) {
        case '1': return '맑음';
        case '3': return '구름많음';
        case '4': return '흐림';
        default: return '맑음';
    }
};

const renderMidTermForecast = (data) => {
    const container = document.getElementById('week-forecast-container');
    if (!data) return;

    const maxTemp = Math.max(...data.map(item => item.MAX_TEMP));
    const minTemp = Math.min(...data.map(item => item.MIN_TEMP));
    const tempRange = maxTemp - minTemp;
    
    let html = ``;
    html += `<div class="weather-container card">`;
    html += `   <h1 class="weather-title">이번주 날씨</h1>`;
    html += `   <div class="weekly-forecast">`;
    data.forEach(item => {
        const maxTempPosition = ((item.MAX_TEMP - minTemp) / tempRange) * 80 + 10;
        const minTempPosition = ((item.MIN_TEMP - minTemp) / tempRange) * 80 + 10;

        html += `       <div class="forecast-item">`;
        html += `           <span class="${convertToDays(item.TM_EF) === '토' ? 'blue' : convertToDays(item.TM_EF) === '일' ? 'red' : ''}">${convertToDays(item.TM_EF)} </span>`;
        html += `           <span>${item.TM_EF.slice(8, 10) === '00' ? '오전' : '오후'}</span>`;
        html += `           <span>${getWeatherDescription(item.SKY)}</span>`;
        html += `           <div class="min-max-temp-bar">
                                <span class="temp" style="bottom: ${maxTempPosition}%;">
                                    ${item.MAX_TEMP}℃
                                    <div class="temp-tolerance">
                                        <span class="max-high red">${item.MAX_H > 0 ? '+' + item.MAX_H : item.MAX_H}</span>
                                        <span class="max-low blue">${item.MAX_L > 0 ? '-' + item.MAX_L : item.MAX_L}</span>
                                    </div>
                                </span>
                                <span class="temp" style="bottom: ${minTempPosition}%;">
                                    ${item.MIN_TEMP}℃
                                    <div class="temp-tolerance">
                                        <span class="min-high red">${item.MIN_H > 0 ? '+' + item.MIN_H : item.MIN_H}</span>
                                        <span class="min-low blue">${item.MIN_L > 0 ? '-' + item.MIN_L : item.MIN_L}</span>
                                    </div>
                                </span>
                            </div>`;
        html += `           <span>${item.RN_ST}%</span>`;
        html += `           <span>${item.PRE}</span>`;
        html += `       </div>`;
    });
    html += `   </div>`;
    html += `</div>`;

    container.innerHTML = html;
};

const renderWeatherImages = (imageBlob, type) => {
    const container = document.getElementById('weather-images-container');
    if (!imageBlob) return;

    const existsCard = document.getElementById(type);
    if (existsCard) {
        existsCard.remove();
    }

    const card = document.createElement('div');
    card.className = 'forecast-card';
    card.id = type;
    card.style.backgroundColor = '#eee';
    
    const imageUrl = URL.createObjectURL(imageBlob);
    card.innerHTML = `
        <div class="forecast-card-content">
            <img src="${imageUrl}" alt="${type} 이미지" class="weather-image">
        </div>
    `;
    
    container.appendChild(card);
};

const renderWarnings = (data) => {
    const warningElement = document.createElement('div');
    warningElement.className = 'warning-message';

    if (data && Object.keys(data).length > 0) {
        warningElement.textContent = '경고: ' + data.message;
        document.body.appendChild(warningElement);

        setTimeout(() => {
            warningElement.classList.add('show');
        }, 100);

        setTimeout(() => {
            warningElement.classList.remove('show');
            setTimeout(() => {
                document.body.removeChild(warningElement);
            }, 300);
        }, 10000);
    }
};

const getCurrentDateTime = () => {
    return `${new Date().toLocaleString('ko-KR', 
        { timeZone: 'Asia/Seoul', year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', hour12: false })
        .replace(/[. :]/g, '')
        .slice(0, 10)}0000`;
};

const convertToDays = (date) => {
    if (!/^\w{12}$/.test(date)) new Error("잘못된 date format");
    
    const year = parseInt(date.slice(0, 4));
    const month = parseInt(date.slice(4, 6)) - 1;
    const day = parseInt(date.slice(6, 8));
    const hour = parseInt(date.slice(8, 10));
    const minute = parseInt(date.slice(10, 12));

    return days[new Date(year, month, day, hour, minute).getDay()];
}

const renderPopupShortTermForecast = (dong, data) => {
    return `
        <div class="popup-content">
            <h2>${dong}</h2>
            <p>기온: ${data.find(item => item.CATEGORY === 'TMP')?.FCST_VALUE || 'N/A'}℃</p>
            <p>습도: ${data.find(item => item.CATEGORY === 'REH')?.FCST_VALUE || 'N/A'}%</p>
            <p>강수확률: ${data.find(item => item.CATEGORY === 'POP')?.FCST_VALUE || 'N/A'}%</p>
        </div>
    `;
};

const setBackground = () => {
    const backgroundColor = new Date().getHours() > 7 && new Date().getHours() < 18 ? BACKGROUND_COLOR.light : BACKGROUND_COLOR.dark;
    document.body.style.backgroundColor = backgroundColor;
};

const createMarker = (coordinate, map) => {
    const marker = L.marker([coordinate.latitude, coordinate.longitude])
        .addTo(map)
        .bindPopup(coordinate.dong);

    marker.on('click', async() => {
        const data = await fetchPopupShortTermForecast(coordinate.nx, coordinate.ny);
        marker.setPopupContent(renderPopupShortTermForecast(coordinate.dong, data));
        marker.openPopup();
    });

    mapMarkers.push(marker);
    return marker;
};

window.onload = async () => {
    setBackground();

    const [indexData, shortTermForecast, midTermForecast] = await Promise.all([
        fetchIndexData(),
        fetchShortTermForecast(),
        fetchMidTermForecast()
    ]);

    renderIndexData(indexData);
    renderShortTermForecast(shortTermForecast);
    renderMidTermForecast(midTermForecast);

    try {
        const map = L.map('map', {
            center: [36.3471194444444, 127.3865667],
            zoom: 14
        });
        
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        Object.values(GRID_COORDINATES.DAEJEON).forEach(district => {
            district.forEach(coordinate => {
                createMarker(coordinate, map);
            });
        });

        setTimeout(() => {
            map.invalidateSize();
        }, 100);
    } catch (error) {
        console.error('Map initialization error:', error);
    }

    const types = ['SATELLITE', 'RADAR', 'LIGHTNING'];
    Promise.all(types.map(async type => {
        const imageBlob = await fetchWeatherImages(type);
        renderWeatherImages(imageBlob, type);
    }));

    window.warningInterval = setInterval(async () => {
        const warnings = await fetchWarnings();
        renderWarnings(warnings);
    }, 60000);
};

window.addEventListener('beforeunload', () => {
    if (window.warningInterval) {
        clearInterval(window.warningInterval);
    }

    mapMarkers.forEach(marker => marker.remove());
    mapMarkers = [];
});

