@ui @regression
Feature: UI signup

  @smoke
  Scenario: Signup exitoso con email unico
    Given que el usuario esta en la pantalla de registro
    When se registra por UI con un email unico
    Then debe visualizar la lista de contactos

  @negative
  Scenario: Signup con email existente muestra error
    Given que existe un usuario ya registrado para signup UI
    And que el usuario esta en la pantalla de registro
    When intenta registrarse por UI con un email ya existente
    Then debe permanecer en signup o mostrar error
