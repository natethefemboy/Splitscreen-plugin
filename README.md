# SplitscreenHandler Plugin

Ein Minecraft Paper Plugin, das Splitscreen-Clients (über Geyser + Floodgate) erlaubt, mehrere Sub-Accounts gleichzeitig zu verwenden.

## Features
- Erkennt Bedrock Splitscreen-Spieler (Xbox, Switch)
- Erstellt automatisch Sub-Accounts (`Spieler#1`, `Spieler#2`, …)
- Jeder Sub-Account hat eigenes Inventar, Position und Steuerung
- Controller-Input wird pro Controller auf den entsprechenden Sub-Account gemappt
- Speichert Konsolenspieler-Daten in `players.yml`
- Reload-Befehl `/splitscreen reload`

## Voraussetzungen
- PaperMC 1.20.4 oder kompatibel
- GeyserMC + Floodgate
- ProtocolLib

## Installation
1. Lade die JAR herunter und platziere sie in `/plugins`.
2. Starte den Server.
3. Passe die `config.yml` an:
```yaml
extra-players: 2
debug: true
