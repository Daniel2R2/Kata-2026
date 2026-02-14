@api @regression
Feature: Sesion y ciclo de usuario por API

  @smoke
  Scenario: Logout por API retorna 200
    Given que el usuario se autentica por API y obtiene un token valido
    When cierra sesion por API
    Then la respuesta de logout debe ser 200

  @smoke
  Scenario: Delete user por API retorna 200
    Given que el usuario se autentica por API y obtiene un token valido
    When elimina su usuario actual por API
    Then la respuesta de eliminar usuario actual debe ser 200
