/**
 * TaskManager — seed script
 * Запуск: node seed-data.js
 * Требует: все сервисы запущены (gateway на :8080)
 */

const zlib = require('zlib');

const BASE_URL = 'http://localhost:8080';

// ─── Генератор PNG-изображений (градиент) ────────────────────────────────────

function makeCRCTable() {
    const t = new Uint32Array(256);
    for (let n = 0; n < 256; n++) {
        let c = n;
        for (let k = 0; k < 8; k++) c = (c & 1) ? (0xEDB88320 ^ (c >>> 1)) : (c >>> 1);
        t[n] = c;
    }
    return t;
}
const CRC_TABLE = makeCRCTable();

function crc32(buf) {
    let crc = 0xFFFFFFFF;
    for (let i = 0; i < buf.length; i++) crc = CRC_TABLE[(crc ^ buf[i]) & 0xFF] ^ (crc >>> 8);
    return (crc ^ 0xFFFFFFFF) >>> 0;
}

function pngChunk(type, data) {
    const len = Buffer.alloc(4); len.writeUInt32BE(data.length, 0);
    const t   = Buffer.from(type, 'ascii');
    const crc = Buffer.alloc(4); crc.writeUInt32BE(crc32(Buffer.concat([t, data])), 0);
    return Buffer.concat([len, t, data, crc]);
}

function makePNG(w, h, c1, c2) {
    const rowSize = w * 3 + 1;
    const raw = Buffer.alloc(rowSize * h);
    for (let y = 0; y < h; y++) {
        const t = y / Math.max(h - 1, 1);
        const r = Math.round(c1[0] + (c2[0] - c1[0]) * t);
        const g = Math.round(c1[1] + (c2[1] - c1[1]) * t);
        const b = Math.round(c1[2] + (c2[2] - c1[2]) * t);
        raw[y * rowSize] = 0;
        for (let x = 0; x < w; x++) {
            const o = y * rowSize + 1 + x * 3;
            raw[o] = r; raw[o + 1] = g; raw[o + 2] = b;
        }
    }
    const ihdr = Buffer.alloc(13);
    ihdr.writeUInt32BE(w, 0); ihdr.writeUInt32BE(h, 4);
    ihdr[8] = 8; ihdr[9] = 2;
    return Buffer.concat([
        Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]),
        pngChunk('IHDR', ihdr),
        pngChunk('IDAT', zlib.deflateSync(raw)),
        pngChunk('IEND', Buffer.alloc(0)),
    ]);
}

const sleep = ms => new Promise(r => setTimeout(r, ms));

function decodeJWT(token) {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(Buffer.from(payload, 'base64').toString('utf-8'));
}

// ─── API-обёртки ─────────────────────────────────────────────────────────────

async function register(username, name, lastName, password, email, avatarBuf) {
    const form = new FormData();
    form.append('username', username);
    form.append('name', name);
    form.append('lastName', lastName);
    form.append('password', password);
    form.append('email', email);
    if (avatarBuf)
        form.append('avatars', new Blob([avatarBuf], { type: 'image/png' }), `${username}.png`);

    const res  = await fetch(`${BASE_URL}/api/auth/registration`, { method: 'POST', body: form });
    const text = await res.text();
    if (!res.ok) { console.error(`  ❌ register ${username}: ${res.status} — ${text.slice(0, 120)}`); return null; }
    console.log(`  ✅ ${username} (${name} ${lastName})`);
    return JSON.parse(text);
}

async function login(username, password) {
    const res  = await fetch(`${BASE_URL}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
    });
    const text = await res.text();
    if (!res.ok) { console.error(`  ❌ login ${username}: ${res.status} — ${text.slice(0, 120)}`); return null; }
    console.log(`  ✅ Токен получен`);
    return JSON.parse(text).token;
}

async function createProject(token, userId, name, description, imgBuf) {
    const form = new FormData();
    form.append('name', name);
    form.append('description', description);
    form.append('userId', String(userId));
    form.append('avatars', new Blob([imgBuf], { type: 'image/png' }), 'cover.png');

    const res  = await fetch(`${BASE_URL}/api/projects/createProject`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` },
        body: form,
    });
    const text = await res.text();
    if (!res.ok) { console.error(`  ❌ project "${name}": ${res.status} — ${text.slice(0, 120)}`); return null; }
    const p = JSON.parse(text);
    console.log(`  📁 "${name}"`);
    return p;
}

async function createTask(token, userId, projectId, name, desc, importance, status, dateFinished, assigneeId) {
    const form = new FormData();
    form.append('name', name);
    form.append('description', desc);
    form.append('taskImportance', importance);
    form.append('taskStatus', status);
    form.append('dateFinished', dateFinished);
    form.append('projectId', projectId);
    form.append('userId', String(userId));
    if (assigneeId != null) form.append('assigneeId', String(assigneeId));

    const res  = await fetch(`${BASE_URL}/api/tasks/createTask`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` },
        body: form,
    });
    const text = await res.text();
    if (!res.ok) { console.error(`    ❌ task "${name}": ${res.status} — ${text.slice(0, 120)}`); return null; }
    console.log(`    ✔ [${status.padEnd(11)}] ${name}`);
    return JSON.parse(text);
}

async function createChat(token, title, ownerId) {
    const url  = `${BASE_URL}/api/chat/create-chat?chatTitle=${encodeURIComponent(title)}&ownerId=${ownerId}`;
    const res  = await fetch(url, { method: 'POST', headers: { 'Authorization': `Bearer ${token}` } });
    const text = await res.text();
    if (!res.ok) { console.error(`  ❌ chat "${title}": ${res.status} — ${text.slice(0, 120)}`); return null; }
    console.log(`  💬 "${title}"`);
    return JSON.parse(text);
}

// ─── Данные ──────────────────────────────────────────────────────────────────

const USER_COLORS = [
    [[124, 58, 237], [167, 139, 250]],
    [[37,  99, 235], [96,  165, 250]],
    [[5,  150, 105], [52,  211, 153]],
    [[234, 88,  12], [251, 146,  60]],
];

const PROJECT_COLORS = [
    [[124, 58, 237], [167, 139, 250]],   // purple
    [[37,  99, 235], [96,  165, 250]],   // blue
    [[5,  150, 105], [52,  211, 153]],   // green
    [[217, 70, 239], [232, 121, 249]],   // pink
    [[234, 88,  12], [251, 146,  60]],   // orange
];

const USERS = [
    { username: 'artemis_admin', name: 'Артём',  lastName: 'Захаров', password: 'Artemis2024', email: 'artemis@taskmanager.ru' },
    { username: 'ivan_petrov',   name: 'Иван',   lastName: 'Петров',  password: 'Ivan2024',    email: 'ivan@taskmanager.ru'    },
    { username: 'anna_kozlova',  name: 'Анна',   lastName: 'Козлова', password: 'Anna2024',    email: 'anna@taskmanager.ru'    },
    { username: 'max_sidorov',   name: 'Максим', lastName: 'Сидоров', password: 'Max2024',     email: 'max@taskmanager.ru'     },
];

const PROJECTS = [
    {
        name: 'TaskManager — Дипломный проект',
        desc: 'Разработка микросервисной платформы управления проектами и задачами с поддержкой чата в реальном времени, ИИ-ассистента и аналитики',
    },
    {
        name: 'Мобильное приложение TravelPlanner',
        desc: 'Создание кросс-платформенного приложения для планирования путешествий с поддержкой офлайн-режима, геолокации и персональных рекомендаций',
    },
    {
        name: 'Редизайн корпоративного портала',
        desc: 'Полный редизайн веб-сайта компании: новый визуальный стиль, улучшенный UX и адаптивная вёрстка для всех устройств',
    },
    {
        name: 'Интеграция платёжной системы',
        desc: 'Интеграция с платёжными шлюзами, реализация webhook-обработчиков, системы уведомлений и мониторинга транзакций в реальном времени',
    },
    {
        name: 'Дашборд аналитики данных',
        desc: 'Разработка интерактивного дашборда с визуализацией KPI, интеграцией с BI-инструментами и автоматической генерацией отчётов',
    },
];

// [name, desc, importance, status, dateFinished]
const TASKS = [
    [   // TaskManager
        ['Проектирование архитектуры',         'Определение границ сервисов, выбор паттернов взаимодействия и проектирование API-контрактов',                        'HIGH',         'COMPLETED',   '2025-03-15'],
        ['Разработка AuthService',              'Реализация сервиса аутентификации с JWT, OAuth2 и управлением ролями пользователей',                                'HIGH',         'COMPLETED',   '2025-03-30'],
        ['Разработка ProjectService',           'Реализация REST API для управления проектами с загрузкой обложек и пагинацией',                                     'HIGH',         'COMPLETED',   '2025-04-10'],
        ['Разработка TaskService',              'Реализация REST API для задач: создание, обновление, фильтрация по статусу и важности',                             'HIGH',         'COMPLETED',   '2025-04-15'],
        ['Разработка ChatService',              'Реализация сервиса чата с WebSocket, хранением сообщений в MongoDB и поддержкой групп',                             'HIGH',         'COMPLETED',   '2025-04-20'],
        ['Интеграция ИИ-ассистента',            'Подключение языковой модели через Groq API, разработка промптов и контекстного диалога',                            'INTERMEDIATE', 'IN_PROGRESS', '2025-05-20'],
        ['Покрытие интеграционными тестами',    'Написание тестов с @SpringBootTest + TestRestTemplate, изолированное окружение H2/Flapdoodle',                      'INTERMEDIATE', 'IN_PROGRESS', '2025-05-25'],
        ['Документация и финальный деплой',     'Оформление Swagger, README и развёртывание через Docker Compose',                                                   'LOW',          'PLANING',     '2025-06-01'],
    ],
    [   // TravelPlanner
        ['UX-исследование и анализ конкурентов','Изучение существующих приложений, опрос аудитории, формирование user stories',                                      'HIGH',         'COMPLETED',   '2025-02-20'],
        ['Разработка дизайн-системы',           'Создание компонентной библиотеки, палитры и типографики в Figma',                                                   'HIGH',         'COMPLETED',   '2025-03-10'],
        ['Интеграция с картографическим API',   'Реализация поиска мест, построения маршрутов и отображения точек на карте',                                         'HIGH',         'IN_PROGRESS', '2025-05-15'],
        ['Офлайн-режим и синхронизация',        'Локальное хранилище, фоновая синхронизация и работа без интернет-соединения',                                       'INTERMEDIATE', 'PLANING',     '2025-06-10'],
        ['Push-уведомления',                    'Интеграция с FCM, настройка триггеров и персонализация уведомлений',                                                'LOW',          'CANCELED',    '2025-04-30'],
        ['Нагрузочное тестирование API',        'Проверка производительности серверной части под нагрузкой и выявление узких мест',                                  'INTERMEDIATE', 'EXPIRED',     '2025-04-10'],
    ],
    [   // Corporate Portal
        ['Аудит текущего интерфейса',           'Анализ UX, выявление проблемных зон, сбор обратной связи от пользователей',                                         'HIGH',         'COMPLETED',   '2025-01-31'],
        ['Прототип в Figma',                    'Разработка интерактивного прототипа всех ключевых экранов и сценариев',                                              'HIGH',         'COMPLETED',   '2025-02-28'],
        ['Вёрстка главной страницы',            'Адаптивная вёрстка главной страницы по утверждённому макету',                                                        'INTERMEDIATE', 'COMPLETED',   '2025-03-20'],
        ['Вёрстка внутренних разделов',         'Страницы каталога, профиля пользователя и административной панели',                                                  'INTERMEDIATE', 'IN_PROGRESS', '2025-05-10'],
        ['Оптимизация производительности',      'Lazy loading, оптимизация изображений, кэширование и улучшение Core Web Vitals',                                     'LOW',          'PLANING',     '2025-06-05'],
        ['SEO-оптимизация',                     'Настройка мета-тегов, sitemap, structured data и технического SEO',                                                  'LOW',          'EXPIRED',     '2025-04-15'],
    ],
    [   // Payment
        ['Техническое задание',                 'Документирование требований, изучение API шлюзов и согласование с командой',                                         'HIGH',         'COMPLETED',   '2025-02-10'],
        ['Интеграция со Stripe',                'Реализация платёжных сессий, обработка webhook и управление подписками',                                             'HIGH',         'COMPLETED',   '2025-03-25'],
        ['Мониторинг транзакций',               'Дашборд транзакций, алерты при сбоях и автоматические ежедневные отчёты',                                           'INTERMEDIATE', 'IN_PROGRESS', '2025-05-20'],
        ['Нагрузочное тестирование шлюза',      'Проверка производительности и устойчивости к сбоям платёжного шлюза',                                               'INTERMEDIATE', 'PLANING',     '2025-06-15'],
        ['Интеграция с YooMoney',               'Альтернативный платёжный шлюз для российского рынка',                                                               'HIGH',         'CANCELED',    '2025-03-01'],
    ],
    [   // Analytics
        ['Сбор требований',                     'Определение ключевых метрик, источников данных и ожидаемых визуализаций с заказчиком',                              'HIGH',         'COMPLETED',   '2025-02-15'],
        ['Схема хранилища данных',              'Разработка схемы агрегаций, определение индексирования и стратегии хранения',                                       'HIGH',         'COMPLETED',   '2025-03-05'],
        ['API агрегации метрик',                'Эндпоинты для получения агрегированных данных с фильтрацией и группировкой',                                        'INTERMEDIATE', 'COMPLETED',   '2025-04-01'],
        ['Визуализация: графики и диаграммы',   'Интерактивные линейные, столбчатые и круговые диаграммы на Chart.js',                                               'INTERMEDIATE', 'IN_PROGRESS', '2025-06-01'],
        ['Экспорт отчётов в PDF',               'Генерация PDF-отчётов с графиками и таблицами по выбранному периоду',                                               'LOW',          'PLANING',     '2025-06-20'],
        ['Подключение внешних источников',      'Импорт данных из внешних API для расширения аналитики',                                                             'LOW',          'EXPIRED',     '2025-04-01'],
    ],
];

const CHATS = ['Команда разработки', 'Дизайн и UX', 'QA и тестирование'];

// ─── Main ─────────────────────────────────────────────────────────────────────

async function main() {
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
    console.log('   TaskManager — заполнение тестовыми данными   ');
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n');

    // 1. Регистрация
    console.log('👤 Регистрация пользователей...');
    const registeredIds = [];
    for (let i = 0; i < USERS.length; i++) {
        const u      = USERS[i];
        const avatar = makePNG(200, 200, USER_COLORS[i][0], USER_COLORS[i][1]);
        const result = await register(u.username, u.name, u.lastName, u.password, u.email, avatar);
        if (result?.id) registeredIds.push(result.id);
        await sleep(300);
    }

    // 2. Вход (username-поле принимает email, см. loadUserByUsername → findByEmail)
    console.log('\n🔑 Вход в систему...');
    const token = await login(USERS[0].email, USERS[0].password);
    if (!token) {
        console.error('\n❌ Не удалось получить JWT-токен.');
        process.exit(1);
    }

    const mainUserId = decodeJWT(token).id;
    console.log(`  ✅ userId из JWT: ${mainUserId}`);

    // 3. Проекты
    console.log('\n📁 Создание проектов...');
    const projects = [];
    for (let i = 0; i < PROJECTS.length; i++) {
        const p   = PROJECTS[i];
        const img = makePNG(800, 400, PROJECT_COLORS[i][0], PROJECT_COLORS[i][1]);
        const created = await createProject(token, mainUserId, p.name, p.desc, img);
        if (created) projects.push(created);
        await sleep(400);
    }

    // 4. Задачи
    console.log('\n✅ Создание задач...');
    let taskTotal = 0;
    for (let pi = 0; pi < projects.length; pi++) {
        const project = projects[pi];
        console.log(`\n  📁 ${project.name}`);
        const taskList = TASKS[pi] ?? [];
        for (const [name, desc, importance, status, date] of taskList) {
            const t = await createTask(token, mainUserId, project.id, name, desc, importance, status, date, mainUserId);
            if (t) taskTotal++;
            await sleep(250);
        }
    }

    // 5. Чаты
    console.log('\n💬 Создание чатов...');
    for (const title of CHATS) {
        await createChat(token, title, mainUserId);
        await sleep(300);
    }

    // Итог
    console.log('\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
    console.log(`✨ Готово!`);
    console.log(`   Пользователи:  ${registeredIds.length} из ${USERS.length}`);
    console.log(`   Проекты:       ${projects.length}`);
    console.log(`   Задачи:        ${taskTotal}`);
    console.log(`   Чаты:          ${CHATS.length}`);
    console.log(`\n   Логин для скриншотов:`);
    console.log(`   Имя пользователя : artemis_admin`);
    console.log(`   Пароль           : Artemis2024`);
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
}

main().catch(console.error);
