Sparkel [![Build](https://github.com/navikt/helse-sparkelapper/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/helse-sparkelapper/actions/workflows/build.yml)
=======

Mikrotjenester som svarer ut behov ved å hente data fra ulike registre.

Dette er for øvrig et skikkelig bra sted å skrive navnene på hver tjeneste og en one-liner om hva de er ment å gjøre. Og så kan man bruke README for hver tjeneste for å beskrive nøyere.

## Legge til en ny gradlemodul

1. Lag en mappe og sørg for at det finnes en `build.gradle.kts` der

## Legge til ny app

Alle gradlemodulene bygges og releases automatisk. 
Ved hver pakke som blir lastet opp trigges en deployment workflow for den pakken.

Navnet på appen prefikses med `sparkel-` i nais.yml, slik at navnet på 
modulen skal være uten.

1. Gjør 'Legge til en ny gradlemodul'. Mappenavnet korresponderer med appnavnet
2. Lag `config/[app]/[cluster].yml` for de klustrene appen skal deployes til. 
3. Push endringene

## Oppgradering av gradle wrapper
Finn nyeste versjon av gradle her: https://gradle.org/releases/

```./gradlew wrapper --gradle-version $gradleVersjon```

Husk å oppdatere gradle versjonen i build.gradle.kts filen
```val gradlewVersion = "$gradleVersjon"```

## Henvendelser
Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub.

### For NAV-ansatte
Interne henvendelser kan sendes via Slack i kanalen #team-bømlo-værsågod
