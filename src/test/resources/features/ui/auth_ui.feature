@ui @regression
Feature: UI authentication

  @smoke
  Scenario: Login exitoso y se ve lista de contactos
    Given que el usuario esta en la pantalla de inicio
    When inicia sesion con credenciales validas
    Then debe visualizar la lista de contactos

  @negative
  Scenario: Login falla y permanece en login o muestra error
    Given que el usuario esta en la pantalla de inicio
    When inicia sesion con credenciales invalidas
    Then debe permanecer en login o mostrar error

  @negative
  Scenario: Login valida campos requeridos
    Given que el usuario esta en la pantalla de inicio
    When intenta iniciar sesion sin completar credenciales
    Then debe permanecer en login o mostrar error
