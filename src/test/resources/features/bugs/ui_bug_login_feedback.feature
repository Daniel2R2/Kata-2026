@bug @known_bug @ui
Feature: Bugs conocidos de UI - feedback de autenticacion

  # BUG-UI-001
  # En algunas ejecuciones el mensaje de error de login no se renderiza de forma consistente.
  Scenario: Login invalido debe mantener contexto de error para el usuario
    Given que el usuario esta en la pantalla de inicio
    When inicia sesion con credenciales invalidas
    Then debe permanecer en login o mostrar error
