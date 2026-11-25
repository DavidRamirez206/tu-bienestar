
# language: es
Característica: HU1 Iniciar sesión

  Escenario: Inicio de sesión exitoso
    Dado que tengo una cuenta institucional registrada
    Cuando ingreso mi correo y mi contraseña correctos
    Entonces accedo a mi panel de inicio

  Escenario: Credenciales incorrectas
    Dado que tengo una cuenta institucional registrada
    Cuando ingreso una contraseña incorrecta
    Entonces veo el mensaje "Usuario o contraseña incorrectos"
    Y el mensaje no indica cuál de los dos datos falló

  Escenario: Usuario sin cuenta institucional
    Dado que no tengo una cuenta registrada en la plataforma
    Cuando intento iniciar sesión
    Entonces el sistema me niega el acceso
