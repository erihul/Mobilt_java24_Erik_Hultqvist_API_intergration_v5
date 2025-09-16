# Mobilt_java24_Erik_Hultqvist_API_intergration_v5


Jag har valt att göra en väderapp, där jag använder två API:er:

- **OpenWeatherMap** – för att få väderdata
- **OpenCageData** – för att hämta platsinformation

##  Appstruktur <!-- 📱 -->

- `MainActivity` med `FragmentContainerView`
- Fyra Fragments:

## 1. LOGIN

<table>
    <tr>
    <td>
    <img src="images/login_fragment.png" width="50%"/>
    </td>
    <td width="50%">
        För inloggning används <strong>Firebase Authentication</strong>.
    </td>
    </tr>
    </table>

## 2. Main (Current Weather)

<table>
    <tr>
    <td>
    <img src="images/current_weather_fragment.png" width="50%"/>
    </td>
    <td width="50%">
        Här hämtar jag platsen från sharedPreferences och kollar den mot Openweathermap. Där tar jag ut temperatur, beskrivning och ikon.
    </td>
    </tr>
    </table>

## 3. 5-Day Forecast

<table>
    <tr>
    <td>
    <img src="images/forecast_fragment.png" width="50%"/>
    </td>
    <td width="50%">
        I detta fragment får användaren se 5-dagarsprognosen för den “valda” platsen. Det är ett annat APIanrop än från “main” fragmentet..
    </td>
    </tr>
    </table>

## 4. Location (Settings)

<table>
    <tr>
    <td>
    <img src="images/location_fragment.png" width="50%"/>
    </td>
    <td width="50%">
        I detta “settings”-liknande fragment så displayas den plats som för närvarande ligger i sharedpreferences Sen går det att hämta sin nuvarande position, via LocationCallback, vid lyckat resultat dirigeras användaren, precis som vid search for location,  tillbaka till “main”, BlankFragment2. Dessutom så sparas de i en Firestore Databas. Nederst på fragmentet finns de tidigare hämtade eller sökta positionerna. De hämtas från databasen (Firestore). De går att klicka på om användaren vill se vädret (dirigeras till “main”) eller ta bort och de raderas då från databasen.
    </td>
    </tr>
    </table>

##  API:er <!-- 🧩 -->

### OpenWeatherMap
- Hämtar aktuell väderdata och prognoser.

### OpenCageData
- Konverterar GPS-koordinater till platsnamn och vice versa.

##  Notifikationer <!-- 🔔 -->

<table>
    <tr>
    <td>
    <img src="images/notification.png" width="50%"/>
    </td>
    <td width="50%">
        Om vädret visar regn skickas en notifikation.
        Om temperaturen är över 25°C, skickas en varning om hetta.
    </td>
    </tr>
    </table>

##  Databas <!-- 🗃️ -->

- Platser som lokaliseras via "GPS" och de platser som lokaliseras via sökrutan sparas i  **firestore database**.
