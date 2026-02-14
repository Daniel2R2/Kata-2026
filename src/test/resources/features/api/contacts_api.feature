@api @regression
Feature: API contacts

  @smoke
  Scenario: Login -> create contact -> list contains contact
    Given que el usuario se autentica por API y obtiene un token valido
    When crea un contacto con datos validos por API
    Then el contacto debe aparecer en el listado

  @negative
  Scenario: create contact sin token retorna 401
    When intenta crear un contacto por API sin token
    Then la respuesta de crear contacto sin token debe ser 401

  @smoke
  Scenario: GET contacto por id retorna 200 y payload coincide
    Given que el usuario se autentica por API y obtiene un token valido
    And crea un contacto con datos validos por API
    When consulta por API el contacto creado por id
    Then la respuesta del get contacto debe ser 200
    And el payload del contacto consultado debe coincidir con los datos creados

  @smoke
  Scenario: Update contacto por PUT retorna 200 y aplica cambios
    Given que el usuario se autentica por API y obtiene un token valido
    And crea un contacto con datos validos por API
    When actualiza por API el contacto creado con datos nuevos usando PUT
    Then la respuesta de actualizar contacto debe ser 200
    And los cambios del contacto deben persistir por API

  @smoke
  Scenario: Update contacto por PATCH retorna 200 y aplica cambios
    Given que el usuario se autentica por API y obtiene un token valido
    And crea un contacto con datos validos por API
    When actualiza por API el contacto creado con datos nuevos usando PATCH
    Then la respuesta de actualizar contacto debe ser 200
    And los cambios del contacto deben persistir por API

  @negative
  Scenario: Update de contacto con id inexistente retorna 404
    Given que el usuario se autentica por API y obtiene un token valido
    When intenta actualizar por API un contacto inexistente
    Then la respuesta de actualizar contacto inexistente debe ser 404

  @negative
  Scenario: Create contacto con payload incompleto retorna 400
    Given que el usuario se autentica por API y obtiene un token valido
    When intenta crear un contacto por API con payload incompleto
    Then la respuesta de crear contacto incompleto debe ser 400
