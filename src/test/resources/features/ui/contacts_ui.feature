@ui @regression
Feature: UI contacts

  @smoke
  Scenario: crear contacto y verlo en listado
    Given que el usuario registrado inicia sesion en la aplicacion
    When crea un nuevo contacto
    Then debe visualizar el contacto en el listado

  @smoke
  Scenario: ver detalle de contacto
    Given que el usuario registrado inicia sesion en la aplicacion
    And crea un nuevo contacto
    When abre el detalle del contacto creado
    Then debe visualizar el detalle del contacto creado

  @regression
  Scenario: crear contacto con todos los campos y validar detalle completo
    Given que el usuario registrado inicia sesion en la aplicacion
    When crea un nuevo contacto con todos los campos del formulario
    And abre el detalle del contacto creado
    Then debe visualizar el detalle del contacto creado

  @smoke
  Scenario: editar contacto y validar persistencia
    Given que el usuario registrado inicia sesion en la aplicacion
    And crea un nuevo contacto
    And abre el detalle del contacto creado
    When edita el contacto creado con datos nuevos
    Then debe visualizar el detalle del contacto editado

  @smoke
  Scenario: eliminar contacto y validar que desaparece
    Given que el usuario registrado inicia sesion en la aplicacion
    And crea un nuevo contacto
    And abre el detalle del contacto creado
    When elimina el contacto desde el detalle
    Then no debe visualizar el contacto eliminado en el listado

  @negative
  Scenario: validaciones required al crear contacto
    Given que el usuario registrado inicia sesion en la aplicacion
    When intenta guardar un contacto sin campos requeridos
    Then debe permanecer en crear contacto o mostrar error
