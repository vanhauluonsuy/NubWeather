# 🌤️ NubWeather

<div align="center">

![Minecraft](https://img.shields.io/badge/Minecraft-1.20+-green?style=for-the-badge&logo=minecraft)
![Paper](https://img.shields.io/badge/Paper-1.20+-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge)
![Version](https://img.shields.io/badge/Version-1.0.0-red?style=for-the-badge)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

**Đồng bộ thời gian và thời tiết Minecraft theo thời gian thực ngoài đời**

[📥 Tải xuống](#-tải-xuống) • [📖 Hướng dẫn](#-cài-đặt) • [⚙️ Cấu hình](#%EF%B8%8F-cấu-hình) • [💬 Hỗ trợ](#-hỗ-trợ)

</div>

---

## 📋 Giới thiệu

**NubWeather** là plugin Minecraft dành cho máy chủ **Paper/Purpur**, giúp đồng bộ thời gian và thời tiết giữa tất cả các thế giới (world) theo **thời gian thực ngoài đời**. Mang đến trải nghiệm sống động và chân thực nhất cho người chơi!

### 🎯 Tại sao nên chọn NubWeather?

- ⏰ **Thời gian thực chính xác** - Mặt trời mọc/lặn theo đúng múi giờ bạn chọn
- 🌍 **Đồng bộ toàn server** - Tất cả các world đều có cùng thời gian và thời tiết
- 🎨 **Nhẹ nhàng & tối ưu** - Không ảnh hưởng đến hiệu suất server
- 🔧 **Dễ cấu hình** - Chỉnh sửa đơn giản qua file config
- 🛡️ **Tương thích WorldGuard** - Tự động vô hiệu hóa khóa thời gian/thời tiết
- 🧩 **Hỗ trợ PlaceholderAPI** - Tích hợp dễ dàng với các plugin khác

---

## ✨ Tính năng chi tiết

### 🌅 Đồng bộ thời gian thực
Thời gian trong game chạy theo múi giờ bạn cài đặt:
- `06:00` 🌄 → Bình minh (tick 0)
- `12:00` ☀️ → Trưa, mặt trời đứng đỉnh (tick 6000)
- `18:00` 🌇 → Hoàng hôn (tick 12000)
- `00:00` 🌙 → Nửa đêm (tick 18000)

### 🌦️ Đồng bộ thời tiết
- Tất cả các world đều có cùng trạng thái thời tiết (nắng/mưa/bão)
- Chọn world chủ để làm chuẩn thời tiết
- Tự động cập nhật theo chu kỳ

### 🔄 Tự động cập nhật
- Cập nhật liên tục mỗi giây (hoặc tùy chỉnh)
- Luôn đảm bảo thời gian chính xác tuyệt đối

### 🛡️ Tương thích WorldGuard
- Tự động gỡ bỏ khóa thời gian (`time-lock`) và thời tiết (`weather-lock`) trên `__global__`
- Có thể bật/tắt tùy chọn

### 🧩 PlaceholderAPI
- Hỗ trợ nhiều placeholder để hiển thị thời gian và thông tin
- Tùy chỉnh tên các buổi trong config
- Tích hợp dễ dàng với các plugin khác

## 🧩 PlaceholderAPI

Nếu bạn cài [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/), bạn có thể sử dụng các placeholder sau:

| Placeholder | Hiển thị | Mô tả | Ví dụ |
|-------------|----------|-------|-------|
| `%nubweather_time%` | `5h:07p` | Giờ và phút hiện tại (rút gọn) | `5h:07p` |
| `%nubweather_time_full%` | `05:07:53` | Giờ:phút:giây hiện tại (đầy đủ) | `05:07:53` |
| `%nubweather_tick%` | `6000` | Số tick trong game hiện tại | `6000` |
| `%nubweather_period%` | `Sáng` | Tên buổi trong ngày (tùy chỉnh trong config) | `Sáng` |

### 📝 Ví dụ sử dụng

**Trong config của plugin khác:**
```yaml
# Hiển thị thời gian thực
message: "⏰ Thời gian hiện tại: %nubweather_time%"
# Output: "⏰ Thời gian hiện tại: 5h:07p"

# Hiển thị thời gian đầy đủ
message: "🕐 %nubweather_time_full% - Chào buổi %nubweather_period%!"
# Output: "🕐 05:07:53 - Chào buổi Sáng!"

# Hiển thị tick game
message: "🎮 Game tick: %nubweather_tick%"
# Output: "🎮 Game tick: 6000"
```
---

## 📥 Tải xuống

| Phiên bản | Minecraft | Tải về |
|-----------|-----------|--------|
| **v1.0.0** | 1.20.x - 1.21.x | [Download .jar](https://github.com/your-username/NubWeather/releases/tag/v1.0.0) |
| **Latest Build** | 1.20.x - 1.21.x | [Download .jar](https://github.com/your-username/NubWeather/releases/latest) |

> **Yêu cầu**: Java 17+, Paper/Purpur 1.20+

---

## 📖 Cài đặt

1. **Tải plugin** từ [Releases](https://github.com/your-username/NubWeather/releases)
2. Đặt file `NubWeather.jar` vào thư mục `plugins/` trên máy chủ
3. Khởi động lại máy chủ (hoặc dùng lệnh `/reload confirm` nếu dùng Paper)
4. Plugin sẽ tự tạo file `config.yml` trong thư mục `plugins/NubWeather/`
5. Chỉnh sửa cấu hình theo nhu cầu
6. Dùng lệnh `/nubweather reload` để áp dụng thay đổi

---

## ⚙️ Cấu hình

File `config.yml` được tạo tự động sau lần chạy đầu tiên:

```yaml
# 🌐 Múi giờ để đồng bộ thời gian thực
# Danh sách timezone: https://en.wikipedia.org/wiki/List_of_tz_database_time_zones
timezone: "Asia/Ho_Chi_Minh"

# ⏰ Đồng bộ thời gian tất cả world theo đồng hồ thực 24h
sync-time: true

# 🔄 Khoảng cách đồng bộ (tick). 20 = 1 giây.
# Giảm xuống 1 nếu muốn mượt nhất (nhưng tốn CPU hơn chút)
sync-interval-ticks: 20

# 🌦️ Đồng bộ thời tiết giữa tất cả các world
sync-weather: true

# 🌍 World đại diện cho thời tiết. Các world khác sẽ copy theo world này
weather-master: world_blue

# 🛡️ Cố gắng xóa WorldGuard weather-lock / time-lock trên __global__
bypass-worldguard: true

# 📝 Tên các buổi trong ngày (placeholder %nubweather_period%)
periods:
  morning: "Sáng"
  noon: "Trưa"
  afternoon: "Chiều"
  night: "Tối"

# 💬 Tin nhắn trong game
messages:
  prefix: "&6[NubWeather] &r"
  reload: "&aConfig reloaded."
  sync: "&aĐã đồng bộ ngay lập tức."
  no-perm: "&cBạn không có quyền."
