
# language: es
Característica: HU3 Base de datos de ejercicios

  Escenario: Ver detalle de un ejercicio
    Dado que estoy en el catálogo de ejercicios
    Cuando abro un ejercicio
    Entonces veo su nombre, tipo, descripción, duración y dificultad

  Escenario: Ver video demostrativo
    Dado que un ejercicio tiene video
    Cuando abro su detalle
    Entonces el video se reproduce correctamente

  Escenario: Filtrar el catálogo
    Dado que estoy en el catálogo de ejercicios
    Cuando filtro por tipo y por dificultad
    Entonces solo veo los ejercicios que cumplen ambos filtros
