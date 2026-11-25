# language: es
Característica: HU17 Registrarse con correo institucional

  Escenario: Registro exitoso
    Dado que tengo un correo @icesi.edu.co o @u.icesi.edu.co
    Cuando me registro con mis datos completos
    Entonces mi cuenta queda creada con el rol de usuario
    Y puedo iniciar sesión

  Escenario: Correo no institucional
    Dado que tengo un correo que no es de Icesi
    Cuando intento registrarme
    Entonces el sistema rechaza el registro y explica el motivo

  Escenario: Correo ya registrado
    Dado que ya existe una cuenta con mi correo
    Cuando intento registrarme de nuevo
    Entonces veo un mensaje de que el correo ya está registrado
