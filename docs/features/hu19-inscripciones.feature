# language: es
Característica: HU19 Inscribirme o cancelar una actividad

  Escenario: Inscribirse
    Dado que veo una actividad disponible
    Cuando me inscribo
    Entonces la actividad aparece en "Mis actividades"

  Escenario: Cancelar inscripción
    Dado que estoy inscrito en una actividad
    Cuando cancelo la inscripción
    Entonces la actividad sale de "Mis actividades"

  Escenario: Inscripción duplicada
    Dado que ya estoy inscrito en una actividad
    Cuando intento inscribirme otra vez
    Entonces el sistema no crea una segunda inscripción
