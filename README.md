# StepScape

StepScape, kullanıcıların günlük adım verilerini takip edebileceği modern bir Android uygulamasıdır. Health Connect entegrasyonu ile adım verilerini otomatik olarak toplar, yerel veritabanında saklar ve Firebase'e senkronize eder.

## 🎯 Özellikler

- **Google Sign-In**: Google hesabı ile kolay giriş
- **Health Connect Entegrasyonu**: Otomatik adım verisi toplama
- **Görsel İlerleme**: Dairesel progress bar ile günlük hedef takibi
- **Grafik Gösterimi**: Günlük, haftalık, aylık adım verilerini görselleştirme
- **Yerel Depolama**: Room database ile offline çalışma
- **Cloud Senkronizasyon**: Firebase Realtime Database ile veri senkronizasyonu
- **Geçmiş Kayıtlar**: Tüm adım kayıtlarını görüntüleme ve filtreleme

## 🛠️ Teknolojiler

- **Kotlin**: Modern Android geliştirme
- **MVVM + Repository Pattern**: Temiz mimari
- **Dagger Hilt**: Dependency Injection
- **Room Database**: Yerel veri depolama
- **Firebase**: Authentication ve Realtime Database
- **Health Connect API**: Sağlık verilerine erişim
- **MPAndroidChart**: Grafik görselleştirme
- **Navigation Component**: Fragment navigasyonu
- **ViewBinding**: Type-safe view erişimi
- **Coroutines & Flow**: Asenkron işlemler

## 📱 Ekranlar

### 1. Login Screen
- Google Sign-In butonu
- Modern gradient arka plan
- Kullanıcı dostu arayüz

### 2. Home Screen (Main)
- Dairesel progress bar ile günlük adım gösterimi
- Motivasyon mesajları
- Tab bazlı grafik görünümü (Day, Week, Month, 6 Month, Year)
- Adım istatistikleri ve tarih aralığı

### 3. Logs Screen
- Tüm adım kayıtlarının listesi
- Tarih, adım sayısı ve senkronizasyon durumu
- RecyclerView ile performanslı listeleme

## 🏗️ Mimari

Proje **MVVM (Model-View-ViewModel)** mimarisi ve **Repository Pattern** kullanılarak geliştirilmiştir:

```
ui/
  ├── login/
  │   ├── LoginFragment
  │   └── LoginViewModel
  ├── home/
  │   ├── HomeFragment
  │   └── HomeViewModel
  └── logs/
      ├── LogsFragment
      └── LogsViewModel

data/
  ├── local/
  │   ├── entity/StepLog
  │   ├── dao/StepLogDao
  │   └── StepScapeDatabase
  ├── health/
  │   └── HealthConnectManager
  └── firebase/
      └── FirebaseSyncService

repository/
  ├── AuthenticationRepository
  ├── StepLogRepository
  └── UserRepository

di/
  ├── AuthenticationModule
  ├── DatabaseModule
  └── UserModule
```

## 📦 Kurulum

1. **Repository'yi klonlayın:**
   ```bash
   git clone <repository-url>
   cd StepScape
   ```

2. **Firebase Yapılandırması:**
   - Firebase Console'da yeni bir proje oluşturun
   - Android uygulaması ekleyin
   - `google-services.json` dosyasını `app/` klasörüne ekleyin
   - Realtime Database'i etkinleştirin
   - Authentication'da Google Sign-In'i etkinleştirin
   - SHA-1 fingerprint'i Firebase Console'a ekleyin

3. **Health Connect:**
   - Health Connect uygulamasının cihazda yüklü olduğundan emin olun
   - Uygulama ilk açılışta Health Connect izinlerini isteyecektir

4. **Gradle Sync:**
   - Android Studio'da projeyi açın
   - Gradle sync işlemini tamamlayın

5. **Çalıştırma:**
   - Uygulamayı bir Android cihaz veya emülatörde çalıştırın

## 🔧 Yapılandırma

### Firebase Realtime Database Rules

```json
{
  "rules": {
    "StepLogs": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "Users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    }
  }
}
```

### SHA-1 Fingerprint

SHA-1 fingerprint'i almak için:
```bash
cd android
./gradlew signingReport
```

## 📊 Veri Yapısı

### StepLog Entity
```kotlin
@Entity(tableName = "step_logs")
data class StepLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val date: Long, // Timestamp
    val steps: Int,
    val syncedToFirebase: Boolean = false
)
```

### Firebase Structure
```
StepLogs/
  {userId}/
    {logId}/
      date: Long
      steps: Int
      syncedToFirebase: Boolean
      timestamp: Long
```

## 🔄 Senkronizasyon

- Uygulama açıldığında (Main ekranına girildiğinde) otomatik senkronizasyon başlar
- `syncedToFirebase == false` olan tüm kayıtlar Firebase'e gönderilir
- Başarılı senkronizasyon sonrası yerel veritabanında `syncedToFirebase = true` olarak güncellenir

## 📝 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.

## 👨‍💻 Geliştirici

Eray Erarslan

---

**Not**: Bu uygulama Health Connect API'sini kullanır. Health Connect'in cihazda yüklü ve güncel olduğundan emin olun.

