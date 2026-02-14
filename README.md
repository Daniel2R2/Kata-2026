# Kata Contact List Automation

Suite de automatizacion UI + API para Contact List App con Serenity BDD, Cucumber, Selenium Screenplay y Rest Assured.

## Requisitos
- Java 17
- Maven 3.9+
- Google Chrome

## Credenciales de autenticacion
- Por defecto, el proyecto lee:
  - `auth.email`
  - `auth.password`
- Estas propiedades estan definidas en `src/test/resources/serenity.conf`.
- Puedes sobreescribirlas por consola en cualquier ejecucion:
  - `mvn --% clean verify -Dauth.email="tu_correo@dominio.com" -Dauth.password="tu_password"`

## Ejecucion (PowerShell)
- Suite completa:
  - `mvn --% clean verify`
- Smoke:
  - `mvn --% clean verify -Dcucumber.filter.tags="@smoke"`
- Regresion:
  - `mvn --% clean verify -Dcucumber.filter.tags="@regression"`
- Negativos:
  - `mvn --% clean verify -Dcucumber.filter.tags="@negative"`
- Solo API:
  - `mvn --% clean verify -Dcucumber.filter.tags="@api"`
- Solo UI:
  - `mvn --% clean verify -Dcucumber.filter.tags="@ui"`
- API negativos:
  - `mvn --% clean verify -Dcucumber.filter.tags="@api and @negative"`
- Suite de bugs conocidos (no incluida por defecto):
  - `mvn --% clean verify -Dcucumber.filter.tags="@bug and @known_bug"`

## Reporte Serenity
- Ruta del reporte:
  - `target/site/serenity/index.html`
- Abrir desde PowerShell:
  - `Invoke-Item .\target\site\serenity\index.html`

## Evidencia API en Serenity
- Modo por defecto:
  - Se adjunta evidencia de escenarios fallidos.
- Modo evidencia completa:
  - `mvn --% clean verify -Dcucumber.filter.tags="@api" -Dapi.evidence.mode=always`

## Politica de ejecucion por defecto
- La suite normal excluye:
  - `@ignore`
  - `@bug`
  - `@known_bug`

