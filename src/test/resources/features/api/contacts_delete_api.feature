@api @regression
Feature: Eliminar contacto por API

  @smoke
  Scenario: Crear y eliminar un contacto exitosamente
    Given que el usuario se autentica por API y obtiene un token valido
    When crea un contacto con datos validos por API
    And elimina el contacto creado por API
    Then la respuesta de eliminar contacto debe ser 200 or 204

  @smoke
  Scenario: Delete contacto y luego get devuelve 404
    Given que el usuario se autentica por API y obtiene un token valido
    And crea un contacto con datos validos por API
    When elimina el contacto creado por API
    And consulta por API el contacto eliminado por id
    Then la respuesta de consultar contacto eliminado debe ser 404
