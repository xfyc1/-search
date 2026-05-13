# AndeSearch

Android 端 Everything-like 文件搜索工具，通过预建索引实现毫秒级文件名搜索，支持拼音搜索中文文件。

## 特性

- **毫秒级搜索** — 预建全盘文件索引，Trie 前缀匹配 < 5ms，FTS5 全文检索 < 30ms
- **拼音搜索** — 输入拼音全拼（`baogao`）或首字母（`bg`）即可找到中文文件（`报告.docx`）
- **包含兜底** — Trie + FTS 前缀匹配 + LIKE 包含查询三层索引，输入关键词的任意部分都能命中
- **文件夹浏览** — 搜索结果中点击文件夹即可进入浏览其内容，支持逐层深入
- **文件打开** — 搜索结果直接点击打开，自动识别 MIME 类型调用系统应用
- **实时监听** — FileObserver + 轮询双通道，文件增删改 30 秒内同步到索引
- **Material 3** — 完整的亮色/暗色主题，现代化设计
- **前台服务** — 索引构建异步执行，通知栏展示进度

## 技术栈

| 层 | 技术 |
|---|------|
| 语言 | Kotlin |
| UI | Jetpack Compose + Material 3 |
| 数据库 | Room + SQLite FTS4 |
| DI | Hilt |
| 异步 | Coroutines + Flow |
| 构建 | Gradle Kotlin DSL + Version Catalog |
| 最低 SDK | 26 (Android 8.0) |
| 目标 SDK | 34 (Android 14) |

## 架构

```
Clean Architecture: data → domain → ui
app/src/main/java/com/andesearch/
├── data/
│   ├── local/          # Room Entity, DAO, Database, FTS
│   ├── scanner/        # 文件扫描器 (java.io.File / Shizuku)
│   ├── watcher/        # 文件变化监听 (FileObserver + Polling)
│   └── repository/     # 数据仓库
├── di/                 # Hilt 依赖注入
├── domain/
│   ├── index/          # 索引引擎, 拼音映射, 前缀树
│   ├── model/          # 领域模型
│   └── usecase/        # 搜索/重建用例
├── ui/
│   ├── components/     # 通用组件 (SearchBar, PermissionGate 等)
│   ├── navigation/     # 导航图
│   ├── results/        # 搜索结果列表
│   ├── search/         # 搜索主页
│   ├── settings/       # 设置页
│   └── theme/          # Material 3 主题
└── service/            # 前台服务 + 开机广播
```

## 搜索机制

```
用户输入 → trim + lowercase → 300ms debounce
         ├─ Phase 1: Trie 内存前缀树     → 前缀匹配 (baseline +10)
         ├─ Phase 2: FTS4 MATCH 'q*'     → token 前缀匹配 (baseline +5)
         ├─ Phase 3: LIKE '%q%' 兜底      → 包含匹配 (baseline +3, 前两阶段 < 20 条时触发)
         └─ Phase 4: 融合去重 + 评分排序
```

### 评分规则

| 匹配类型 | 分数 |
|---|---|
| 文件名完全等于查询 | +100 |
| 文件名以查询开头 | +50 |
| 文件名包含查询 | +30 |
| 扩展名精确匹配 | +15 |
| 路径包含查询 | +10 |
| 目录 | +1 |

### 拼音支持

内置 500+ 常用汉字拼音映射，文件名中的中文自动生成索引：

- 全拼索引：`"报告.docx"` → `"baogao"` → 搜索 `"baogao"` 可命中
- 首字母索引：`"报告.docx"` → `"bg"` → 搜索 `"bg"` 可命中

### FTS 索引列

| 列 | 内容 | 说明 |
|---|---|---|
| `name` | 文件名 | tokenizer 分词 |
| `path` | 完整路径 | 支持路径搜索 |
| `pinyin` | 拼音全拼 | 空格分隔 |
| `pinyinInitials` | 首字母 | 连续字母 |

## 构建

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更新
- JDK 17
- Android SDK 34
- Gradle 8.5

### 编译

```bash
# 配置 SDK 路径 (local.properties)
echo "sdk.dir=/path/to/android-sdk" > local.properties

# 编译 Debug APK
./gradlew assembleDebug

# APK 输出位置
# app/build/outputs/apk/debug/app-debug.apk
```

### 安装

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

首次启动需授予「所有文件访问权限」，此权限仅用于扫描文件名和路径，不会读取文件内容。

## 权限说明

| 权限 | 用途 |
|---|---|
| `MANAGE_EXTERNAL_STORAGE` | 扫描设备上所有文件建立索引 |
| `FOREGROUND_SERVICE` | 后台索引服务 |
| `POST_NOTIFICATIONS` | 索引进度通知 |
| `RECEIVE_BOOT_COMPLETED` | 开机自动启动索引服务 |

## License

MIT
