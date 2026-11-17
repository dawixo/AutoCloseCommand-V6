# AutoCloseCommand V6 — Minecraft 26.3 (Fabric)

## Funkcje

- Mod wyłącznie kliencki.
- **F8** włącza/wyłącza oryginalną funkcję AutoCloseCommand.
- Oryginalna funkcja wykrywa osobny ekran kontenera mający dokładnie 90 slotów (54 sloty menu + 36 slotów ekwipunku gracza), zamyka go standardową procedurą, czeka 2 ticki i wysyła `/autoclicker`.
- Komenda `/autoclicker` jest wysyłana tylko raz na jedno otwarcie pasującego okna.
- **F7** niezależnie włącza/wyłącza AutoSell, które cyklicznie wysyła `/sellall`.
- AutoSell jest domyślnie wyłączony. Jego stan jest zapamiętywany po restarcie gry.
- F7 i F8 działają niezależnie: wyłączenie jednej funkcji nie wyłącza drugiej.

## Ustawianie odstępu AutoSell

Po pierwszym uruchomieniu moda utworzy plik:

```text
config/autoclosecommand.properties
```

W nim można zmienić:

```properties
autoSellEnabled=false
autoSellIntervalSeconds=60
```

`autoSellIntervalSeconds` określa odstęp między komendami w sekundach. Dozwolone wartości to 1–86400. Po zmianie ustawień w pliku uruchom ponownie grę. Wartość `autoSellEnabled` jest również zapisywana automatycznie po użyciu F7.

Po włączeniu AutoSell klawiszem F7 pierwsza komenda zostanie wysłana dopiero po upływie pełnego ustawionego odstępu. Komendy są wysyłane tylko wtedy, gdy gracz jest połączony ze światem/serwerem.

## Klawisze

- **F7** — AutoSell `/sellall`.
- **F8** — oryginalny AutoCloseCommand + `/autoclicker`.

Klawisze można zmienić w Ustawienia → Sterowanie → Przypisanie klawiszy.

## Wymagania

- Minecraft Java **26.3**
- Fabric Loader **0.20.1** lub nowszy
- Fabric API **0.166.2+26.3**
- Java **25**
- Gradle **9.9.1** lub nowszy zgodny z Fabric Loom

## Budowanie JAR w Windows

1. Zainstaluj JDK 25 i Gradle 9.9.1 lub nowszy.
2. Upewnij się, że `JAVA_HOME` wskazuje JDK 25, a polecenie `gradle` działa w terminalu.
3. Uruchom `build.bat` w głównym folderze projektu.
4. Gotowy plik znajdziesz w `build\\libs\\autoclosecommand-1.1.0.jar`.
5. Włóż JAR do folderu `mods` swojej instancji Minecrafta. Fabric API musi być zainstalowane w tej instancji.

## Ważne

AutoCloseCommand reaguje tylko na ekran kontenera Minecrafta z dokładnie 90 slotami łącznie. Nie analizuje typów, kolorów, nazw ani pozycji przedmiotów.
