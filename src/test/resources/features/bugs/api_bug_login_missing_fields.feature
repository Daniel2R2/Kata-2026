@bug @known_bug @api
Feature: Bugs conocidos de API - comportamiento de login

  # BUG-API-001
  # La API puede devolver 400 o 401 para payload incompleto en /users/login.
  Scenario: Login con campos faltantes debe responder con error controlado
    Given que existe un usuario registrado por API
    When intenta autenticarse por API con campos faltantes
    Then la respuesta de login con campos faltantes debe ser 400 o 401
