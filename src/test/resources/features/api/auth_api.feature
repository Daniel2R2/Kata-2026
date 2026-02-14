@api @regression
Feature: API authentication

  @smoke
  Scenario: Signup por API exitoso
    When registra un usuario nuevo por API
    Then la respuesta de signup por API debe ser 201
    And el usuario creado por API debe tener email generado

  @smoke
  Scenario: Login por API retorna token no vacio
    Given que el usuario se autentica por API y obtiene un token valido
    Then el token de autenticacion no debe estar vacio

  @negative
  Scenario: Login invalido retorna 401 o 400
    When intenta autenticarse por API con credenciales invalidas
    Then la respuesta de login invalido debe ser 400 o 401

  @negative
  Scenario: Login con campos faltantes retorna 400 o 401
    Given que existe un usuario registrado por API
    When intenta autenticarse por API con campos faltantes
    Then la respuesta de login con campos faltantes debe ser 400 o 401
