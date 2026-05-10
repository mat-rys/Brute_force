# Brute Force Algorithm Analyzer - Instrukcja

Projekt jest pełną aplikacją webową Spring Boot umożliwiającą analizę i benchmark algorytmu Brute Force dla różnych rozkładów danych.

## Wymagania
- Java 17 lub nowsza
- Maven (załączony `mvnw`)

## Uruchomienie projektu

1. Otwórz terminal w katalogu głównym projektu.
2. Zbuduj projekt:
   ```bash
   ./mvnw clean install
   ```
3. Uruchom aplikację:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Otwórz przeglądarkę i przejdź pod adres:
   ```
   http://localhost:8080
   ```

## Funkcje aplikacji
- **Konfiguracja testów**: Ustawianie długości tekstu, wzorca, ziarna RNG oraz liczby powtórzeń.
- **Rozkłady danych**: Obsługa wielu przypadków (Losowy, Najlepszy, Najgorszy, Częściowe dopasowania, Periodyczny, Adversarial).
- **Dashboard**: Interaktywne wykresy (Chart.js) czasu wykonania i liczby porównań.
- **Eksport**: Możliwość pobrania wyników w formatach CSV oraz JSON.
- **Analiza**: Strona z pseudokodem i analizą złożoności teoretycznej.

## Struktura projektu
- `algorithm/`: Implementacja algorytmu Brute Force.
- `controller/`: Obsługa żądań HTTP.
- `service/`: Logika benchmarków i generatorów danych.
- `model/`: Klasy danych i konfiguracji.
- `templates/`: Widoki Thymeleaf.
- `static/`: Style CSS i skrypty.
