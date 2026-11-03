# language: es
Característica: HU18 Ver y editar mi perfil

  Escenario: Ver perfil
    Dado que inicié sesión
    Cuando abro mi perfil
    Entonces veo mi nombre, apellido, correo institucional y edad

  Escenario: Editar perfil
    Dado que estoy en mi perfil
    Cuando edito mis datos y guardo
    Entonces los cambios quedan guardados

  Escenario: Perfil ajeno
    Dado que inicié sesión con el rol de usuario
    Cuando intento ver o editar el perfil de otra persona
    Entonces el sistema me lo impide
