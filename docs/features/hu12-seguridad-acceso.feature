# language: es
Característica: HU12 Seguridad y control de acceso

  Escenario: Acceso restringido por rol
    Dado que inicié sesión con el rol de usuario
    Cuando intento entrar a una función de entrenador o de administrador
    Entonces el sistema me lo impide
    Y no veo esas opciones en el menú

  Escenario: Ruta protegida sin sesión
    Dado que no he iniciado sesión
    Cuando intento abrir una página protegida
    Entonces el sistema me redirige al inicio de sesión

  Escenario: Contraseñas y datos protegidos
    Dado que un usuario se registra con su contraseña
    Cuando la cuenta se guarda
    Entonces la contraseña queda almacenada con hash
    Y sus datos personales solo los ve él o un rol con permiso
