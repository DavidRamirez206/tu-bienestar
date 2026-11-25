# language: es
Característica: HU9.2 Panel administrativo: asignar entrenadores

  Escenario: Asignar entrenador
    Dado que inicié sesión como administrador
    Cuando asigno un entrenador a un usuario
    Entonces el usuario aparece en "Mis alumnos" de ese entrenador

  Escenario: Reasignar entrenador
    Dado que un usuario tiene un entrenador asignado
    Cuando lo reasigno a otro entrenador
    Entonces el nuevo entrenador lo ve en "Mis alumnos"
    Y el entrenador anterior deja de verlo

  Escenario: Asignación duplicada
    Dado que un usuario ya está asignado a un entrenador
    Cuando intento asignarlo de nuevo al mismo entrenador
    Entonces el sistema no crea una asignación duplicada
